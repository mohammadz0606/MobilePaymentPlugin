package com.edesign.paymentsdk.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by punja on 7/17/2017.
 */

public class Utility {

    private Context context;
    private ArrayList<String> parameterToEncodeFromSecureHash = new ArrayList<String>();
    private ArrayList<String> excludedParameterFromSecureHash = new ArrayList<String>();
    private ArrayList<String> parameterToEncode = new ArrayList<String>();
    private Map<String, String> withoutResKey = new TreeMap<String, String>();

    public Utility(Context current) {
        this.context = current;
    }

    //    Creating secureHash when user send request to server
    public String secureHashForRequest(String SECRET_KEY, Map<String, String> parameters) {
        try {
            StringBuilder orderedString = new StringBuilder();

            // append secret key to orderString

            orderedString.append(SECRET_KEY);
            for (String treeMapKey : parameters.keySet()) {

                // check ExcludedParametersFromSecureHash.txt file
                // if key is exist then skip the key

                if (excludedParameterFromSecureHash != null && excludedParameterFromSecureHash.contains(treeMapKey)) {
                    continue;
                }

                // check ParametersToEncodeForSecureHash.txt file
                // if key is exist in file then encode those values and append to orderString

                if (parameterToEncodeFromSecureHash != null && parameterToEncodeFromSecureHash.contains(treeMapKey)) {
                    orderedString.append(URLEncoder.encode(parameters.get(treeMapKey), "UTF-8"));
                } else {
                    orderedString.append(parameters.get(treeMapKey));
                }
            }
            System.out.println("Oder string for request SecureHash: " + orderedString.toString());
            String secureHash = new String(Hex.encodeHex(DigestUtils.sha256(orderedString.toString())));
            System.out.println("SecureHash for request: " + secureHash);
            return secureHash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //     Creating secureHash when user get Response from server
    public String secureHashForResponse(String SECRET_KEY, Map<String, String> parameters) {
        try {
            String secureHash = "";
            StringBuilder orderedString = new StringBuilder();
            orderedString.append(SECRET_KEY);
            if (parameters != null) {
                for (String treeMapKey : parameters.keySet()) {
                    String val = parameters.get(treeMapKey);
                    if (!val.equalsIgnoreCase("null") && !val.equalsIgnoreCase("")) {

                        // here we not adding secureHash value to orderString

                        if (!treeMapKey.equalsIgnoreCase(Parameters.RESPONSE_SECURE_HASH)) {

                            // check ExcludedParametersFromSecureHash.txt file
                            // if key is exist then skip the key

                            if (excludedParameterFromSecureHash != null && excludedParameterFromSecureHash.contains(treeMapKey)) {
                                continue;
                            }
                            // check ParametersToEncodeForSecureHash.txt file
                            // if key is exist in file then encode those values and append to orderString

                            if (parameterToEncodeFromSecureHash != null && parameterToEncodeFromSecureHash.contains(treeMapKey)) {
                                orderedString.append(URLEncoder.encode(parameters.get(treeMapKey), "UTF-8"));
                            } else {
                                orderedString.append(parameters.get(treeMapKey));
                            }
                        }
                    }
                }
                System.out.println("Oder string for Response secureHash: " + orderedString.toString());
                secureHash = new String(Hex.encodeHex(DigestUtils.sha256(orderedString.toString())));
            }
            return secureHash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Method to read ParameterToEncodeForSecureHash.txt file
    public String readWordParameterToEncodeForSecureHash() {
        StringBuilder buf = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(Parameters.PARAMETER_TO_ENCODE_FOR_SECURE_HASH_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
            String line;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
                parameterToEncodeFromSecureHash.add(line);
            }
            reader.close();
            return buf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Method to read ExcludedParameterFromSecureHash.txt file
    public String readWordExcludedParameterFromSecureHash() {
        StringBuilder buf = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(Parameters.EXCLUDED_PARAMETER_FROM_SECURE_HASH_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
            String line;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
                excludedParameterFromSecureHash.add(line);
            }
            reader.close();
            return buf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Method to read ParameterToEncode.txt file
    public String readWordsFromParameterToEncode() {
        StringBuilder buf = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(Parameters.PARAMETER_TO_ENCODE_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
            String line;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
                parameterToEncode.add(line);
            }
            reader.close();
            return buf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Method to generate request query for sending to the Payment Gateway
    public StringBuffer requestQuery(String secureHash, Map<String, String> parameters) {
        try {
            StringBuffer request = new StringBuffer();
            for (String treeMapKey : parameters.keySet()) {

                //checking parameterToEncode file
                //if key is exist in file then encode those values and append to request query

                if (parameterToEncode != null && parameterToEncode.contains(treeMapKey)) {
                    request.append(treeMapKey).append("=").append(URLEncoder.encode(parameters.get(treeMapKey), "UTF-8")).append("&");
                } else {
                    request.append(treeMapKey).append("=").append(parameters.get(treeMapKey)).append("&");
                }
            }
            return request.append("SecureHash").append("=").append(secureHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Method to check Internet Network is available
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }catch (Exception e){

        }

    }


}
