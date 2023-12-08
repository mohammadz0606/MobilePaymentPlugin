package com.edesign.paymentsdk.version2

import android.app.Activity
import android.content.Intent
import com.edesign.paymentsdk.Utils.ErrorCodesMessage
import com.edesign.paymentsdk.Utils.Parameters
import com.edesign.paymentsdk.Utils.STSError

class Checkout {
    var activity: Activity
    var request = OpenPaymentRequest()
    var response= STSError()

    companion object {
        lateinit var param: PaymentResultListener
    }


    constructor(activity: Activity, param: PaymentResultListener) {
        this.activity = activity
        Companion.param = param
    }

    fun open(req: OpenPaymentRequest) {
        request = req
        var a=request.getParameters()[Parameters.AUTHENTICATION_TOKEN]
        var b=  request.getParameters()[Parameters.MERCHANT_ID]
        if (request.getParameters()[Parameters.AUTHENTICATION_TOKEN] == null || request.getParameters()[Parameters.AUTHENTICATION_TOKEN].toString()
                .isNullOrEmpty() || request.getParameters()[Parameters.MERCHANT_ID]==null || request.getParameters()[Parameters.MERCHANT_ID].toString().isNullOrEmpty()
        ) {
            response!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kSDKNotInitializedErrorCode)
            response!!.addResponse(ErrorCodesMessage.kErrorDescription, ErrorCodesMessage.kSDKNotInitialiazedErrorMessage)
            param.onPaymentFailed(response.response)
            return
        }else if (request.getParameters()[Parameters.TRANSACTION_ID] == null || request.getParameters()[Parameters.TRANSACTION_ID].toString()
                .isNullOrEmpty()) {
            response!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kInvalidTransactionIdCode)
            response!!.addResponse(ErrorCodesMessage.kErrorDescription, ErrorCodesMessage.kInvalidTransactionIdMessage)
            param.onPaymentFailed(response.response)
            return
        }else if (request.getParameters()[Parameters.CURRENCY] == null || request.getParameters()[Parameters.CURRENCY].toString()
                .isNullOrEmpty()) {
            response!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kInvalidCurrencyCodeErrorCode)
            response!!.addResponse(ErrorCodesMessage.kErrorDescription, ErrorCodesMessage.kInvalidCurrencyCodeErrorMessage)
            param.onPaymentFailed(response.response)
            return
        }else if (request.getParameters()[Parameters.AMOUNT] == null || request.getParameters()[Parameters.AMOUNT].toString()
                .isNullOrEmpty()) {
            response!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kInvalidAmountCode)
            response!!.addResponse(ErrorCodesMessage.kErrorDescription, ErrorCodesMessage.kInvalidAmountMessage)
            param.onPaymentFailed(response.response)
            return
        }

        var intent = Intent(activity, PaymentActivity::class.java)
        intent.putExtra(Parameters.OPTIONS, request)
        activity!!.startActivityForResult(intent, 444)

    }
}