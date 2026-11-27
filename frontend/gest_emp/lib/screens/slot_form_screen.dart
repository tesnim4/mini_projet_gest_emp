// lib/screens/slot_form_screen.dart
import 'package:flutter/material.dart';
import '../models/calendar_slot.dart';
import '../services/api_service.dart';

class SlotFormScreen extends StatefulWidget {
  final CalendarSlot? slot; // Null = Création, non-null = Modification
  const SlotFormScreen({super.key, this.slot});

  @override
  State<SlotFormScreen> createState() => _SlotFormScreenState();
}

class _SlotFormScreenState extends State<SlotFormScreen> {
  final _formKey = GlobalKey<FormState>();
  late TextEditingController _descCtrl;
  DateTime? _start;
  DateTime? _end;

  @override
  void initState() {
    super.initState();
    _descCtrl = TextEditingController(text: widget.slot?.description ?? '');
    if (widget.slot != null) {
      _start = DateTime.tryParse(widget.slot!.timeInterval.start);
      _end = DateTime.tryParse(widget.slot!.timeInterval.end);
    }
  }

  Future<void> _selectDateTime(bool isStart) async {
    final initialDate = isStart ? (_start ?? DateTime.now()) : (_end ?? DateTime.now());
    final date = await showDatePicker(
      context: context,
      initialDate: initialDate,
      firstDate: DateTime(2020),
      lastDate: DateTime(2030),
    );
    if (date == null) return;
    if (!mounted) return;

    final time = await showTimePicker(
      context: context,
      initialTime: TimeOfDay.fromDateTime(initialDate),
    );
    if (time == null) return;

    final picked = DateTime(date.year, date.month, date.day, time.hour, time.minute);
    setState(() {
      if (isStart) _start = picked; else _end = picked;
    });
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    if (_start == null || _end == null) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Veuillez remplir les dates')));
      return;
    }
    if (_end!.isBefore(_start!)) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('La fin doit être après le début')));
      return;
    }

    final newSlot = CalendarSlot(
      id: widget.slot?.id ?? 0,
      description: _descCtrl.text,
      timeInterval: TimeInterval(
        start: _start!.toIso8601String(),
        end: _end!.toIso8601String(),
      ),
      version: widget.slot?.version ?? 1,
    );

    try {
      if (widget.slot == null) {
        await ApiService.createSlot(newSlot);
      } else {
        await ApiService.updateSlot(newSlot);
      }
      if (!mounted) return;
      Navigator.pop(context, true); // Retourne true pour rafraichir l'écran précédent
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('$e')));
    }
  }

  @override
  Widget build(BuildContext context) {
    final isEdit = widget.slot != null;
    return Scaffold(
      appBar: AppBar(
        title: Text(isEdit ? 'Modifier le créneau' : 'Nouveau créneau'),
        backgroundColor: Colors.blue,
        foregroundColor: Colors.white,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              TextFormField(
                controller: _descCtrl,
                decoration: const InputDecoration(labelText: 'Description', border: OutlineInputBorder()),
                validator: (v) => (v == null || v.trim().isEmpty) ? 'Champ obligatoire' : null,
              ),
              const SizedBox(height: 16),
              ListTile(
                title: Text(_start == null ? 'Choisir le début' : 'Début: ${_start.toString().substring(0, 16)}'),
                trailing: const Icon(Icons.calendar_today, color: Colors.blue),
                onTap: () => _selectDateTime(true),
                tileColor: Colors.grey[200],
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(4)),
              ),
              const SizedBox(height: 16),
              ListTile(
                title: Text(_end == null ? 'Choisir la fin' : 'Fin: ${_end.toString().substring(0, 16)}'),
                trailing: const Icon(Icons.calendar_today, color: Colors.blue),
                onTap: () => _selectDateTime(false),
                tileColor: Colors.grey[200],
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(4)),
              ),
              const SizedBox(height: 32),
              ElevatedButton(
                onPressed: _submit,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.blue, 
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16)
                ),
                child: Text(isEdit ? 'Enregistrer les modifications' : 'Créer le créneau'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}