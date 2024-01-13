import 'dart:developer';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:sts_one_pay/models/initializeSDK.dart';
import 'package:sts_one_pay/models/sts_one_pay.dart';

import 'models/error_sts_one_pay.dart';
import 'models/on_delete.dart';
import 'models/other_api.dart';
import 'models/payment_page_failed_response.dart';
import 'models/payment_page_response.dart';
import 'sts_one_pay_platform_interface.dart';

class MethodChannelStsOnePay extends StsOnePayPlatform {
  final _methodChannel = const MethodChannel('sts_one_pay');
  final _methodChannelIOS =
      const MethodChannel('samples.flutter.dev/paymentIOS');

  @override
  Future<void> initializeSDK(InitializeSDK initializeSDK) async {
    try {
      if (Platform.isAndroid) {
      } else if (Platform.isIOS) {
        final Map<Object?, Object?> resp = await _methodChannelIOS.invokeMethod(
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
  Future<void> openPaymentPage(
    StsOnePay stsOnePay, {
    required Function(StsOnePayResponse result) onPaymentResponse,
    required Function(StsOnePayPaymentFailed result) onPaymentFailed,
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
                onPaymentResponse(StsOnePayResponse.fromJson(data));
              } else {
                onPaymentFailed(StsOnePayPaymentFailed.fromJson(data));
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
        final Map<Object?, Object?> resp = await _methodChannelIOS.invokeMethod(
          'openPaymentPage',
          stsOnePay.toIOSJson(),
        );
        String status = resp["code"].toString();
        String transactionId = resp["transactionId"].toString();
        log(resp.toString(),name: transactionId);

        if (status.toLowerCase() == '200') {
          log("success",name: transactionId);
          log(resp.toString(),name: transactionId);
          // Map<String, dynamic> data = resp["infoDictionary"];
          onResultResponse(StsOnePayResponse.fromIOSJsonSuccess(resp));
        } else {
          log("error",name: transactionId);
          // onResultResponse(StsOnePayResponse.fromJsonFailed(data));
        }
      } else {
        throw const ErrorStsOnePay(
          code: 2011,
          message: 'Platform Not supports',
        );
      }
    } on PlatformException catch (e) {
      throw ErrorStsOnePay(
        code: int.tryParse(e.code)??0,
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
        await _methodChannelIOS.invokeMethod(
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
        await _methodChannel.invokeMethod(
          'completion',
          otherAPI.toJson(),
        );
      } else if (Platform.isIOS) {
        await _methodChannelIOS.invokeMethod(
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
        await _methodChannel.invokeMethod(
          'inquiry',
          otherAPI.toJson(),
        );
      } else if (Platform.isIOS) {
        final response = await _methodChannelIOS.invokeMethod(
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
