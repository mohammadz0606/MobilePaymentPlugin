package com.edesign.paymentsdk.version2.mPayment

import java.util.*

/**
 * Created by eDesign on 27/04/2017.
 * Using this ApproveResponse class merchant mobile application should be able to  receive its response taking into consideration that
 * the merchant is using PayOne EZ-Connect interface and Smart Route Interface.
 */
class MobilePaymentResponse {
    private var key: String? = null
    private var value: String? = null
    val response: MutableMap<String, String> = TreeMap()

    /*This method add all response that the merchant mobile application required */
    fun addResponse(key: String, value: String) {
        this.key = key
        this.value = value
        response[key] = value
    }


    /* Merchant mobile application is able to retrieve response using this method based on key */
    operator fun get(key: String): String? {
        return response[key]
    }
}