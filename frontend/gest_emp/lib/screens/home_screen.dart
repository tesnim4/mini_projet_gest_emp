// lib/screens/home_screen.dart
import 'package:flutter/material.dart';
import '../models/calendar_slot.dart';
import '../services/api_service.dart';
import 'slot_form_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  late Future<List<CalendarSlot>> _slotsFuture;
  String _from = '2027-01-01';
  String _to = '2027-01-31';

  @override
  void initState() {
    super.initState();
    _loadSlots();
  }

  void _loadSlots() {
    setState(() {
      _slotsFuture = ApiService.getSlots(_from, _to);
    });
  }

  Future<void> _pickDate(bool isFrom) async {
    final date = await showDatePicker(
      context: context,
      initialDate: DateTime.parse(isFrom ? _from : _to),
      firstDate: DateTime(2020),
      lastDate: DateTime(2030),
    );
    if (date != null) {
      setState(() {
        final d = date.toIso8601String().split('T')[0];
        if (isFrom) _from = d; else _to = d;
      });
      _loadSlots();
    }
  }

  void _deleteSlot(int id, String desc) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Confirmer'),
        content: Text('Supprimer "$desc" ?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Annuler')),
          TextButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('Supprimer', style: TextStyle(color: Colors.red))),
        ],
      ),
    );

    if (confirm == true) {
      try {
        await ApiService.deleteSlot(id);
        if (!mounted) return;
        _loadSlots();
      } catch (e) {
        if (!mounted) return;
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('$e')));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('📅 Emploi du temps'),
        backgroundColor: Colors.blue,
        foregroundColor: Colors.white,
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                OutlinedButton(onPressed: () => _pickDate(true), child: Text('Du: $_from')),
                OutlinedButton(onPressed: () => _pickDate(false), child: Text('Au: $_to')),
                IconButton(icon: const Icon(Icons.refresh), onPressed: _loadSlots),
              ],
            ),
          ),
          Expanded(
            child: FutureBuilder<List<CalendarSlot>>(
              future: _slotsFuture,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                }
                if (snapshot.hasError) {
                  return Center(
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Text('Erreur: ${snapshot.error}', style: const TextStyle(color: Colors.red)),
                    ),
                  );
                }
                final slots = snapshot.data ?? [];
                if (slots.isEmpty) {
                  return const Center(child: Text('Aucun créneau sur cette période.'));
                }
                return ListView.builder(
                  itemCount: slots.length,
                  itemBuilder: (context, index) {
                    final slot = slots[index];
                    return Card(
                      margin: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                      child: ListTile(
                        title: Text(slot.description, style: const TextStyle(fontWeight: FontWeight.bold)),
                        subtitle: Text(
                          '${slot.timeInterval.start.replaceAll('T', ' ')} -> ${slot.timeInterval.end.replaceAll('T', ' ')}',
                        ),
                        trailing: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            IconButton(
                              icon: const Icon(Icons.edit, color: Colors.blue),
                              onPressed: () async {
                                final refreshed = await Navigator.push(
                                  context,
                                  MaterialPageRoute(builder: (_) => SlotFormScreen(slot: slot)),
                                );
                                if (refreshed == true) _loadSlots();
                              },
                            ),
                            IconButton(
                              icon: const Icon(Icons.delete, color: Colors.red),
                              onPressed: () => _deleteSlot(slot.id, slot.description),
                            ),
                          ],
                        ),
                      ),
                    );
                  },
                );
              },
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        backgroundColor: Colors.blue,
        child: const Icon(Icons.add, color: Colors.white),
        onPressed: () async {
          final refreshed = await Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => const SlotFormScreen()),
          );
          if (refreshed == true) _loadSlots();
        },
      ),
    );
  }
}