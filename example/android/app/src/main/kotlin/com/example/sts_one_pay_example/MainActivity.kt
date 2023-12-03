package com.example.sts_one_pay_example

import android.widget.Toast
import com.edesign.paymentsdk.Refund.RefundRequest
import com.edesign.paymentsdk.Refund.RefundResponse
import com.edesign.paymentsdk.version2.*
import com.edesign.paymentsdk.version2.CardType.*
import com.edesign.paymentsdk.version2.refund.RefundCallback
import com.edesign.paymentsdk.version2.refund.SmartRouteRefundService
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import com.google.gson.Gson


class MainActivity : FlutterActivity(), PaymentResultListener {
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
                    refund()
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
            arrayListOf<String>(
                VISA.name,
                MASTERCARD.name,
            ),
        )
        //optional param
        //request.addOptional("ItemID", "Item1")
        //request.addOptional("Quantity", "1")
        //request.addOptional("Version", "1.0")
        request.addOptional("FrameworkInfo", "Android 7.0")
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


    private fun refund() {
        val request = RefundRequest()
        request.setPaymentAuthenticationToken(
            "AuthenticationToken",
            "MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh"
        )
        request.add("MessageID", "4")
        request.add("MerchantID", "AirrchipMerchant")
        request.add("TransactionID", System.currentTimeMillis().toString())
        request.add("CurrencyISOCode", "682")
        request.add("Amount", "5000")
        request.add(
            "OriginalTransactionID", ""
        )
        request.add("Version", "1.0")
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