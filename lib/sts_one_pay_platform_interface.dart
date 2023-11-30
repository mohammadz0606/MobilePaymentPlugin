import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'sts_one_pay_method_channel.dart';

abstract class StsOnePayPlatform extends PlatformInterface {
  /// Constructs a StsOnePayPlatform.
  StsOnePayPlatform() : super(token: _token);

  static final Object _token = Object();

  static StsOnePayPlatform _instance = MethodChannelStsOnePay();

  /// The default instance of [StsOnePayPlatform] to use.
  ///
  /// Defaults to [MethodChannelStsOnePay].
  static StsOnePayPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [StsOnePayPlatform] when
  /// they register themselves.
  static set instance(StsOnePayPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
