import 'dart:developer' as log;
import 'package:flutter/material.dart';
import 'package:sts_one_pay/models/sts_one_pay.dart';
import 'package:sts_one_pay/sts_one_pay_platform_interface.dart';
import 'package:sts_one_pay/sts_one_pay_method_channel.dart';

class PayOneProvider extends ChangeNotifier {
  String amount = '';
  String amountOtherAPI = '';
  String tokensText = '';
  String currency = '';
  String currencyOtherAPI = '';
  String transactionId = '';
  String originalTransactionID = '';
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

  Future<void> refund() async {
    try {
     // await _methodChannelStsOnePay.refund();
    } catch (e) {
      log.log(e.toString());
    }
  }

  Future<void> completion() async {
    try {
      //await _methodChannelStsOnePay.completion();
    } catch (e) {
      log.log(e.toString());
    }
  }

  Future<void> inquiry() async {
    try {
    //  await _methodChannelStsOnePay.inquiry();
    } catch (e) {
      log.log(e.toString());
    }
  }

  void onChangeAmount(String value) {
    amount = value.trim();
    notifyListeners();
  }

  void onChangeAmountOtherAPI(String value) {
    amountOtherAPI = value.trim();
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

  void onChangeCurrencyOtherAPI(String value) {
    currencyOtherAPI = value.trim();
    notifyListeners();
  }

  void onChangeTransactionId(String value) {
    transactionId = value.trim();
    notifyListeners();
  }

  void onChangeOriginalTransactionID(String value) {
    originalTransactionID = value.trim();
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
