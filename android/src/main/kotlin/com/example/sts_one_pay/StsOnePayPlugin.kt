package com.example.sts_one_pay

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodChannel

class StsOnePayPlugin : FlutterPlugin {

    private lateinit var channel: MethodChannel


    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "sts_one_pay")
        val stsOnePaySdk = StsOnePaySdk(flutterPluginBinding.applicationContext)
        channel.setMethodCallHandler(stsOnePaySdk)
        //flutterPluginBinding.applicationContext
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
