package com.example.sts_one_pay_example

import android.widget.Toast
import com.edesign.paymentsdk.Inquiry.InquiryRequest
import com.edesign.paymentsdk.Inquiry.InquiryResponse
import com.edesign.paymentsdk.Refund.RefundRequest
import com.edesign.paymentsdk.Refund.RefundResponse
import com.edesign.paymentsdk.version2.*
import com.edesign.paymentsdk.version2.CardType.*
import com.edesign.paymentsdk.version2.completion.CompletionCallback
import com.edesign.paymentsdk.version2.completion.CompletionRequest
import com.edesign.paymentsdk.version2.completion.CompletionResponse
import com.edesign.paymentsdk.version2.completion.SmartRouteCompletionService
import com.edesign.paymentsdk.version2.inquiry.InquiryCallback
import com.edesign.paymentsdk.version2.inquiry.SmartRouteInquiryService
import com.edesign.paymentsdk.version2.refund.RefundCallback
import com.edesign.paymentsdk.version2.refund.SmartRouteRefundService
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import com.google.gson.Gson


class MainActivity : FlutterActivity(), PaymentResultListener {
    private val channel = "samples.flutter.dev/payment"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            channel
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

    private fun getResult(result: Map<String, Any>) {
        flutterEngine?.let { engine ->
            MethodChannel(engine.dartExecutor.binaryMessenger, channel)
                .invokeMethod("getResult", result)
        }
    }

    private fun paymentMethod(params: Map<String, Any>) {
        val request = OpenPaymentRequest()
        request.paymentType = params["paymentType"] as String
        PaymentType.PREAUTH.name
        request.add("AuthenticationToken", params["authenticationToken"] as String)
        request.add("TransactionID", params["transactionId"] as String)
        request.add("MerchantID", params["merchantID"] as String)
        request.add("ClientIPaddress", params["clientIPaddress"] as String)
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
        val tokenList = params["tokens"] as List<String>
        //c52bdd6fa4266337c024cc738df1c59b6c4dc0088c863300d66025709219d6ea
        //cbaed5f2465fc578b0394631737420c2d5d4dc73324cf29e053591b4bb209ad
        //8e6ceb318f8aeedca171600d0713ba33d973f6e5d5d6e92e69bfa8ad4d24577a
        request.add("Tokens", tokenList.joinToString(","))
        val checkout = Checkout(this, this)
        checkout.open(request)
    }

    override fun onDeleteCardResponse(token: String, deleted: Boolean) {
        Toast.makeText(this, "$token + $deleted", Toast.LENGTH_LONG).show()
    }

    override fun onPaymentFailed(a: MutableMap<String, String>) {
        Toast.makeText(this, Gson().toJson(a), Toast.LENGTH_LONG).show()
        val result = mapOf("status" to "failed", "data" to a)
        getResult(result)
    }

    override fun onResponse(a: MutableMap<String, String>) {
        val result = mapOf("status" to "success", "data" to a)
        getResult(result)
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
                    val result = mapOf("data" to response)
                    getResult(result)
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
                    val result = mapOf("data" to response)
                    getResult(result)
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
                    val result = mapOf("data" to response)
                    getResult(result)
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

/*
private fun paymentMethod() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val request = OpenPaymentRequest()
        request.paymentType = PaymentType.SALES.name
        PaymentType.PREAUTH.name
        request.add("AuthenticationToken", "MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh")
        request.add("TransactionID", System.currentTimeMillis().toString())
        request.add("MerchantID", "AirrchipMerchant")
        request.add("ClientIPaddress", "3.7.21.24")
        request.add("Amount", "200")
        request.add("Currency", "682")
        request.add("PaymentDescription", "Sample Payment")
        request.add("AgreementID", "")
        request.add("AgreementType", "")
        request.add("Language", "en")
        request.add("ThreeDSEnable", true)
        request.add("TokenizeCard", true)
        request.add("CardScanningEnable", true)
        request.add("SaveCard", true)
        request.add("PaymentMethod", arrayListOf<String>(PaymentMethod.Cards.name))
        request.add(
            "CardType",
            arrayListOf<String>(
                VISA.name,
                MASTERCARD.name,
                AMEX.name,
                DINERS.name,
                UNION.name,
                JCB.name,
                DISCOVER.name,
                MADA.name,
            ),
        )
        //optional param
        //request.addOptional("ItemID", "Item1")
        //request.addOptional("Quantity", "1")
        //request.addOptional("Version", "1.0")
        request.addOptional("FrameworkInfo", "Android 7.0")
        request.add("Tokens", arrayOf<String>())
        val checkout = Checkout(this, this)
        checkout.open(request)
    }
 */