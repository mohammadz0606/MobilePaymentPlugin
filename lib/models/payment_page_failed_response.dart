class StsOnePayPaymentFailed {
  final String? secureHash;
  final int? statusCode;
  final String? statusDescription;
  final String? responseHashMatch;

  const StsOnePayPaymentFailed({
    this.secureHash,
    this.statusCode,
    this.statusDescription,
    this.responseHashMatch,
  });

  factory StsOnePayPaymentFailed.fromJson(Map<String, dynamic> json) {
    return StsOnePayPaymentFailed(
      secureHash: json["secureHash"],
      statusCode: int.parse(json["statusCode"]),
      statusDescription: json["statusDescription"],
      responseHashMatch: json["responseHashMatch"],
    );
  }

  factory StsOnePayPaymentFailed.fromIOSJson(Map<Object?, Object?> json) {
    return StsOnePayPaymentFailed(
      secureHash: json["secureHash"].toString(),
      statusCode: int.parse(json["code"].toString()),
      statusDescription: json["statusDescription"].toString(),
      responseHashMatch: json["responseHashMatch"].toString(),
    );
  }
}
