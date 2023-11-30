class OnePayModel {
  final String amount;
  final List<String> tokens;
  final String currency;
  final String transactionId;
  final bool isThreeDSSecure;
  final bool shouldTokenizeCard;
  final bool isCardScanEnable;
  final bool isSaveCardEnable;
  final String langCode;
  final String paymentType;

  const OnePayModel({
    required this.amount,
    required this.tokens,
    required this.currency,
    required this.transactionId,
    required this.isThreeDSSecure,
    required this.shouldTokenizeCard,
    required this.isCardScanEnable,
    required this.isSaveCardEnable,
    required this.langCode,
    required this.paymentType,
  });

  Map<String, dynamic> toJson() {
    return {
      "amount": amount,
      "tokens": tokens,
      "currency": currency,
      "transactionId": transactionId,
      "isThreeDSSecure": isThreeDSSecure,
      "shouldTokenizeCard": shouldTokenizeCard,
      "isCardScanEnable": isCardScanEnable,
      "isSaveCardEnable": isSaveCardEnable,
      "langCode": langCode,
      "paymentType": paymentType,
    };
  }
}
