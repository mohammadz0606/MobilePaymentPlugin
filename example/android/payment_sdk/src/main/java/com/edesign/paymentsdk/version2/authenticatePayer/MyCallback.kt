package com.edesign.paymentsdk.version2.authenticatePayer


interface MyCallback {
    fun onResponse(authenticatePayerResponse: AuthenticatePayerResponse)
}