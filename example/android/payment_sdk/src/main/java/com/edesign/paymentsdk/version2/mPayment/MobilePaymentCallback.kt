package com.edesign.paymentsdk.version2.mPayment

interface MobilePaymentCallback {
    fun onResponse(MobilePaymentResponse: MobilePaymentResponse)
}