class OnDeleteCard {
  final String token;
  final bool deleted;

  const OnDeleteCard({
    required this.token,
    required this.deleted,
  });

  factory OnDeleteCard.fromJson(Map<String, dynamic> json) {
    return OnDeleteCard(
      token: json["token"],
      deleted:
          (json["deleted"]).toString().toLowerCase() == 'true' ? true : false,
    );
  }
}
