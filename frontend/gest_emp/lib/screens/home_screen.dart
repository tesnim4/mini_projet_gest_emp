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

  DateTime? _from;
  DateTime? _to;

  @override
  void initState() {
    super.initState();
    _loadSlots();
  }

  
  String _formatDate(DateTime date) {
    return '${date.year}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')}';
  }

  void _loadSlots() {
    
    final fromStr = _from != null ? _formatDate(_from!) : '2000-01-01';
    final toStr = _to != null ? _formatDate(_to!) : '2100-12-31';

    setState(() {
      _slotsFuture = ApiService.getSlots(fromStr, toStr);
    });
  }

  Future<void> _pickDate(bool isFrom) async {
    // On ouvre le calendrier à la date actuelle si aucune date n'est choisie
    final initialDate =
        isFrom ? (_from ?? DateTime.now()) : (_to ?? DateTime.now());

    final date = await showDatePicker(
      context: context,
      initialDate: initialDate,
      firstDate: DateTime(2000),
      lastDate: DateTime(2100),
    );

    if (date != null) {
      setState(() {
        if (isFrom) {
          _from = date;
        } else {
          _to = date;
        }
      });
      _loadSlots(); // Recharge les données avec la nouvelle date
    }
  }

  void _deleteSlot(int id, String desc) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder:
          (ctx) => AlertDialog(
            title: const Text('Confirmer'),
            content: Text('Supprimer "$desc" ?'),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(ctx, false),
                child: const Text('Annuler'),
              ),
              TextButton(
                onPressed: () => Navigator.pop(ctx, true),
                child: const Text(
                  'Supprimer',
                  style: TextStyle(color: Colors.red),
                ),
              ),
            ],
          ),
    );

    if (confirm == true) {
      try {
        await ApiService.deleteSlot(id);
        _loadSlots();
      } catch (e) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('$e')));
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
                // ✅ MODIFICATION : Affichage conditionnel du texte des boutons
                OutlinedButton(
                  onPressed: () => _pickDate(true),
                  child: Text(
                    _from == null
                        ? 'Du: Choisir'
                        : 'Du: ${_formatDate(_from!)}',
                  ),
                ),
                OutlinedButton(
                  onPressed: () => _pickDate(false),
                  child: Text(
                    _to == null ? 'Au: Choisir' : 'Au: ${_formatDate(_to!)}',
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.refresh),
                  onPressed: _loadSlots,
                ),
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
                    child: Text(
                      'Erreur: ${snapshot.error}',
                      style: const TextStyle(color: Colors.red),
                    ),
                  );
                }
                final slots = snapshot.data ?? [];
                if (slots.isEmpty) {
                  return const Center(child: Text('Aucun créneau trouvé.'));
                }
                return ListView.builder(
                  itemCount: slots.length,
                  itemBuilder: (context, index) {
                    final slot = slots[index];
                    return Card(
                      margin: const EdgeInsets.symmetric(
                        horizontal: 10,
                        vertical: 5,
                      ),
                      child: ListTile(
                        title: Text(
                          slot.description,
                          style: const TextStyle(fontWeight: FontWeight.bold),
                        ),
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
                                  MaterialPageRoute(
                                    builder: (_) => SlotFormScreen(slot: slot),
                                  ),
                                );
                                if (refreshed == true) _loadSlots();
                              },
                            ),
                            IconButton(
                              icon: const Icon(Icons.delete, color: Colors.red),
                              onPressed:
                                  () => _deleteSlot(slot.id, slot.description),
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
