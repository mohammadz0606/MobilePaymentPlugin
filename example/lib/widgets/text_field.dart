import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class CustomTextField extends StatelessWidget {
  const CustomTextField({
    super.key,
    this.hintText,
    this.validator,
    this.prefixIcon,
    this.controller,
    this.maxLength,
    this.onChanged,
    required this.keyboardType,
    this.obscureText,
    this.focusNode,
    this.textInputAction,
    this.onEditingComplete,
    this.autofocus,
    this.suffixIcon,
    this.readOnly,
    this.inputFormatters,
    this.suffixText,
    this.maxLines,
  });

  final String? hintText;
  final FormFieldValidator? validator;
  final Widget? prefixIcon;
  final IconButton? suffixIcon;
  final TextEditingController? controller;
  final int? maxLength;
  final ValueChanged<String>? onChanged;
  final TextInputType keyboardType;
  final bool? obscureText;
  final FocusNode? focusNode;
  final TextInputAction? textInputAction;
  final VoidCallback? onEditingComplete;
  final bool? autofocus;
  final bool? readOnly;
  final List<TextInputFormatter>? inputFormatters;
  final String? suffixText;
  final int? maxLines;

  @override
  Widget build(BuildContext context) {
    return TextFormField(
      readOnly: readOnly ?? false,
      inputFormatters: inputFormatters,
      focusNode: focusNode,
      cursorColor: Theme.of(context).primaryColor,
      enableInteractiveSelection: true,
      autofocus: autofocus ?? false,
      textInputAction: textInputAction,
      onEditingComplete: onEditingComplete,
      keyboardType: keyboardType,
      validator: validator,
      controller: controller,
      maxLength: maxLength,
      maxLines: maxLines,
      onTapOutside: (event) {
        FocusManager.instance.primaryFocus?.unfocus();
      },
      obscureText: obscureText ?? false,
      decoration: InputDecoration(
        hintStyle: const TextStyle(
          fontSize: 14,
        ),
        prefixIcon: prefixIcon,
        suffixIcon: suffixIcon,
        suffixIconColor: Theme.of(context).iconTheme.color,
        prefixIconColor: Theme.of(context).iconTheme.color,
        suffixText: suffixText,
        suffixStyle: TextStyle(
          color: Theme.of(context).textTheme.bodySmall!.color,
        ),
        counterStyle: TextStyle(
          color: Theme.of(context).brightness == Brightness.light
              ? Colors.black
              : Colors.white,
        ),
        disabledBorder: const OutlineInputBorder(
          borderSide: BorderSide(
            color: Colors.grey,
          ),
        ),
        enabledBorder: OutlineInputBorder(
          borderSide: BorderSide(
            color: Theme.of(context).primaryColor,
          ),
        ),
        border: OutlineInputBorder(
          borderSide: BorderSide(
            color: Theme.of(context).primaryColor,
          ),
        ),
        errorBorder: OutlineInputBorder(
          borderSide: BorderSide(
            color: Theme.of(context).primaryColor,
          ),
        ),
        enabled: true,
        focusedBorder: OutlineInputBorder(
          borderSide: BorderSide(
            color: Theme.of(context).primaryColor,
          ),
        ),
        hintText: hintText,
      ),
      textAlign: TextAlign.left,
      onChanged: onChanged,
      textDirection: TextDirection.ltr,
    );
  }
}
