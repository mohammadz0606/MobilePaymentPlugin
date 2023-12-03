import 'dart:developer' as log;
import 'package:flutter/material.dart';
import 'package:sts_one_pay/sts_one_pay.dart';
import 'package:sts_one_pay/sts_one_pay_platform_interface.dart';
import 'package:sts_one_pay/sts_one_pay_method_channel.dart';

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
  Language selectedLangVale = Language.ar;
  PaymentType selectedPaymentTypeTypeValue = PaymentType.sale;

  final StsOnePayPlatform _methodChannelStsOnePay = MethodChannelStsOnePay();

  Future<void> openPaymentPage() async {
    try {
      await _methodChannelStsOnePay.openPaymentPage(
        StsOnePay(
          authenticationToken: 'MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh',
          merchantID: 'AirrchipMerchant',
          amount: amount,
          tokens: tokensList,
          currency: currency,
          transactionId: transactionId,
          isThreeDSSecure: isThreeDSSecure,
          shouldTokenizeCard: shouldTokenizeCard,
          isCardScanEnable: isCardScanEnable,
          isSaveCardEnable: isSaveCardEnable,
          langCode: selectedLangVale,
          paymentType: selectedPaymentTypeTypeValue,
        ),
      );
    } catch (e) {
      log.log(e.toString());
    }
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

  void onChangeLang(Language value) {
    selectedLangVale = value;
    notifyListeners();
  }

  void onChangePaymentTypeType(PaymentType value) {
    selectedPaymentTypeTypeValue = value;
    notifyListeners();
  }
}
