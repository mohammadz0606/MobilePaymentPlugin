class ErrorStsOnePay implements Exception {
  final String code;
  final String message;

  const ErrorStsOnePay({
    required this.code,
    required this.message,
  });
}
