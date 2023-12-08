package com.edesign.paymentsdk.Approve;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by eDesign on 27/04/2017.
 * Using this ApproveResponse class merchant mobile application should be able to  receive its response taking into consideration that
 * the merchant is using PayOne EZ-Connect interface and Smart Route Interface.
 */

public class ApproveResponse {
    private String key, value;
    private Map<String, String> response = new TreeMap<String, String>();

    /*This method add all response that the merchant mobile application required */
    public void addResponse(String key, String value) {
        this.key = key;
        this.value = value;
        response.put(key, value);
    }

    /*This method return inside get method*/
    public Map<String, String> getResponse() {
        return response;
    }

    /* Merchant mobile application is able to retrieve response using this method based on key */
    public String get(String key) {
        String res = getResponse().get(key);
        return res;
    }


}
