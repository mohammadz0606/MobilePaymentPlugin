package com.example.sts_one_pay

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class StsOnePayPlugin : FlutterPlugin, ActivityAware ,MethodChannel.MethodCallHandler {

    private lateinit var channel: MethodChannel

    private lateinit var stsOnePaySdk: StsOnePaySdk

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "paymentMethod" -> {
                stsOnePaySdk.paymentMethod(call.arguments as Map<String, Any>)
                result.success(null)
            }
            "refund" -> {
                stsOnePaySdk.refund(call.arguments as Map<String, Any>)
                result.success(null)
            }
            "completion" -> {
                stsOnePaySdk.completion(call.arguments as Map<String, Any>)
                result.success(null)
            }
            "inquiry" -> {
                stsOnePaySdk.inquiry(call.arguments as Map<String, Any>)
                result.success(null)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "sts_one_pay")
        channel.setMethodCallHandler(this)
        //flutterPluginBinding.applicationContext

    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        stsOnePaySdk = StsOnePaySdk(binding.activity as FlutterActivity)
    }

    override fun onDetachedFromActivityForConfigChanges() {

    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        stsOnePaySdk = StsOnePaySdk(binding.activity as FlutterActivity)
    }

    override fun onDetachedFromActivity() {

    }
}
