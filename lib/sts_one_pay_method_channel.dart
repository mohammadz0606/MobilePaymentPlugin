import 'dart:developer';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:sts_one_pay/models/initializeSDK.dart';
import 'package:sts_one_pay/models/sts_one_pay.dart';

import 'models/other_api.dart';
import 'sts_one_pay_platform_interface.dart';

class MethodChannelStsOnePay extends StsOnePayPlatform {
  final MethodChannel _channel =
      const MethodChannel('samples.flutter.dev/payment');
  final MethodChannel _channelIOS =
    const MethodChannel('samples.flutter.dev/paymentIOS');

  @override
  Future<void> initializeSDK(InitializeSDK initializeSDK) async {
    try {
      if (Platform.isAndroid) {

      } else if (Platform.isIOS) {

        final Map<Object?, Object?> resp = await _channelIOS.invokeMethod(
          'initializeSDK',
          initializeSDK.toJson(),
        );
        log(resp.toString());
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
  Future<void> openPaymentPage(StsOnePay stsOnePay) async {
    try {
      if (Platform.isAndroid) {
        await _channel.invokeMethod(
          'paymentMethod',
          stsOnePay.toJson(),
        );
      } else if (Platform.isIOS) {

        final Map<Object?, Object?> resp = await _channelIOS.invokeMethod(
          'openPaymentPage',
          stsOnePay.toIOSJson(),
        );
        log("dddd");
        log(resp["key"].toString());
        log(resp["error"].toString());
      } else {
        /// throw custom error
      }
    } on PlatformException catch (e) {
      log(e.toString());
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
        await _channelIOS.invokeMethod(
          'refund',
          otherAPI.toJson(),
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
  Future<void> completion(OtherAPI otherAPI) async {
    try {
      if (Platform.isAndroid) {
        await _channel.invokeMethod(
          'completion',
          otherAPI.toJson(),
        );
      } else if (Platform.isIOS) {
        await _channelIOS.invokeMethod(
          'completion',
          otherAPI.toJson(),
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
  Future<void> inquiry(OtherAPI otherAPI) async {
    try {
      if (Platform.isAndroid) {
        await _channel.invokeMethod(
          'inquiry',
          otherAPI.toJson(),
        );
      } else if (Platform.isIOS) {
        final response = await _channelIOS.invokeMethod(
          'getInquiry',
          otherAPI.toJson(),
        );
        print('Response from native code: $response');
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
