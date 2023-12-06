import 'models/error_sts_one_pay.dart';

abstract class StsOnePayErrorHandler {
  static bool amount(String amount) {
    RegExp specialCharacters = RegExp(r'[^\w\s]');
    double? amountDouble = double.tryParse(amount);
    if (specialCharacters.hasMatch(amount)) {
      throw const ErrorStsOnePay(
        code: 1000,
        message: 'The amount must be entered without commas or periods',
      );
    } else if (amountDouble == null) {
      throw const ErrorStsOnePay(
        code: 1001,
        message: 'Amount Enter incorrectly',
      );
    } else if (amountDouble <= 0) {
      throw const ErrorStsOnePay(
        code: 1002,
        message: 'Amount must be Greater than zero',
      );
    }
    return true;
  }
}
