import '../sts_one_pay_platform_interface.dart';

class OtherAPI {
  final String authenticationToken;
  final String messageID;
  final String merchantID;
  final String transactionID;
  final String currencyISOCode;
  final String amount;
  final String originalTransactionID;
  final String version;

  OtherAPI({
    this.authenticationToken = 'MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh',
    this.messageID = '5',
    this.merchantID = 'AirrchipMerchant',
    this.transactionID = '',
    required this.currencyISOCode,
    required this.amount,
    required this.originalTransactionID,
    this.version = '1.0',
  }) : assert(double.parse(amount) > 0);

  Map<String, dynamic> toJson() {
    return {
      "authenticationToken": authenticationToken,
      "messageID": messageID,
      "merchantID": merchantID,
      "transactionID": transactionID.isEmpty
          ? StsOnePayPlatform.generateTransactionId()
          : transactionID,
      "currencyISOCode": currencyISOCode,
      "amount": amount,
      "originalTransactionID": originalTransactionID,
      "version": version,
    };
  }
}
