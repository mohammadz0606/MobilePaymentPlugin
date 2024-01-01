class StsOnePayResponse {
  final String amount;
  final int? approvalCode;
  final String? cardNumber;
  final String currencyISOCode;
  final String? gatewayName;
  final int? gatewayStatusCode;
  final String? gatewayStatusDescription;
  final String merchantID;
  final int? messageID;
  final int? paymentMethod;
  final int? rrn;
  final String? secureHash;
  final int? statusCode;
  final String? statusDescription;
  final String? token;
  final String transactionID;
  final String? responseHashMatch;
  final bool saveCard;

  const StsOnePayResponse({
    required this.amount,
    required this.approvalCode,
    required this.cardNumber,
    required this.currencyISOCode,
    required this.gatewayName,
    required this.gatewayStatusCode,
    required this.gatewayStatusDescription,
    required this.merchantID,
    required this.messageID,
    required this.rrn,
    required this.secureHash,
    required this.paymentMethod,
    required this.statusCode,
    required this.statusDescription,
    required this.token,
    required this.transactionID,
    required this.responseHashMatch,
    required this.saveCard,
  });

  factory StsOnePayResponse.fromJson(Map<String, dynamic> json) {
    return StsOnePayResponse(
      amount: json["Response.Amount"].toString(),
      approvalCode: int.tryParse(json["Response.ApprovalCode"]),
      cardNumber: json["Response.CardNumber"],
      currencyISOCode: json["Response.CurrencyISOCode"],
      gatewayName: json["Response.GatewayName"],
      gatewayStatusCode: int.tryParse(json["Response.GatewayStatusCode"]),
      gatewayStatusDescription: json["Response.GatewayStatusDescription"],
      merchantID: json["Response.MerchantID"],
      messageID: int.tryParse(json["Response.MessageID"]),
      paymentMethod: int.tryParse(json["Response.PaymentMethod"]),
      rrn: int.tryParse(json["Response.RRN"]),
      secureHash: json["Response.SecureHash"],
      statusCode: int.tryParse(json["Response.StatusCode"]),
      statusDescription: json["Response.StatusDescription"],
      token: json["Response.Token"],
      transactionID: json["Response.TransactionID"],
      responseHashMatch: json["ResponseHashMatch"],
      saveCard: (json["SaveCard"]).toString().toLowerCase() == "true" ? true : false,
    );
  }
}
