package com.edesign.paymentsdk.version2

interface PaymentResultListener {
    fun onResponse(a: MutableMap<String, String>)
    fun onPaymentFailed(a: MutableMap<String, String>)
    fun onDeleteCardResponse(token:String,deleted:Boolean)
}