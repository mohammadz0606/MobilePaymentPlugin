import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'sts_one_pay_platform_interface.dart';

/// An implementation of [StsOnePayPlatform] that uses method channels.
class MethodChannelStsOnePay extends StsOnePayPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('sts_one_pay');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
