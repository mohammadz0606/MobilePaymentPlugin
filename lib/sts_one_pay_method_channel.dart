import 'dart:developer';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:sts_one_pay/models/sts_one_pay.dart';

import 'models/error_sts_one_pay.dart';
import 'models/on_delete.dart';
import 'models/other_api.dart';
import 'models/payment_page_response.dart';
import 'sts_one_pay_platform_interface.dart';

class MethodChannelStsOnePay extends StsOnePayPlatform {
  final _methodChannel = const MethodChannel('sts_one_pay');

  @override
  Future<void> openPaymentPage(
    StsOnePay stsOnePay, {
    required Function(StsOnePayResponse result) onResultResponse,
    required Function(OnDeleteCard onDeleteCard) onDeleteCardResponse,
  }) async {
    try {
      if (Platform.isAndroid) {
        await _methodChannel.invokeMethod(
          'paymentMethod',
          stsOnePay.toJson(),
        );
        _methodChannel.setMethodCallHandler((call) async {
          if (call.method == 'getResult') {
            try {
              Map<String, dynamic> data = Map.castFrom(call.arguments['data']);
              String status = call.arguments['status'];
              if (status.toLowerCase() == 'success') {
                onResultResponse(StsOnePayResponse.fromJsonSuccess(data));
              } else {
                onResultResponse(StsOnePayResponse.fromJsonFailed(data));
              }
            } catch (e) {
              log(e.toString());
              throw Exception('Error in call arguments');
            }
          } else if (call.method == "onDeleteCard") {
            try {
              Map<String, dynamic> data = Map.castFrom(call.arguments);
              onDeleteCardResponse(OnDeleteCard.fromJson(data));
              log('onDeleteCard');
              log(data.toString());
            } catch (e) {
              log(e.toString());
              throw Exception('Error in call arguments');
            }
          }
        });
      } else if (Platform.isIOS) {
        /// implement ios method
      } else {
        throw const ErrorStsOnePay(
          code: 2011,
          message: 'Platform Not supports',
        );
      }
    } on PlatformException catch (e) {
      throw ErrorStsOnePay(
        code: 2012,
        message: 'Platform Exception ${e.message}',
      );
    } catch (e) {
      throw Exception(e.toString());
    }
  }

  @override
  Future<void> refund(OtherAPI otherAPI) async {
    try {
      if (Platform.isAndroid) {
        await _methodChannel.invokeMethod(
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
        await _methodChannel.invokeMethod(
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
        await _methodChannel.invokeMethod(
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
