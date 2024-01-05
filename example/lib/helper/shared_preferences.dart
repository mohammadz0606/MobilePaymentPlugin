import 'package:shared_preferences/shared_preferences.dart';

abstract class SharedPreferencesApp {
  static late SharedPreferences sharedPreferences;

  static Future<SharedPreferences> init() async =>
      sharedPreferences = await SharedPreferences.getInstance();

  static Future<bool> setArray({
    required String key,
    required List<String> array,
  }) async =>
      await sharedPreferences.setStringList(key, array);

  static List<String>? getArray({
    required String key,
  }) =>
      sharedPreferences.getStringList(key);

  static Future<bool> remove({
    required String key,
  }) async =>
      await sharedPreferences.remove(key);
}