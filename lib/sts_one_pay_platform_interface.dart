import 'dart:math';

import 'package:sts_one_pay/models/sts_one_pay.dart';

import 'models/on_delete.dart';
import 'models/other_api.dart';
import 'models/payment_page_response.dart';

abstract class StsOnePayPlatform {
  static String generateTransactionId() {
    int timestamp = DateTime.now().millisecondsSinceEpoch;
    int random = Random().nextInt(999999);
    return (timestamp + random).toString();
  }

  Future<void> openPaymentPage(
    StsOnePay stsOnePay, {
    required Function(StsOnePayResponse result) onResultResponse,
    required Function(OnDeleteCard onDeleteCard) onDeleteCardResponse,
  });

  Future<void> refund(OtherAPI otherAPI);

  Future<void> completion(OtherAPI otherAPI);

  Future<void> inquiry(OtherAPI otherAPI);
}
