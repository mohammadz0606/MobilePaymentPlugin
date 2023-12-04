import '../sts_one_pay_platform_interface.dart';

class StsOnePay {
  final String merchantID;
  final String authenticationToken;
  final String amount;
  final List<String> tokens;
  final String currency;
  final String transactionId;
  final bool isThreeDSSecure;
  final bool shouldTokenizeCard;
  final bool isCardScanEnable;
  final bool isSaveCardEnable;
  final Language langCode;
  final PaymentType paymentType;
  final String paymentDescription;
  final String version;

  StsOnePay({
    required this.authenticationToken,
    required this.merchantID,
    required this.amount,
    this.tokens = const [],
    required this.currency,
    this.transactionId = '',
    this.isThreeDSSecure = true,
    this.shouldTokenizeCard = true,
    this.isCardScanEnable = true,
    this.isSaveCardEnable = true,
    this.langCode = Language.en,
    this.paymentType = PaymentType.sale,
    this.paymentDescription = 'Sample Payment',
    this.version = '1.0',
  }) : assert(double.parse(amount) > 0);

  Map<String, dynamic> toJson() {
    return {
      "authenticationToken": authenticationToken,
      "merchantID": merchantID,
      "amount": amount.trim(),
      "tokens": tokens,
      "currency": currency.trim(),
      "transactionId": transactionId.isEmpty
          ? StsOnePayPlatform.generateTransactionId()
          : transactionId.trim(),
      "isThreeDSSecure": isThreeDSSecure,
      "shouldTokenizeCard": shouldTokenizeCard,
      "isCardScanEnable": isCardScanEnable,
      "isSaveCardEnable": isSaveCardEnable,
      "langCode": langCode.name,
      "paymentType": paymentType.name,
      "paymentDescription": paymentDescription,
      "version": version,
    };
  }
}

enum Language {
  ar,
  en,
  tr,
}

enum PaymentType {
  sale,
  preAuth,
}

enum CardType {
  visa,
  mastercard,
  amex,
  diners,
  union,
  jcb,
  discover,
  mada,
}
