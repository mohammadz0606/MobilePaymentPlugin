import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:sts_one_pay_example/widgets/text_field.dart';

import '../one_pay_provider.dart';
import 'note_text.dart';

class OtherApiFields extends StatelessWidget {
  const OtherApiFields({super.key});

  @override
  Widget build(BuildContext context) {
    return Consumer<PayOneProvider>(
      builder: (context, provider, child) {
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            CustomTextField(
              onChanged: (value) => provider.onChangeOriginalTransactionID(value),
              keyboardType: const TextInputType.numberWithOptions(decimal: false),
              inputFormatters: [
                FilteringTextInputFormatter.digitsOnly,
              ],
              hintText: 'Original TransactionID',
              textInputAction: TextInputAction.next,
            ),
            const SizedBox(height: 20),
            CustomTextField(
              onChanged: (value) => provider.onChangeAmountOtherAPI(value),
              keyboardType: const TextInputType.numberWithOptions(decimal: false),
              inputFormatters: [
                FilteringTextInputFormatter.digitsOnly,
              ],
              hintText: 'Amount',
              textInputAction: TextInputAction.next,
            ),
            const NoteText(text: '* Amount without dot'),
            CustomTextField(
              onChanged: (value) => provider.onChangeCurrencyOtherAPI(value),
              keyboardType: const TextInputType.numberWithOptions(decimal: false),
              inputFormatters: [
                FilteringTextInputFormatter.digitsOnly,
              ],
              hintText: 'Currency',
              textInputAction: TextInputAction.done,
            ),
            const NoteText(text: 'Currency code (Only one currency)'),
          ],
        );
      },
    );
  }
}
