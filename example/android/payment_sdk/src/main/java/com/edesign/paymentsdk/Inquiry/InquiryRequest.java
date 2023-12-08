package com.edesign.paymentsdk.Inquiry;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by eDesign on 28/04/2017.
 * * Using this InquiryRequest class merchant mobile application should be able to send a Inquiry Payment message
 * using PayOne EZ-Connect interface and Smart Route interface.
 */

public class InquiryRequest {
    private String PaymentType;
    private String key;
    private String value;
    private Map<String, String> parameters = new TreeMap<String, String>();
    private Map<String, String> secKey = new TreeMap<String, String>();
    private Map<String, String> genericKeyValue = new TreeMap<String, String>();
    private boolean log;


    /*This method add the requested parameter based on key value pair
    * Merchant simply create object of InquiryRequest and then call this method to add the requested parameter*/
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
        PaymentType = paymentType;
    }

    /*This method return the requested payment type*/
    public String getPaymentType() {
        return PaymentType;
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
   *To enabled or disabled Log in EZ-Connect Inquiry Payment or Smart Route Inquiry Payment */
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



}

