import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:sts_one_pay_example/widgets/text_field.dart';

import '../one_pay_provider.dart';

class OtherApiFields extends StatefulWidget {
  const OtherApiFields({super.key});

  @override
  State<OtherApiFields> createState() => _OtherApiFieldsState();
}

class _OtherApiFieldsState extends State<OtherApiFields> {
  @override
  Widget build(BuildContext context) {
    return Consumer<PayOneProvider>(
      builder: (context, provider, child) {
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            CustomTextField(
              onChanged: (value) => provider.onChangeAmountOtherAPI(value),
              keyboardType:
                  const TextInputType.numberWithOptions(decimal: false),
              inputFormatters: [
                FilteringTextInputFormatter.allow(
                  RegExp(r'^\d+\.?\d{0,2}'),
                ),
              ],
              hintText: 'Amount',
              textInputAction: TextInputAction.next,
            ),
          ],
        );
      },
    );
  }
}
