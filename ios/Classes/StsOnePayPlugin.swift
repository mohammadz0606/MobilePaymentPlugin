import Flutter
import UIKit
import MobilePaymentSDK

public class StsOnePayPlugin: NSObject, FlutterPlugin {
    private var result: ((Any) -> ())?
        public static let shared = StsOnePayPlugin()

    override private init() {}
    public static func register(with registrar: FlutterPluginRegistrar) {
        let instance = StsOnePayPlugin()
//         let merchantId = "AirrchipMerchant"
//         let secretKey = "MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh"
//         let appleMerchantId = "merchant.com.stspayone.demo"

        // Initialize MyPaymentSDKDelegate as an instance variable
        

        let channel = FlutterMethodChannel(name: "samples.flutter.dev/paymentIOS", binaryMessenger: registrar.messenger())
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "initializeSDK":
         guard let args = call.arguments as? [String : Any] else {return}
         handleInitializeSDK(args: args)
        case "openPaymentPage":
            if let viewController = UIApplication.shared.windows.first?.rootViewController as? UIViewController, let args = call.arguments as? [String: Any] {
                guard let args = call.arguments as? [String : Any] else {return}
                print("dddd")
                print(args["amount"]!)
                self.result = result
                handleOpenPaymentPage(viewController: viewController, args: args, result: self.result!)
            } else {
                result(result)
            }
        case "getInquiry":
            guard let args = call.arguments as? [String : Any] else {return}
            guard let forTransactionId = args["transactionID"] as? String
            else {
                result(FlutterError(code: "INVALID_ARGUMENTS", message: "Invalid payment data", details: nil))
                return
            }
            MobilePaymentSDK.shared.getInquiry(forTransactionId: forTransactionId)
            //let response = "Payment successful for transaction ID: \(forTransactionId)"
            //            result(response)
        case "refund":
            guard let args = call.arguments as? [String : Any] else {return}
            guard let forOriginalTransactionId = args["originalTransactionID"] as? String,
                  let forTransactionId = args["transactionID"] as? String,
                  let forIsoCountry = args["currencyISOCode"] as? String,
                  let forAmount = args["amount"] as? String
            else {
                result(FlutterError(code: "INVALID_ARGUMENTS", message: "Invalid payment data", details: nil))
                return
            }
            self.result = result
            MobilePaymentSDK.shared.requestForPaymentRefund(
                forOriginalTransactionId: forOriginalTransactionId,
                forTransactionId: forTransactionId,
                forIsoCountry: forIsoCountry,
                forAmount: forAmount)
            //            result(nil)
        case "completion":
            guard let args = call.arguments as? [String : Any] else {return}
            guard let forOriginalTransactionId = args["originalTransactionID"] as? String,
                  let forTransactionId = args["transactionID"] as? String,
                  let forIsoCountry = args["currencyISOCode"] as? String,
                  let forAmount = args["amount"] as? String
            else {
                result(FlutterError(code: "INVALID_ARGUMENTS", message: "Invalid payment data", details: nil))
                return
            }
            self.result = result
            MobilePaymentSDK.shared.requestForPaymentCompletion(
                forOriginalTransactionId: forOriginalTransactionId,
                forTransactionId: forTransactionId,
                forIsoCountry: forIsoCountry,
                forAmount: forAmount)
            //            result(nil)
        default:
            result(FlutterMethodNotImplemented)
        }
    }
       func handleInitializeSDK(args: [String: Any]) {
//         guard let merchantId = args["merchantId"] as? String,
//                  let secretKey = args["secretKey"] as? String,
//                  let appleMerchantId = args["appleMerchantId"] as? String
//            else {
//                result(FlutterError(code: "INVALID_ARGUMENTS", message: "Invalid payment data", details: nil))
//                return
//            }

            try? MobilePaymentSDK.initializeSDK(
                withMerchantID: "AirrchipMerchant",
                secretKey: "MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh",
                delegate: StsOnePayPlugin.shared,
                appleMerchantId: "merchant.com.stspayone.demo"
            )
        }
    func handleOpenPaymentPage(viewController: UIViewController, args: [String: Any], result: @escaping FlutterResult) {
        // Extract necessary data from Flutter method call arguments
        guard let amount = args["amount"] as? String,
              let currency = args["currency"] as? String,
              let transactionId = args["transactionId"] as? String,
              let paymentTitle = args["paymentTitle"] as? String,
              let paymentDescription = args["paymentDescription"] as? String,
              let itemId = args["itemId"] as? String,
              let quantity = args["quantity"] as? String,
              let is3DSAuth = args["isThreeDSSecure"] as? Bool,
              let shouldTokenizeCard = args["shouldTokenizeCard"] as? Bool,
              let isCardScanningEnabled = args["isCardScanEnable"] as? Bool,
              let paymentMethods = args["cardsType"] as? [Int],
              let tokensValues = args["tokens"] as? [String],
              let paymentTypeValue = args["paymentType"] as? Int,
              let langCode = args["langCode"] as? String,
              let agreementId = args["agreementId"] as? String,
              let agreementTypeValue = args["agreementType"] as? String
        else {
            result(FlutterError(code: "INVALID_ARGUMENTS", message: "Invalid payment data", details: nil))
            return
        }
        var paymentMthds: [MobilePaymentSDKPaymentMethod] = []
        for item in paymentMethods {
            paymentMthds.append(MobilePaymentSDKPaymentMethod(rawValue: item).unsafelyUnwrapped)
        }
        var tokens: [String] = []
        for item in tokensValues {
            tokens.append(item)
        }
        var paymentType = MobilePaymentType(rawValue: paymentTypeValue).unsafelyUnwrapped
        //ZA: fix me here
//                var lang = MobilePaymentSupportedLanguage.init(rawValue: <#T##MobilePaymentSupportedLanguage.RawValue#>)
//        var agreementType = MobileAgreementType(rawValue: agreementTypeValue).unsafelyUnwrapped
        MobilePaymentSDK.shared.showPaymentPage(
            fromViewController: viewController,
            amount: amount,
            currency: currency,
            tokens: tokens,
            paymentMethods: paymentMthds,
            transactionId: transactionId,
            paymentType: paymentType,
            is3DSAuth: is3DSAuth,
            shouldTokenizeCard: shouldTokenizeCard,
            isCardScanningEnabled: isCardScanningEnabled,
            language: .arabic,
            paymentDescription: paymentDescription,
            paymentTitle: paymentTitle,
            quantity: quantity,
            itemId: itemId,
            agreementId: agreementId,
            agreementType: .none
        )

        result("Payment page opened successfully")
    }


}

extension StsOnePayPlugin: MobilePaymentSDKDelegate {
    public func onPaymentSuccess(withTransactionId transactionId: String, infoDictionary: [String: Any], tokenizedCard: String?, shouldStoreCard: Bool) {
    print(transactionId)
    print(infoDictionary)
    print(tokenizedCard)
    print(shouldStoreCard)
        self.result?(infoDictionary)
        // Implementation goes here
    }

    public func onPaymentError(_ error: MobilePaymentError, transactionId: String) {
        print(error)
    print(transactionId)
        // Implementation goes here
        self.result?(error)
    }

    public func onPaymentCancelled(transactionId: String) {
        print(transactionId)
        self.result?(transactionId)
        // Implementation goes here
    }

    public func didTapDeleteCard(_ withToken: String) {
        print(withToken)

        // Implementation goes here
    }

    public func onPaymentCompletion(withResponse response: MobilePaymentGatewayResponse) {
        // Implementation goes here
    }
}

struct FlutterReponse {
    var type: MyEnum?
}

enum MyEnum {
    case success
    case error
    case cancelled
}
