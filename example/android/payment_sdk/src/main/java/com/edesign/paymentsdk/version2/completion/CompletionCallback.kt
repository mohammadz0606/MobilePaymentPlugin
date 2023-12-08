package com.edesign.paymentsdk.version2.completion


interface CompletionCallback {
    fun onResponse(response: CompletionResponse)
}