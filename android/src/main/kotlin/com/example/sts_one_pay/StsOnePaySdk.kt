package com.example.sts_one_pay

import android.widget.Toast
import com.edesign.paymentsdk.Inquiry.InquiryRequest
import com.edesign.paymentsdk.Inquiry.InquiryResponse
import com.edesign.paymentsdk.Refund.RefundRequest
import com.edesign.paymentsdk.Refund.RefundResponse
import com.edesign.paymentsdk.version2.*
import com.edesign.paymentsdk.version2.completion.CompletionCallback
import com.edesign.paymentsdk.version2.completion.CompletionRequest
import com.edesign.paymentsdk.version2.completion.CompletionResponse
import com.edesign.paymentsdk.version2.completion.SmartRouteCompletionService
import com.edesign.paymentsdk.version2.inquiry.InquiryCallback
import com.edesign.paymentsdk.version2.inquiry.SmartRouteInquiryService
import com.edesign.paymentsdk.version2.refund.RefundCallback
import com.edesign.paymentsdk.version2.refund.SmartRouteRefundService
import com.google.gson.Gson
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class StsOnePaySdk : FlutterActivity(), PaymentResultListener {

    private val cannel = "samples.flutter.dev/payment"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            cannel
        ).setMethodCallHandler { call, result ->
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
    }

    private fun paymentMethod(params: Map<String, Any>) {
        val request = OpenPaymentRequest()
        request.paymentType = params["paymentType"] as String
        PaymentType.PREAUTH.name
        request.add("AuthenticationToken", params["authenticationToken"] as String)
        request.add("TransactionID", params["transactionId"] as String)
        request.add("MerchantID", params["merchantID"] as String)
        request.add("ClientIPaddress", "3.7.21.24")
        request.add("Amount", params["amount"] as String)
        request.add("Currency", params["currency"] as String)
        request.add("PaymentDescription", params["paymentDescription"] as String)
        request.add("AgreementID", "")
        request.add("AgreementType", "")
        request.add("Language", params["langCode"] as String)
        request.add("ThreeDSEnable", params["isThreeDSSecure"] as Boolean)
        request.add("TokenizeCard", params["shouldTokenizeCard"] as Boolean)
        request.add("CardScanningEnable", params["isCardScanEnable"] as Boolean)
        request.add("SaveCard", params["isSaveCardEnable"] as Boolean)
        request.add("PaymentMethod", arrayListOf<String>(PaymentMethod.Cards.name))
        request.add(
            "CardType",
            params["cardsType"] as List<String>,
        )
        //optional param
        //request.addOptional("ItemID", "Item1")
        //request.addOptional("Quantity", "1")
        request.addOptional("Version", params["version"] as String)
        request.addOptional("FrameworkInfo", params["frameworkInfo"] as String)
        request.add("Tokens", params["tokens"] as List<String>)
        val checkout = Checkout(this, this)
        checkout.open(request)
    }

    override fun onDeleteCardResponse(token: String, deleted: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onPaymentFailed(a: MutableMap<String, String>) {
        Toast.makeText(this, Gson().toJson(a), Toast.LENGTH_LONG).show()
        print("Error tr")
        print("ssss")
    }

    override fun onResponse(a: MutableMap<String, String>) {
        Toast.makeText(this, Gson().toJson(a), Toast.LENGTH_LONG).show()
    }


    private fun refund(params: Map<String, Any>) {
        val request = RefundRequest()
        request.setPaymentAuthenticationToken(
            "AuthenticationToken",
            params["authenticationToken"] as String
        )
        request.add("MessageID", params["messageID"] as String)
        request.add("MerchantID", params["merchantID"] as String)
        request.add("TransactionID", params["transactionID"] as String)
        request.add("CurrencyISOCode", params["currencyISOCode"] as String)
        request.add("Amount", params["amount"] as String)
        request.add(
            "OriginalTransactionID", params["originalTransactionID"] as String
        )
        request.add("Version", params["version"] as String)
        val paymentService = SmartRouteRefundService(this)
        paymentService.process(
            request,
            object : RefundCallback {
                override fun onResponse(response: RefundResponse) {
                    Toast.makeText(
                        applicationContext,
                        "Response: \n" + Gson().toJson(response),
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
        )
    }

    private fun completion(params: Map<String, Any>) {
        val request = CompletionRequest()
        request.setPaymentAuthenticationToken(
            "AuthenticationToken",
            params["authenticationToken"] as String
        )
        request.add("MessageID", params["messageID"] as String)
        request.add("MerchantID", params["merchantID"] as String)
        request.add("TransactionID", params["transactionID"] as String)
        request.add("CurrencyISOCode", params["currencyISOCode"] as String)
        request.add("Amount", params["amount"] as String)
        request.add(
            "OriginalTransactionID", params["originalTransactionID"] as String
        )
        request.add("Version", params["version"] as String)
        val paymentService = SmartRouteCompletionService(this)
        paymentService.process(
            request,
            object : CompletionCallback {
                override fun onResponse(response: CompletionResponse) {
                    Toast.makeText(
                        applicationContext,
                        "Response: \n" + Gson().toJson(response),
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
        )
    }

    private fun inquiry(params: Map<String, Any>) {
        val request = InquiryRequest()
        request.setPaymentAuthenticationToken(
            "AuthenticationToken",
            params["authenticationToken"] as String
        )
        request.add("MessageID", params["messageID"] as String)
        request.add("MerchantID", params["merchantID"] as String)
        request.add("TransactionID", params["transactionID"] as String)
        request.add("CurrencyISOCode", params["currencyISOCode"] as String)
        request.add("Amount", params["amount"] as String)
        request.add(
            "OriginalTransactionID", params["originalTransactionID"] as String
        )
        request.add("Version", params["version"] as String)
        val paymentService = SmartRouteInquiryService(this)
        paymentService.process(
            request,
            object : InquiryCallback {
                override fun onResponse(response: InquiryResponse) {
                    Toast.makeText(
                        applicationContext,
                        "Response: \n" + Gson().toJson(response),
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
        )
    }
}