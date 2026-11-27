import 'dart:io' show Platform;
import 'package:flutter/foundation.dart' show kIsWeb;

class AppConfig {
  static const String _localIp = '192.168.1.15'; 
  static const int _port = 9090;

  static String get apiBaseUrl {
    if (kIsWeb) return 'http://localhost:$_port';
    if (Platform.isAndroid) return 'http://10.0.2.2:$_port';
    if (Platform.isIOS) return 'http://localhost:$_port';
    return 'http://localhost:$_port';
  }
}