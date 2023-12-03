import 'package:sts_one_pay/sts_one_pay.dart';

abstract class StsOnePayPlatform {
  Future<void> openPaymentPage(StsOnePay stsOnePay);

  Future<void> refund();
}
