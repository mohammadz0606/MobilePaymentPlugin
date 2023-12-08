package com.edesign.paymentsdk.version2.completion

import java.util.*

/**
 * Created by eDesign on 28/04/2017.
 * * * Using this ApproveRequest class merchant mobile application should be able to send a Approve Payment message
 * using Smart Route interface only.
 */
class CompletionRequest {
    /*This method return the requested payment type*//*This method for check the payment type during adding the requested parameter
     *To check the request is for EZ-Connect or Smart Route*/  var paymentType: String? = null
    private var key: String? = null
    private var value: String? = null
    private val parameters: MutableMap<String, String> = TreeMap()
    private val secKey: MutableMap<String, String> = TreeMap()
    private val genericKeyValue: MutableMap<String, String> = TreeMap()
    private val parameterToEncrypt = ArrayList<String>()
    /*This method return true or false value set by Merchant*/  var logging = false
        private set

    /*This method add the requested parameter based on key value pair
   * Merchant simply create object of ApproveRequest and then call this method to add the requested parameter*/
    fun add(key: String, value: String) {
        this.key = key
        this.value = value
        parameters[key] = value
    }

    /*This method return the requested parameter*/
    fun getParameters(): Map<String, String> {
        return parameters
    }

    /*This method for adding secret key during setting requested parameter
     * Merchant add value based on key
     * If the secret key is changed merchant will have to make a change on the app
     * */
    fun setPaymentAuthenticationToken(key: String, value: String) {
        this.key = key
        this.value = value
        secKey[key] = value
    }

    /*This method return the value based on key*/
    fun getSecKey(): Map<String, String> {
        return secKey
    }

    /*This method to add true or false value  during adding the requested parameter
   *To enabled or disabled Log in Smart Route Approve Payment */
    fun isLogging(log: Boolean) {
        logging = log
    }

    /*This method used inside sts to add all Generic.txt url
         *After we can used this url based on key*/
    fun addGeneric(key: String, value: String) {
        this.key = key
        this.value = value
        genericKeyValue[key] = value
    }

    /*This method return the value based on key*/
    fun getGenericKeyValue(): Map<String, String> {
        return genericKeyValue
    }

    /*This method used inside sts to add all ParametersToEncrypt value
         *We encrypt this parameter only that available in this file*/
    fun outputParameterToEncrypt(key: String) {
        this.key = key
        parameterToEncrypt.add(key)
    }

    /*This method return value only encrypt parameter*/
    fun getParameterToEncrypt(): ArrayList<String> {
        return parameterToEncrypt
    }
}