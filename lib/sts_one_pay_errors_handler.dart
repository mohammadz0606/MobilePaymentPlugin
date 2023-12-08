import 'models/error_sts_one_pay.dart';

abstract class StsOnePayErrorHandler {
  static bool amount(String amount) {
    RegExp specialCharacters = RegExp(r'[^\w\s]');
    double? amountDouble = double.tryParse(amount);
    if (amount.isEmpty) {
      throw const ErrorStsOnePay(
        code: 2000,
        message: 'amount is empty',
      );
    } else if (specialCharacters.hasMatch(amount)) {
      throw const ErrorStsOnePay(
        code: 2001,
        message: 'The amount must be entered without commas or periods',
      );
    } else if (amountDouble == null) {
      throw const ErrorStsOnePay(
        code: 2002,
        message: 'Amount Enter incorrectly',
      );
    } else if (amountDouble <= 0) {
      throw const ErrorStsOnePay(
        code: 2003,
        message: 'Amount must be Greater than zero',
      );
    }
    return true;
  }

  static bool authenticationToken(String authenticationToken) {
    if (authenticationToken.isEmpty) {
      throw const ErrorStsOnePay(
        code: 2004,
        message: 'AuthenticationToken is empty',
      );
    }
    return true;
  }

  static bool transactionId(String transactionId) {
    RegExp onlyNumbers = RegExp(r'^\d+$');
    if (onlyNumbers.hasMatch(transactionId)) {
      throw const ErrorStsOnePay(
        code: 2005,
        message: 'Transaction Id must be integer numbers only',
      );
    }
    return true;
  }

  static bool merchantID(String merchantID) {
    if (merchantID.isEmpty) {
      throw const ErrorStsOnePay(
        code: 2006,
        message: 'merchantID is empty',
      );
    }
    return true;
  }

  static bool currency(String currency) {
    if(currency.isEmpty){
      throw const ErrorStsOnePay(
        code: 2007,
        message: 'currency is empty',
      );
    }
    return true;
  }
}