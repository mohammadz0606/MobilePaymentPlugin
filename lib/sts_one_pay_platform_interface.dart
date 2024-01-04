import 'dart:math';

import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'package:sts_one_pay/models/sts_one_pay.dart';

import 'models/other_api.dart';

abstract class StsOnePayPlatform extends PlatformInterface {
  StsOnePayPlatform() : super(token: _token);

  static final Object _token = Object();

  static String generateTransactionId() {
    int timestamp = DateTime.now().millisecondsSinceEpoch;
    int random = Random().nextInt(999999);
    return (timestamp + random).toString();
  }

  Future<void> openPaymentPage(StsOnePay stsOnePay);

  Future<void> refund(OtherAPI otherAPI);

  Future<void> completion(OtherAPI otherAPI);

  Future<void> inquiry(OtherAPI otherAPI);
}
