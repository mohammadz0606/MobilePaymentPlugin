import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:sts_one_pay/models/sts_one_pay.dart';

import '../one_pay_provider.dart';
import 'dropdown_button.dart';
import 'note_text.dart';
import 'text_field.dart';

class PaymentFields extends StatelessWidget {
  const PaymentFields({super.key});

  @override
  Widget build(BuildContext context) {
    return Consumer<PayOneProvider>(
      builder: (context, provider, child) {
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            CustomTextField(
              onChanged: (value) => provider.onChangeAmount(value),
              keyboardType:
                  const TextInputType.numberWithOptions(decimal: false),
              inputFormatters: [
                FilteringTextInputFormatter.digitsOnly,
              ],
              hintText: 'Amount',
              textInputAction: TextInputAction.next,
            ),
            const NoteText(text: '* Amount without dot'),
            CustomTextField(
              onChanged: (value) => provider.onChangeToken(value),
              keyboardType: TextInputType.multiline,
              hintText: 'Token',
              maxLines: null,
              textInputAction: TextInputAction.next,
            ),
            const NoteText(text: 'Tokens separated by comma (,) if multi'),
            CustomTextField(
              onChanged: (value) => provider.onChangeCurrency(value),
              keyboardType:
                  const TextInputType.numberWithOptions(decimal: false),
              inputFormatters: [
                FilteringTextInputFormatter.allow(
                  RegExp(r'^\d+\.?\d{0,2}'),
                ),
              ],
              hintText: 'Currency',
              textInputAction: TextInputAction.next,
            ),
            const NoteText(text: 'Currency code (Only one currency)'),
            CustomTextField(
              onChanged: (value) => provider.onChangeTransactionId(value),
              keyboardType:
                  const TextInputType.numberWithOptions(decimal: false),
              inputFormatters: [
                FilteringTextInputFormatter.digitsOnly,
              ],
              hintText: 'Transaction ID',
              textInputAction: TextInputAction.done,
            ),
            const NoteText(
              text: 'Transaction ID (Keep empty for auto generation)',
            ),
            SwitchListTile.adaptive(
              value: provider.isThreeDSSecure,
              onChanged: (value) => provider.onChangeThreeDSSecure(value),
              title: Text(
                'Is 3DS Secure',
                style: TextStyle(
                  color: Theme.of(context).primaryColor,
                ),
              ),
            ),
            SwitchListTile.adaptive(
              value: provider.shouldTokenizeCard,
              onChanged: (value) {
                provider.onChangeShouldTokenizeCard(value);
              },
              title: Text(
                'Should Tokenize Card',
                style: TextStyle(
                  color: Theme.of(context).primaryColor,
                ),
              ),
            ),
            SwitchListTile.adaptive(
              value: provider.isCardScanEnable,
              onChanged: (value) => provider.onChangeCardScanEnable(value),
              title: Text(
                'Is Card scan enable',
                style: TextStyle(
                  color: Theme.of(context).primaryColor,
                ),
              ),
            ),
            SwitchListTile.adaptive(
              value: provider.isSaveCardEnable,
              onChanged: (value) => provider.onChangeSaveCardEnable(value),
              title: Text(
                'Is Save Card Enabled',
                style: TextStyle(
                  color: Theme.of(context).primaryColor,
                ),
              ),
            ),
            CustomDropdownButton(
              selectedTextValue: provider.selectedLangVale,
              onChanged: (value) => provider.onChangeLang(value),
              items: const [
                DropdownMenuItem<Language>(
                  value: Language.ar,
                  child: Text('Arabic'),
                ),
                DropdownMenuItem<Language>(
                  value: Language.en,
                  child: Text('English'),
                ),
                DropdownMenuItem<Language>(
                  value: Language.tr,
                  child: Text('Turkish'),
                ),
              ],
            ),
            CustomDropdownButton(
              selectedTextValue: provider.selectedPaymentTypeTypeValue,
              onChanged: (value) {
                provider.onChangePaymentTypeType(value);
                log('value:');
                log(value.toString());
              },
              items: const [
                DropdownMenuItem<PaymentType>(
                  value: PaymentType.sale,
                  child: Text('Sale'),
                ),
                DropdownMenuItem<PaymentType>(
                  value: PaymentType.preAuth,
                  child: Text('PreAuth'),
                ),
              ],
            ),
            const NoteText(
              text: 'Cards Type',
            ),
            ListView.builder(
              itemCount: provider.cardsType.length,
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemBuilder: (context, index) {
                var card = provider.cardsType[index];
                return CheckboxListTile.adaptive(
                  value: provider.cardsSelected.contains(card),
                  onChanged: (value) {
                    provider.onChangedCheckboxCardsType(
                      value: value!,
                      cardType: card,
                    );
                  },
                  title: Text(card.name.toUpperCase()),
                );
              },
            ),
          ],
        );
      },
    );
  }
}
