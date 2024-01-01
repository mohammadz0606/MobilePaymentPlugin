import 'dart:developer';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:sts_one_pay/models/sts_one_pay.dart';

import 'models/other_api.dart';
import 'sts_one_pay_platform_interface.dart';

class MethodChannelStsOnePay extends StsOnePayPlatform {
  final MethodChannel _channel =
      const MethodChannel('samples.flutter.dev/payment');

  @override
  Future<Map<String, String>> openPaymentPage(StsOnePay stsOnePay) async {
    try {
      if (Platform.isAndroid) {
        Map<String, String> responseData = {};
        await _channel.invokeMethod(
          'paymentMethod',
          stsOnePay.toJson(),
        );
        _channel.setMethodCallHandler((call) async {
          if (call.method == 'getResult') {
           try{
             Map<String, String> data = Map.castFrom(call.arguments['data']);
             responseData = data;
             log('data getResult');
             log(responseData.toString());
           }catch(e){
             log('Error in call arguments');
             throw Exception(e.toString());
           }
          }
        });
        return responseData;
      } else if (Platform.isIOS) {
        /// implement ios method
        Map<String, String> responseData = {};
        return responseData;
      } else {
        throw PlatformException(code: '0', message: '');
      }
    } on PlatformException catch (e) {
      log(e.message.toString());
      throw PlatformException(code: '0', message: '');
    } catch (e) {
      log(e.toString());

      /// Edit Exception
      throw Exception();
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
