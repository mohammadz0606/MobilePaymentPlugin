import UIKit
import Flutter


@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
      
//      let merchantId = "AirrchipMerchant"
//      let secretKey = "MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh"
//      let appleMerchantId = "merchant.com.stspayone.demo"
//      //Provide appleMerchantId only if you need to use apple pay //This sdk initialization is recommended to be done in AppDelegate: Didfinishlaunching
//      try? MobilePaymentSDK.initializeSDK(withMerchantID: merchantId, secretKey: secretKey, delegate: self,
//      appleMerchantId: appleMerchantId)
//
//       let controller = window?.rootViewController as! FlutterViewController
//       let channel = FlutterMethodChannel(name: "samples.flutter.dev/testt", binaryMessenger: controller.binaryMessenger)
//
//
//       channel.setMethodCallHandler { (call, result) in
//           if call.method == "callNativeSwiftMethod" {
//             self.callNativeSwiftMethod() // <-- Make sure this function is inside the class
//             result(nil)
//           } else if call.method == "openPaymentPage" {
//               if let viewController = self.window?.rootViewController as? UIViewController {
//               print("indele")
//                 self.handleOpenPaymentPage(viewController: viewController, result: result)
//               } else {
//                 result(FlutterError(code: "VIEW_CONTROLLER_ERROR", message: "Unable to get root view controller", details: nil))
//               }
//             } else if call.method == "getInquiry" {
//                 MobilePaymentSDK.shared.getInquiry(forTransactionId: "1111111")
//                 print("dddd")
//                 result(nil)
//             } else {
//               result(FlutterMethodNotImplemented)
//             }
//         }
      
    GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
//
//     func handleOpenPaymentPage(viewController: UIViewController, result: @escaping FlutterResult) {
//         // Call showPaymentPage as a static method on the MobilePaymentSDK class
//         MobilePaymentSDK.shared.showPaymentPage(
//           fromViewController: viewController,
//           amount: "10233",
//           currency: "400",
//           tokens: [],
//           paymentMethods: [.visa, .amex, .mada, .mastercard],
//           transactionId: "12345",
//           paymentType: .sale,
//           is3DSAuth: true,
//           shouldTokenizeCard: true,
//           isCardScanningEnabled: true,
//           language: .arabic,
//           paymentDescription: "Pay for your goods",
//           paymentTitle: "This is title",
//           quantity: "1",
//           itemId: "0",
//           agreementId: "",
//           agreementType: .none
//         )
//
//         result(nil)
//       }
    
//     func callNativeSwiftMethod() {
//        // Your Swift code implementation goes here
//        let alertController = UIAlertController(title: "Native Swift Alert", message: "Hello from Swift", preferredStyle: .alert)
//        alertController.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
//
//        // Assuming there is a visible view controller to present the alert
//        self.window?.rootViewController?.present(alertController, animated: true, completion: nil)
//      }
// }


//
// extension AppDelegate : MobilePaymentSDKDelegate{
//     func onPaymentSuccess(withTransactionId transactionId: String, infoDictionary: [String : Any], tokenizedCard: String?, shouldStoreCard: Bool) {
//     }
//
//     func onPaymentError(_ error: MobilePaymentError, transactionId: String) {
//
//     }
//
//     func onPaymentCancelled(transactionId: String) {
//
//     }
//
//     func didTapDeleteCard(_ withToken: String) {
//     }
//
//     func onPaymentCompletion(withResponse response: MobilePaymentGatewayResponse) {
//     }
//
//
 }
