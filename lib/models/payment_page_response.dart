class StsOnePayResponse {
  final String? amount;
  final int? approvalCode;
  final String? cardNumber;
  final String? currencyISOCode;
  final String? gatewayName;
  final int? gatewayStatusCode;
  final String? gatewayStatusDescription;
  final String? merchantID;
  final int? messageID;
  final int? paymentMethod;
  final int? rrn;
  final String? secureHash;
  final int? statusCode;
  final String? statusDescription;
  final String? token;
  final String? transactionID;
  final String? responseHashMatch;
  final bool? saveCard;

  const StsOnePayResponse({
    this.amount,
    this.approvalCode,
    this.cardNumber,
    this.currencyISOCode,
    this.gatewayName,
    this.gatewayStatusCode,
    this.gatewayStatusDescription,
    this.merchantID,
    this.messageID,
    this.rrn,
    this.secureHash,
    this.paymentMethod,
    this.statusCode,
    this.statusDescription,
    this.token,
    this.transactionID,
    this.responseHashMatch,
    this.saveCard,
  });

  factory StsOnePayResponse.fromJsonSuccess(Map<String, dynamic> json) {
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
      saveCard: json["SaveCard"] == null
          ? null
          : (json["SaveCard"]).toString().toLowerCase() == "true"
              ? true
              : false,
    );
  }
  factory StsOnePayResponse.fromIOSJsonSuccess(Map<Object?, Object?> json) {

    return StsOnePayResponse(
      amount: json["Response.Amount"].toString(),
      approvalCode: int.tryParse(json["Response.ApprovalCode"].toString()),
      cardNumber: json["Response.CardNumber"].toString(),
      currencyISOCode: json["Response.CurrencyISOCode"].toString(),
      gatewayName: json["Response.GatewayName"].toString(),
      gatewayStatusCode: int.tryParse(json["Response.GatewayStatusCode"].toString()),
      gatewayStatusDescription: json["Response.GatewayStatusDescription"].toString(),
      merchantID: json["Response.MerchantID"].toString(),
      messageID: int.tryParse(json["Response.MessageID"].toString()),
      paymentMethod: int.tryParse(json["Response.PaymentMethod"].toString()),
      rrn: int.tryParse(json["Response.RRN"].toString()),
      secureHash: json["Response.SecureHash"].toString(),
      statusCode: int.tryParse(json["Response.StatusCode"].toString()),
      statusDescription: json["Response.StatusDescription"].toString(),
      token: json["Response.Token"].toString(),
      transactionID: json["Response.TransactionID"].toString(),
      responseHashMatch: json["ResponseHashMatch"].toString(),
      saveCard: json["SaveCard"] == null
          ? null
          : (json["SaveCard"]).toString().toLowerCase() == "true"
              ? true
              : false,
    );
  }

  factory StsOnePayResponse.fromJsonFailed(Map<String, dynamic> json) {
    return StsOnePayResponse(
      secureHash: json["Response.SecureHash"],
      statusCode: int.tryParse(json["Response.StatusCode"]),
      statusDescription: json["Response.StatusDescription"],
      responseHashMatch: json["ResponseHashMatch"],
    );
  }
}
