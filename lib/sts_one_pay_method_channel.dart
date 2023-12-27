import 'dart:developer';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:sts_one_pay/models/sts_one_pay.dart';

import 'models/other_api.dart';
import 'sts_one_pay_platform_interface.dart';

class MethodChannelStsOnePay extends StsOnePayPlatform {
  final MethodChannel _channel =
      const MethodChannel('samples.flutter.dev/payment');
  final MethodChannel _channelIOS =
      const MethodChannel('sts_one_pay');

  @override
  Future<void> openPaymentPage(StsOnePay stsOnePay) async {
    try {
      if (Platform.isAndroid) {
        await _channel.invokeMethod(
          'paymentMethod',
          stsOnePay.toJson(),
        );
      } else if (Platform.isIOS) {

        await _channelIOS.invokeMethod(
          'showPaymentPage',
          stsOnePay.toJson(),
        );
      } else {
        /// throw custom error
      }
    } on PlatformException catch (e) {
      log(e.message.toString());
    } catch (e) {
      log(e.toString());
    }
  }

  @override
  Future<void> refund(OtherAPI otherAPI) async {
    try {
      if (Platform.isAndroid) {
        await _channel.invokeMethod(
          'refund',
          otherAPI.toJson(),
        );
      } else if (Platform.isIOS) {
        /// implement ios method
      } else {
        /// throw custom error
      }
    } on PlatformException catch (e) {
      log(e.message.toString());
    } catch (e) {
      log(e.toString());
    }
  }

  @override
  Future<void> completion(OtherAPI otherAPI) async {
    try {
      if (Platform.isAndroid) {
        await _channel.invokeMethod(
          'completion',
          otherAPI.toJson(),
        );
      } else if (Platform.isIOS) {
        /// implement ios method
      } else {
        /// throw custom error
      }
    } on PlatformException catch (e) {
      log(e.message.toString());
    } catch (e) {
      log(e.toString());
    }
  }

  @override
  Future<void> inquiry(OtherAPI otherAPI) async {
    try {
      if (Platform.isAndroid) {
        await _channel.invokeMethod(
          'inquiry',
          otherAPI.toJson(),
        );
      } else if (Platform.isIOS) {
        /// implement ios method
      } else {
        /// throw custom error
      }
    } on PlatformException catch (e) {
      log(e.message.toString());
    } catch (e) {
      log(e.toString());
    }
  }
}
