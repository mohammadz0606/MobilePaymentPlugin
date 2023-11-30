import 'package:flutter_test/flutter_test.dart';
import 'package:sts_one_pay/sts_one_pay.dart';
import 'package:sts_one_pay/sts_one_pay_platform_interface.dart';
import 'package:sts_one_pay/sts_one_pay_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockStsOnePayPlatform
    with MockPlatformInterfaceMixin
    implements StsOnePayPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final StsOnePayPlatform initialPlatform = StsOnePayPlatform.instance;

  test('$MethodChannelStsOnePay is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelStsOnePay>());
  });

  test('getPlatformVersion', () async {
    StsOnePay stsOnePayPlugin = StsOnePay();
    MockStsOnePayPlatform fakePlatform = MockStsOnePayPlatform();
    StsOnePayPlatform.instance = fakePlatform;

    expect(await stsOnePayPlugin.getPlatformVersion(), '42');
  });
}
