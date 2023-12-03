import 'package:sts_one_pay/models/sts_one_pay.dart';

abstract class StsOnePayPlatform {
  Future<void> openPaymentPage(StsOnePay stsOnePay);

  Future<void> refund();

  Future<void> completion();

  Future<void> inquiry();
}
