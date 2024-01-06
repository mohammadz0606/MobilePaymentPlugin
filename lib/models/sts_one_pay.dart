import '../sts_one_pay_errors_handler.dart';
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
  final String paymentTitle;
  final String paymentDescription;
  final String version;
  final String frameworkInfo;
  final String itemId;
  final String quantity;
  final String agreementId;
  final AgreementType agreementType;
  final List<CardType> cardsType;
  final String clientIPaddress;

  StsOnePay(
      {this.authenticationToken = 'MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh',
      this.merchantID = 'AirrchipMerchant',
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
      this.paymentTitle = 'Sample Payment',
      this.version = '1.0',
      this.frameworkInfo = 'Android 7.0',
      this.clientIPaddress = '3.7.21.24',
      this.itemId = "",
      this.quantity = "1",
      this.agreementId = "",
      this.agreementType = AgreementType.NONE,
      this.cardsType = const [
        CardType.mastercard,
        CardType.visa,
      ]})
      : assert(
          StsOnePayErrorHandler.amount(amount) &&
              StsOnePayErrorHandler.authenticationToken(authenticationToken) &&
              StsOnePayErrorHandler.transactionId(transactionId) &&
              StsOnePayErrorHandler.currency(currency) &&
              StsOnePayErrorHandler.merchantID(merchantID) &&
              StsOnePayErrorHandler.cardsType(cardsType),
        );

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
      "paymentDescription": paymentDescription.trim(),
      "version": version,
      "frameworkInfo": frameworkInfo,
      "clientIPaddress": clientIPaddress,
      "cardsType": cardsType.map<String>((cardType) => cardType.name).toList(),
    };
  }

  Map<String, dynamic> toIOSJson() {
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
      "paymentType": paymentType.index,
      "paymentTitle": paymentTitle,
      "paymentDescription": paymentDescription.trim(),
      "version": version,
      "frameworkInfo": frameworkInfo,
      "itemId": itemId,
      "quantity": quantity,
      "agreementId": agreementId,
      "agreementType": agreementType.name,
      "cardsType": cardsType.map<int>((cardType) => cardType.index).toList(),
    };
  }
}

enum Language {
  en,
  ar,
  tr,
}

enum PaymentType {
  sale,
  preAuth,
}
enum AgreementType {
  UNSCHEDULED,
  RECURRING,
  NONE,
}

enum CardType {
  visa,
  mastercard,
  mada,
  jcb,
  diners,
  union,
  discover,
  amex,
  applePay,
  payPal,
  cash,
}
