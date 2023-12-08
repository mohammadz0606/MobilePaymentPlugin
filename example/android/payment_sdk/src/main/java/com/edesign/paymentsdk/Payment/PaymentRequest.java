package com.edesign.paymentsdk.Payment;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by eDesign on 19/04/2017.
 * Using this PaymentRequest class merchant mobile application should be able to send a Payment message
 * using PayOne EZ-Connect interface and Smart Route interface.
 */

public class PaymentRequest {
    private String paymentType;
    private String key;
    private String value;
    private boolean log;
    private Map<String, String> parameters = new TreeMap<String, String>();
    private Map<String, String> secKey = new TreeMap<String, String>();
    private Map<String, String> genericKeyValue = new TreeMap<String, String>();
    private ArrayList<String> parameterToEncrypt = new ArrayList<String>();


    /*This method add the requested parameter based on key value pair
    * Merchant simply create object of PaymentRequest and then call this method to add the requested parameter*/
    public void add(String key, String value) {
        this.key = key;
        this.value = value;
        parameters.put(key, value);
    }

    /*This method return the requested parameter*/
    public Map<String, String> getParameters() {
        return parameters;
    }

    /*This method for check the payment type during adding the requested parameter
    *To check the request is for EZ-Connect or Smart Route*/
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    /*This method return the requested payment type*/
    public String getPaymentType() {
        return paymentType;
    }

    /*This method for adding secret key during setting requested parameter
    * Merchant add value based on key
    * If the secret key is changed merchant will have to make a change on the app
    * */
    public void setPaymentAuthenticationToken(String key, String value) {
        this.key = key;
        this.value = value;
        secKey.put(key, value);
    }

    /*This method return the value based on key*/
    public Map<String, String> getSecKey() {
        return secKey;
    }

    /*This method to add true or false value  during adding the requested parameter
    *To enabled or disabled Log in EZ-Connect Payment or Smart Route Payment */
    public void isLogging(boolean log) {
        this.log = log;
    }

    /*This method return true or false value set by Merchant*/
    public boolean getLogging() {
        return log;
    }

    /*This method used inside sts to add all Generic.txt url
     *After we can used this url based on key*/
    public void addGeneric(String key, String value) {
        this.key = key;
        this.value = value;
        genericKeyValue.put(key, value);
    }

    /*This method return the value based on key*/
    public Map<String, String> getGenericKeyValue() {
        return genericKeyValue;
    }

    /*This method used inside sts to add all ParametersToEncrypt value
       *We encrypt this parameter only that available in this file*/
    public void outputParameterToEncrypt(String key) {
        this.key = key;
        parameterToEncrypt.add(key);
    }

    /*This method return value only encrypt parameter*/
    public ArrayList<String> getParameterToEncrypt() {
        return parameterToEncrypt;
    }

}

