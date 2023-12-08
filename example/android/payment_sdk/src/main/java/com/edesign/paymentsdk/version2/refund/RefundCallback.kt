package com.edesign.paymentsdk.version2.refund

import com.edesign.paymentsdk.Refund.RefundResponse


interface RefundCallback {
    fun onResponse(response: RefundResponse)
}