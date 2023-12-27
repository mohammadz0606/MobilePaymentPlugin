import Flutter
import UIKit
import MobilePaymentSDK

public class StsOnePayPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "sts_one_pay", binaryMessenger: registrar.messenger())
//     let channel = FlutterMethodChannel(name: "MobilePaymentSDK/iOS", binaryMessenger: registrar.messenger())
    let instance = StsOnePayPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
          if call.method == "showPaymentPage" {
          let paymentResult = MobilePaymentSDK.showPaymentPage()

//             let arg1 = call.arguments as? <String,Any>
              // Handle the method call and send a result back to Flutter
              let nativeResult = "Result from native method"
              result(arg1)
          } else {
              result(FlutterMethodNotImplemented)
          }
      }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "getPlatformVersion":
      result("iOS " + UIDevice.current.systemVersion)
    default:
      result(FlutterMethodNotImplemented)
    }
  }
}
