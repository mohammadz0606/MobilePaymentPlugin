import 'package:flutter/material.dart';

class NoteText extends StatelessWidget {
  const NoteText({
    super.key,
    required this.text,
  });

  final String text;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 10.5),
      child: Text(
        text,
        style: TextStyle(
          color: Theme.of(context).primaryColor,
          fontSize: 15.5,
        ),
      ),
    );
  }
}
