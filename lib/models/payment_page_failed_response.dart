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
}
