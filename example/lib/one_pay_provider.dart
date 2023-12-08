import 'dart:developer' as log;
import 'package:flutter/material.dart';
import 'package:sts_one_pay/models/error_sts_one_pay.dart';
import 'package:sts_one_pay/models/other_api.dart';
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

  Future<void> openPaymentPage({
    required Function(String code, String error) onError,
  }) async {
    try {
      await _methodChannelStsOnePay.openPaymentPage(
        StsOnePay(
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
    } on ErrorStsOnePay catch (e) {
      log.log(e.code.toString());
      log.log(e.message);
      onError(e.code.toString(), e.message);
    } catch (e) {
      log.log(e.toString());
    }
  }

  Future<void> refund({
    required Function(String code, String error) onError,
  }) async {
    try {
      await _methodChannelStsOnePay.refund(
        OtherAPI(
          amount: amountOtherAPI,
          currencyISOCode: currencyOtherAPI,
          originalTransactionID: originalTransactionID,
        ),
      );
    } on ErrorStsOnePay catch (e) {
      log.log(e.code.toString());
      log.log(e.message);
      onError(e.code.toString(), e.message);
    } catch (e) {
      log.log(e.toString());
    }
  }

  Future<void> completion({
    required Function(String code, String error) onError,
  }) async {
    try {
      await _methodChannelStsOnePay.completion(
        OtherAPI(
          amount: amountOtherAPI,
          currencyISOCode: currencyOtherAPI,
          originalTransactionID: originalTransactionID,
        ),
      );
    } on ErrorStsOnePay catch (e) {
      log.log(e.code.toString());
      log.log(e.message);
      onError(e.code.toString(), e.message);
    } catch (e) {
      log.log(e.toString());
    }
  }

  Future<void> inquiry({
    required Function(String code, String error) onError,
  }) async {
    try {
      await _methodChannelStsOnePay.inquiry(
        OtherAPI(
          amount: amountOtherAPI,
          currencyISOCode: currencyOtherAPI,
          originalTransactionID: originalTransactionID,
        ),
      );
    } on ErrorStsOnePay catch (e) {
      log.log(e.code.toString());
      log.log(e.message);
      onError(e.code.toString(), e.message);
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
    isCardScanEnable = value;
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
