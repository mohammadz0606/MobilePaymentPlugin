package com.example.sts_one_pay

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** StsOnePayPlugin */
class StsOnePayPlugin : FlutterPlugin, MethodCallHandler, StsOnePaySdk() {

    private lateinit var channel: MethodChannel

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "sts_one_pay")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "paymentMethod" -> {
                paymentMethod(call.arguments as Map<String, Any>)
                result.success(null)
            }
            "refund" -> {
                refund(call.arguments as Map<String, Any>)
                result.success(null)
            }
            "completion" -> {
                completion(call.arguments as Map<String, Any>)
                result.success(null)
            }
            "inquiry" -> {
                inquiry(call.arguments as Map<String, Any>)
                result.success(null)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
