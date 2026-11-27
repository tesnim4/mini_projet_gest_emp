class TimeInterval {
  final String start;
  final String end;

  TimeInterval({required this.start, required this.end});

  factory TimeInterval.fromJson(Map<String, dynamic> json) {
    return TimeInterval(start: json['start'] ?? '', end: json['end'] ?? '');
  }

  Map<String, dynamic> toJson() => {'start': start, 'end': end};
}

class CalendarSlot {
  final int id;
  final String description;
  final TimeInterval timeInterval;
  final int version;

  CalendarSlot({
    required this.id,
    required this.description,
    required this.timeInterval,
    required this.version,
  });

  factory CalendarSlot.fromJson(Map<String, dynamic> json) {
    return CalendarSlot(
      id: json['id'] ?? 0,
      description: json['description'] ?? '',
      timeInterval: TimeInterval.fromJson(json['timeInterval'] ?? {}),
      version: json['version'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'description': description,
        'timeInterval': timeInterval.toJson(),
        'version': version,
      };
}