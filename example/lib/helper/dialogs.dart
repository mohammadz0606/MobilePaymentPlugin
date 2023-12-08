import 'package:flutter/material.dart';

Future<void> showCustomDialog(
  BuildContext context, {
  required String title,
  required String description,
}) async {
  return await showDialog(
    context: context,
    useSafeArea: true,
    builder: (context) {
      return AlertDialog(
        title: Text(title),
        titleTextStyle: const TextStyle(
          color: Colors.red,
          fontWeight: FontWeight.w600,
          fontSize: 20,
        ),
        content: Text(description),
        actionsPadding: const EdgeInsets.symmetric(horizontal: 5, vertical: 10),
      );
    },
  );
}
