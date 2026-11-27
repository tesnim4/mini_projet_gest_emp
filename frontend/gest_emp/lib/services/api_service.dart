import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/calendar_slot.dart';
import '../config.dart';

class ApiService {
  static final String baseUrl = AppConfig.apiBaseUrl;

  static Future<List<CalendarSlot>> getSlots(String from, String to) async {
    final uri = Uri.parse('$baseUrl/timeslots/$from&$to');
    final response = await http.get(uri);

    if (response.statusCode == 200) {
      final dynamic data = jsonDecode(response.body);
      final List<dynamic> list = data is List ? data : (data['slots'] ?? data['calendarSlots'] ?? []);
      return list.map((item) => CalendarSlot.fromJson(item)).toList();
    } else {
      throw Exception('Erreur de récupération (${response.statusCode})');
    }
  }

  static Future<CalendarSlot> getSlot(int id) async {
    final uri = Uri.parse('$baseUrl/api/slots/$id');
    final response = await http.get(uri);

    if (response.statusCode == 200) {
      return CalendarSlot.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Créneau introuvable (${response.statusCode})');
    }
  }

  static Future<CalendarSlot> createSlot(CalendarSlot slot) async {
    final uri = Uri.parse('$baseUrl/api/slots');
    final response = await http.post(
      uri,
      headers: {'Content-Type': 'application/json; charset=UTF-8'},
      body: jsonEncode(slot.toJson()),
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      return CalendarSlot.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Erreur de création (${response.statusCode})');
    }
  }

  static Future<CalendarSlot> updateSlot(CalendarSlot slot) async {
    final uri = Uri.parse('$baseUrl/api/slots/${slot.id}');
    final response = await http.put(
      uri,
      headers: {'Content-Type': 'application/json; charset=UTF-8'},
      body: jsonEncode(slot.toJson()),
    );

    if (response.statusCode == 200) {
      return CalendarSlot.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Erreur de modification (${response.statusCode})');
    }
  }

  static Future<void> deleteSlot(int id) async {
    final uri = Uri.parse('$baseUrl/api/slots/$id');
    final response = await http.delete(uri);

    if (response.statusCode != 200 && response.statusCode != 204) {
      throw Exception('Erreur de suppression (${response.statusCode})');
    }
  }
}