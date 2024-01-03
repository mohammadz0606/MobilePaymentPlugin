import Flutter
import UIKit
import MobilePaymentSDK

public class StsOnePayPlugin: NSObject, FlutterPlugin {
    private var paymentDelegate: MyPaymentSDKDelegate?

    public static func register(with registrar: FlutterPluginRegistrar) {
        let instance = StsOnePayPlugin()
        let merchantId = "AirrchipMerchant"
        let secretKey = "MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh"
        let appleMerchantId = "merchant.com.stspayone.demo"

        // Initialize MyPaymentSDKDelegate as an instance variable
        instance.paymentDelegate = MyPaymentSDKDelegate()
        try? MobilePaymentSDK.initializeSDK(withMerchantID: merchantId, secretKey: secretKey, delegate: instance.paymentDelegate, appleMerchantId: appleMerchantId)

        let channel = FlutterMethodChannel(name: "samples.flutter.dev/paymentIOS", binaryMessenger: registrar.messenger())
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "openPaymentPage":
            if let viewController = UIApplication.shared.windows.first?.rootViewController as? UIViewController, let args = call.arguments as? [String: Any] {
                guard let args = call.arguments as? [String : Any] else {return}
                                               print("dddd")
                           print(args["amount"]!)
                handleOpenPaymentPage(viewController: viewController, args: args, result: result)
            } else {
                result(FlutterError(code: "VIEW_CONTROLLER_ERROR", message: "Unable to get root view controller or invalid arguments", details: nil))
            }
        case "getInquiry":
            MobilePaymentSDK.shared.getInquiry(forTransactionId: "1704311676991")
            result(nil)
        case "refund":
            MobilePaymentSDK.shared.requestForPaymentRefund(
            forOriginalTransactionId: "",
            forTransactionId: "",
            forIsoCountry: "",
            forAmount: "")
            result(nil)
        case "completion":
            MobilePaymentSDK.shared.requestForPaymentCompletion(
            forOriginalTransactionId: "",
            forTransactionId: "",
            forIsoCountry: "",
            forAmount: "")
            result(nil)
        default:
            result(FlutterMethodNotImplemented)
        }
    }

    func handleOpenPaymentPage(viewController: UIViewController, args: [String: Any], result: @escaping FlutterResult) {
        // Extract necessary data from Flutter method call arguments
        guard let amount = args["amount"] as? String
        , let currency = args["currency"] as? String
        , let transactionId = args["transactionId"] as? String
        , let is3DSAuth = args["isThreeDSSecure"] as? Bool
        , let shouldTokenizeCard = args["shouldTokenizeCard"] as? Bool
        , let isCardScanningEnabled = args["isCardScanEnable"] as? Bool
         else {
            result(FlutterError(code: "INVALID_ARGUMENTS", message: "Invalid payment data", details: nil))
            return
        }

        MobilePaymentSDK.shared.showPaymentPage(
            fromViewController: viewController,
            amount: amount,
            currency: currency,
                  tokens: [],
                  paymentMethods: [.visa, .amex, .mada, .mastercard],
                  transactionId: transactionId,
                  paymentType: .sale,
                  is3DSAuth: is3DSAuth,
                  shouldTokenizeCard: shouldTokenizeCard,
                  isCardScanningEnabled: isCardScanningEnabled,
                  language: .arabic,
                  paymentDescription: "Pay for your goods",
                  paymentTitle: "This is title",
                  quantity: "1",
                  itemId: "0",
                  agreementId: "",
                  agreementType: .none
                )

        result("Payment page opened successfully")
    }


}

public class MyPaymentSDKDelegate: MobilePaymentSDKDelegate {
    public func onPaymentSuccess(withTransactionId transactionId: String, infoDictionary: [String: Any], tokenizedCard: String?, shouldStoreCard: Bool) {
    print(transactionId)
    print(infoDictionary)
    print(tokenizedCard)
    print(shouldStoreCard)
        // Implementation goes here
    }

    public func onPaymentError(_ error: MobilePaymentError, transactionId: String) {
        print(error)
    print(transactionId)

        // Implementation goes here
    }

    public func onPaymentCancelled(transactionId: String) {
        print(transactionId)

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
