
import 'sts_one_pay_platform_interface.dart';

class StsOnePay {
  Future<String?> getPlatformVersion() {
    return StsOnePayPlatform.instance.getPlatformVersion();
  }
}
