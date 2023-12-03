import 'dart:developer';

import 'package:flutter/services.dart';
import 'package:sts_one_pay/sts_one_pay.dart';

import 'sts_one_pay_platform_interface.dart';

class MethodChannelStsOnePay extends StsOnePayPlatform {
  final MethodChannel _channel =
      const MethodChannel('samples.flutter.dev/payment');

  @override
  Future<void> openPaymentPage(StsOnePay stsOnePay) async {
    try {
      await _channel.invokeMethod(
        'paymentMethod',
        stsOnePay.toJson(),
      );
    } on PlatformException catch (e) {
      log(e.message.toString());
    } catch (e) {
      log(e.toString());
    }
  }

  @override
  Future<void> refund() async {
    try {
      await _channel.invokeMethod('refund');
    } on PlatformException catch (e) {
      log(e.message.toString());
    } catch (e) {
      log(e.toString());
    }
  }
}
