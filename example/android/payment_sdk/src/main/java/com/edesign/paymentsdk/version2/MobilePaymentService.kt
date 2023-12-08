package com.edesign.paymentsdk.version2

import android.content.Context
import com.edesign.paymentsdk.Payment.PaymentRequest
import com.edesign.paymentsdk.Utils.Utility

class MobilePaymentService {
    private var mContext:Context
    private var req = PaymentRequest()
    private var utility: Utility? = null

    constructor(context:Context){
        mContext=context
    }

    fun process(request:PaymentRequest): String? {
        req=request
        utility= Utility(mContext)
        return  utility!!.secureHashForRequest(req.secKey.get("AuthenticationToken"),req.parameters)
    }
}