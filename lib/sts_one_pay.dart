import 'dart:math';

class StsOnePay {
  final String merchantID;
  final String authenticationToken;
  final String amount;
  final List<String>? tokens;
  final String currency;
  final String transactionId;
  final bool? isThreeDSSecure;
  final bool? shouldTokenizeCard;
  final bool? isCardScanEnable;
  final bool? isSaveCardEnable;
  final Language? langCode;
  final PaymentType? paymentType;
  final String? paymentDescription;

  StsOnePay(
      {required this.authenticationToken,
      required this.merchantID,
      required this.amount,
      this.tokens,
      required this.currency,
      required this.transactionId,
      this.isThreeDSSecure = true,
      this.shouldTokenizeCard = true,
      this.isCardScanEnable = true,
      this.isSaveCardEnable = true,
      this.langCode = Language.en,
      this.paymentType = PaymentType.sale,
      this.paymentDescription = 'Sample Payment'});

  Map<String, dynamic> toJson() {
    return {
      "authenticationToken": authenticationToken,
      "merchantID": merchantID,
      "amount": amount.trim(),
      "tokens": tokens,
      "currency": currency.trim(),
      "transactionId": transactionId.isEmpty
          ? _generateTransactionId()
          : transactionId.trim(),
      "isThreeDSSecure": isThreeDSSecure,
      "shouldTokenizeCard": shouldTokenizeCard,
      "isCardScanEnable": isCardScanEnable,
      "isSaveCardEnable": isSaveCardEnable,
      "langCode": langCode?.name ?? Language.en.name,
      "paymentType": paymentType?.name ?? PaymentType.sale.name,
      "paymentDescription": paymentDescription,
    };
  }

  String _generateTransactionId() {
    int timestamp = DateTime.now().millisecondsSinceEpoch;
    int random = Random().nextInt(999999);
    return (timestamp + random).toString();
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
