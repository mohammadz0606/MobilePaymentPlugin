import 'package:flutter/material.dart';

class CustomDropdownButton extends StatelessWidget {
  const CustomDropdownButton({
    super.key,
    required this.selectedTextValue,
    required this.items,
    this.onChanged,
  });

  final dynamic selectedTextValue;
  final List<DropdownMenuItem> items;
  final Function(dynamic)? onChanged;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 15),
      child: DropdownButton(
        iconSize: 27,
        isExpanded: true,
        value: selectedTextValue,
        iconEnabledColor: Colors.black,
        iconDisabledColor: Colors.black,
        padding: EdgeInsets.zero,
        items: items,
        onChanged: onChanged,
      ),
    );
  }
}
