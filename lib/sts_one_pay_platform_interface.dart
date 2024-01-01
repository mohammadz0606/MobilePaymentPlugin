import 'dart:math';

import 'package:sts_one_pay/models/sts_one_pay.dart';

import 'models/other_api.dart';

abstract class StsOnePayPlatform {
  static String generateTransactionId() {
    int timestamp = DateTime.now().millisecondsSinceEpoch;
    int random = Random().nextInt(999999);
    return (timestamp + random).toString();
  }

  Future<Map<String, String>> openPaymentPage(StsOnePay stsOnePay);

  Future<void> refund(OtherAPI otherAPI);

  Future<void> completion(OtherAPI otherAPI);

  Future<void> inquiry(OtherAPI otherAPI);
}
