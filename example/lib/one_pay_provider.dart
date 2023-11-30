import 'dart:developer' as log;
import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'one_pay_model.dart';

class PayOneProvider extends ChangeNotifier {
  String amount = '';
  String tokensText = '';
  String currency = '';
  String transactionId = '';
  List<String> tokensList = [];
  bool isThreeDSSecure = true;
  bool shouldTokenizeCard = true;
  bool isCardScanEnable = true;
  bool isSaveCardEnable = true;
  String selectedLangVale = 'ar';
  String selectedPaymentTypeTypeValue = 'sale';

  final MethodChannel _channel =
      const MethodChannel('samples.flutter.dev/payment');

  Future<void> openPaymentPage() async {
    try {
      OnePayModel onePayModel = OnePayModel(
        amount: amount,
        tokens: tokensList,
        currency: currency,
        transactionId:
            transactionId.isEmpty ? _generateTransactionId() : transactionId,
        isThreeDSSecure: isThreeDSSecure,
        shouldTokenizeCard: shouldTokenizeCard,
        isCardScanEnable: isCardScanEnable,
        isSaveCardEnable: isSaveCardEnable,
        langCode: selectedLangVale,
        paymentType: selectedPaymentTypeTypeValue,
      );
      await _channel.invokeMethod(
        'paymentMethod',
        onePayModel.toJson(),
      );
    } on PlatformException catch (e) {
      log.log(e.message.toString());
    }
  }

  String _generateTransactionId() {
    int timestamp = DateTime.now().millisecondsSinceEpoch;
    int random = Random().nextInt(999999);
    return (timestamp + random).toString();
  }

  void onChangeAmount(String value) {
    amount = value.trim();
    notifyListeners();
  }

  void onChangeToken(String value) {
    tokensText = value.trim();
    notifyListeners();
  }

  void onChangeCurrency(String value) {
    currency = value.trim();
    notifyListeners();
  }

  void onChangeTransactionId(String value) {
    transactionId = value.trim();
    notifyListeners();
  }

  void onChangeThreeDSSecure(bool value) {
    isThreeDSSecure = value;
    notifyListeners();
  }

  void onChangeShouldTokenizeCard(bool value) {
    shouldTokenizeCard = value;
    notifyListeners();
  }

  void onChangeCardScanEnable(bool value) {
    isSaveCardEnable = value;
    notifyListeners();
  }

  void onChangeSaveCardEnable(bool value) {
    isSaveCardEnable = value;
    notifyListeners();
  }

  void onChangeLang(String value) {
    selectedLangVale = value;
    notifyListeners();
  }

  void onChangePaymentTypeType(String value) {
    selectedPaymentTypeTypeValue = value;
    notifyListeners();
  }
}
