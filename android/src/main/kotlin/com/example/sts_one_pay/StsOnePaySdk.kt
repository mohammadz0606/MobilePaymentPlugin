package com.example.sts_one_pay

import android.widget.Toast
import com.edesign.paymentsdk.Inquiry.InquiryRequest
import com.edesign.paymentsdk.Inquiry.InquiryResponse
import com.edesign.paymentsdk.Refund.RefundRequest
import com.edesign.paymentsdk.Refund.RefundResponse
import com.edesign.paymentsdk.version2.*
import com.edesign.paymentsdk.version2.PaymentResultListener
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
import io.flutter.plugin.common.MethodChannel


open class StsOnePaySdk(private val activity: FlutterActivity, private var channel: MethodChannel) :
    PaymentResultListener {

    private fun getResult(result: Map<String, Any>) {
        channel.invokeMethod("getResult", result)
    }

    private fun onDeleteCard(token: String, deleted: Boolean) {
        val result = mapOf("token" to token, "deleted" to deleted)
        channel.invokeMethod("onDeleteCard", result)
    }


    fun paymentMethod(params: Map<String, Any>) {
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
        //request.addOptional("ItemID", params["itemId"] as String)
        //request.addOptional("Quantity", params["quantity"] as String)
        request.addOptional("Version", params["version"] as String)
        request.addOptional("FrameworkInfo", params["frameworkInfo"] as String)
        val tokenList = params["tokens"] as List<String>
        //c52bdd6fa4266337c024cc738df1c59b6c4dc0088c863300d66025709219d6ea
        //cbaed5f2465fc578b0394631737420c2d5d4dc73324cf29e053591b4bb209ad
        //8e6ceb318f8aeedca171600d0713ba33d973f6e5d5d6e92e69bfa8ad4d24577a
        request.add("Tokens", tokenList.joinToString(","))
        val checkout = Checkout(activity, this)
        checkout.open(request)
    }

    override fun onDeleteCardResponse(token: String, deleted: Boolean) {
        onDeleteCard(token, deleted)
    }

    override fun onPaymentFailed(a: MutableMap<String, String>) {
        val result = mapOf("status" to "failed", "data" to a)
        getResult(result)
        //Toast.makeText(activity, Gson().toJson(a), Toast.LENGTH_LONG).show()
    }

    override fun onResponse(a: MutableMap<String, String>) {
        val result = mapOf("status" to "success", "data" to a)
        getResult(result)
    }


    fun refund(params: Map<String, Any>) {
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
        val paymentService = SmartRouteRefundService(activity)
        paymentService.process(
            request,
            object : RefundCallback {
                override fun onResponse(response: RefundResponse) {
                    Toast.makeText(
                        activity,
                        "Response: \n" + Gson().toJson(response),
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
        )
    }

    fun completion(params: Map<String, Any>) {
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
        val paymentService = SmartRouteCompletionService(activity)
        paymentService.process(
            request,
            object : CompletionCallback {
                override fun onResponse(response: CompletionResponse) {
                    Toast.makeText(
                        activity,
                        "Response: \n" + Gson().toJson(response),
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
        )
    }

    fun inquiry(params: Map<String, Any>) {
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
        val paymentService = SmartRouteInquiryService(activity)
        paymentService.process(
            request,
            object : InquiryCallback {
                override fun onResponse(response: InquiryResponse) {
                    Toast.makeText(
                        activity,
                        "Response: \n" + Gson().toJson(response),
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
        )
    }
}