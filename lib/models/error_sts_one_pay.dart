class ErrorStsOnePay implements Exception {
  final int code;
  final String message;

  const ErrorStsOnePay({
    required this.code,
    required this.message,
  });
}
