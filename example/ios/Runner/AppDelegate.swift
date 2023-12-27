import UIKit
import Flutter

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
     let merchantId = "PROVIDED_BY_SUPPORT" //mandatory string
     let secretKey = " PROVIDED_BY_SUPPORT " //mandatory string
     let appleMerchantId = "FROM_APPLE_DEVELOPER_PORTAL" //If your app supports Apple Pay add the Identifier, if not, keep it empty string.
     //Provide appleMerchantId only if you need to use apple pay //This sdk initialization is recommended to be done in AppDelegate: Didfinishlaunching
     try? MobilePaymentSDK.initializeSDK(withMerchantID: merchantId, secretKey: secretKey, delegate: self,
     appleMerchantId: appleMerchantId)
    GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
}
