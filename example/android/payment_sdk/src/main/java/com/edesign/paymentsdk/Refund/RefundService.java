package com.edesign.paymentsdk.Refund;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.edesign.paymentsdk.Sdk.PaymentGatway.EzRefundPayment;
import com.edesign.paymentsdk.Sdk.PaymentGatway.SmartRefundPayment;
import com.edesign.paymentsdk.Utils.Parameters;
import com.edesign.paymentsdk.Utils.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/*
  Created by eDesign on 20/04/2017.
  Refund Services class handle the Refund request
  weather user interface smart route or Ez-connect
  process the app request on the bases of app request
  and return the proper response object to the app
*/
public class RefundService {
    private Context context;
    private String authenticationToken = "";
    private RefundRequest req = new RefundRequest();
    private RefundResponse response;
    private Utility utility;

    /*Context used as a parameter*/

    public RefundService(Context context) {
        this.context = context;
    }

     /* Merchant mobile application will invoke this  process method
         This method process the user request and return the response back to the app
         reading url from resource file
         generating secure hash
         getting the query string
         making server request
         getting response from server and set response in addResponse method according to the response
         and return back the response object to the app
     */

    public RefundResponse process(RefundRequest request) {

        /*Here we assign request object to class level because we used or update  RefundRequest different method*/

        req = request;
        response = new RefundResponse();
        utility = new Utility(context);
        String requestSecret = "", secretKey = "";

        /*check for Internet Network is available*/

        if (utility.isNetworkAvailable()) {

             /*Here we check Resources file that provided by user in assets folder
              this method check all url from Generic.txt
              more detail check inside method
            */

            if (readGenericFile() != null && utility.readWordExcludedParameterFromSecureHash() != null && utility.readWordParameterToEncodeForSecureHash() != null && utility.readWordsFromParameterToEncode() != null &&
                    req.getGenericKeyValue().containsKey(Parameters.BYPASS_SECURE_HASH_VALIDATION) && !req.getGenericKeyValue().get(Parameters.BYPASS_SECURE_HASH_VALIDATION).isEmpty() &&
                    req.getGenericKeyValue().get(Parameters.REFUND_URL) != null && req.getGenericKeyValue().containsKey(Parameters.REFUND_URL) && !req.getGenericKeyValue().get(Parameters.REFUND_URL).isEmpty()) {
                try {

                    /*Get authentication token from setPaymentAuthenticationToken method if user set during request*/

                    requestSecret = req.getSecKey().get(Parameters.AUTHENTICATION_TOKEN);
                    System.out.println("Authentication token from request: " + requestSecret);

                     /*We call AsyncTask method to get the secretKey */

                    secretKey = new GetMerchantUrl().execute().get();
                    System.out.println("Authentication token from resource file: " + secretKey);
                } catch (InterruptedException e) {

                     /*In case of Abnormal Error we return execCode is 3 */

                    if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                        response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_THREE);
                        response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                    }
                    if (req.getLogging()) {
                        Log.d("Error", e.toString());
                    }
                } catch (ExecutionException e) {

                 /*In case of Abnormal Error we return execCode is 3 */

                    if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                        response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_THREE);
                        response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                    }
                    if (req.getLogging()) {
                        Log.d("Error", e.toString());
                    }
                }
                if (secretKey != null && !secretKey.isEmpty()) {
                    authenticationToken = secretKey;

                /*If merchants’s URL is not get from server than we give second preference to AuthenticationToken that is set by user*/

                } else if (requestSecret != null && !requestSecret.isEmpty()) {
                    authenticationToken = requestSecret;
                }


                if (authenticationToken != null && !authenticationToken.isEmpty()) {
                    try {

                        /*For EZConnect Payment*/

                        if (request.getPaymentType() != null && request.getPaymentType().equals(Parameters.EZ_CONNECT_VALUE)) {
                            if (EzRefundPayment.refundResEz != null) {
                                EzRefundPayment.refundResEz.clear();
                            }
                            if (EzRefundPayment.timeOutList != null) {
                                EzRefundPayment.timeOutList.clear();
                            }

                             /*This method send request to the payment gateway
                             * more detail check inside method*/

                            sendToServer();

                            /*This method for creating secureHash using response from server match with secureHash
                             * more detail check inside method*/

                            secureHashValidation();

                             /*For Smart Route Payment*/

                        } else if (request.getPaymentType() != null && request.getPaymentType().equals(Parameters.SMART_ROUTE_VALUE)) {
                            if (SmartRefundPayment.refundResSm != null) {
                                SmartRefundPayment.refundResSm.clear();
                            }
                            if (SmartRefundPayment.timeOutList != null) {
                                SmartRefundPayment.timeOutList.clear();
                            }

                             /*This method send request to the payment gateway
                              * more detail check inside method*/

                            sendDataToServer();

                             /*This method for creating secureHash using response from server match with secureHash
                             * more detail check inside method*/

                            secureHashValidation();


                        } else {

                          /*In case when paymentType is not set during request then we return execCode is 6 */

                            if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                                response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_SIX);
                                response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                            }
                        }
                        try {

                              /*In case of EzConnect payment*/

                            if (request.getPaymentType().equals(Parameters.EZ_CONNECT_VALUE)) {
                                if (EzRefundPayment.timeOutList != null && EzRefundPayment.timeOutList.size() != Parameters.NUMERIC_ZERO) {
                                    response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_TWO);
                                    response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                                } else if (EzRefundPayment.refundResEz != null && !EzRefundPayment.refundResEz.containsKey("") && EzRefundPayment.refundResEz.size() != Parameters.NUMERIC_ZERO) {
                                    response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_ZERO);
                                    for (Map.Entry<String, String> entry : EzRefundPayment.refundResEz.entrySet()) {
                                        String key = entry.getKey();
                                        String value = entry.getValue();
                                        response.addResponse(key, value);
                                    }

                                } else {
                                    if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                                        response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_ONE);
                                        response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                                    }
                                }

                                /*Checking for Smart Route Payment*/

                            } else if (request.getPaymentType().equals(Parameters.SMART_ROUTE_VALUE)) {
                                if (SmartRefundPayment.timeOutList != null && SmartRefundPayment.timeOutList.size() != Parameters.NUMERIC_ZERO) {
                                    response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_TWO);
                                    response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                                } else if (SmartRefundPayment.refundResSm != null && !SmartRefundPayment.refundResSm.containsKey("") && SmartRefundPayment.refundResSm.size() != Parameters.NUMERIC_ZERO) {
                                    response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_ZERO);
                                    for (Map.Entry<String, String> entry : SmartRefundPayment.refundResSm.entrySet()) {
                                        String key = entry.getKey();
                                        String value = entry.getValue();
                                        response.addResponse(key, value);
                                    }
                                } else {
                                    if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                                        response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_ONE);
                                        response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);

                                    }

                                }
                            }
                        } catch (Exception e) {

                             /*In case of Abnormal Error we return execCode is 3 */

                            if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                                response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_THREE);
                                response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                            }
                            if (request.getLogging()) {
                                Log.d("Error", e.toString());
                            }
                        }
                    } catch (Exception e) {

                        /*In case of Abnormal Error we return execCode is 3 */

                        if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                            response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_THREE);
                            response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                        }
                        if (request.getLogging()) {
                            Log.d("Error", e.toString());
                        }
                    }

                    /*If Authentication Token is NOT configured in the mobile SDK or the mobile SDK is unable to obtain
                    the authentication token from merchant side than return user execCode is 5*/

                } else {
                    if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                        response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_FIVE);
                        response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                    }
                }

                /*We return exec code 4 if Missing Configuration in resource files*/

            } else {
                if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                    response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_FOUR);
                    response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                }
            }
        } else {
             /*when internet Network is not available then we return execCode is 1 */
            if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_ONE);
                response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
            }
        }
        System.out.println("Response object contain: " + response.getResponse().toString());
        return response;
    }

    //This method for creating secureHash using response from server match with secureHash
    //that is received from server side
    private void secureHashValidation() {
        String generatedSecureHash;
        String receivedSecureHash;

        /*Check first parameter is missing or not in Ez Connect Payment
        * if missing then status code and status description return to user  */

        try {
            if (req.getPaymentType().equals(Parameters.EZ_CONNECT_VALUE)) {
                if (req.getGenericKeyValue().get(Parameters.BYPASS_SECURE_HASH_VALIDATION).equals(Parameters.N_VALUE)) {
                    if (EzRefundPayment.refundResEz != null && EzRefundPayment.refundResEz.containsKey(Parameters.EZ_CONNECT_RESPONSE_STATUS_ONE)) {

                        /*Now that we have the map, order it to generate secure hash and compare it with the received one*/

                        generatedSecureHash = utility.secureHashForResponse(authenticationToken, EzRefundPayment.refundResEz);
                        System.out.println("Generated secureHash: " + generatedSecureHash);

                        receivedSecureHash = EzRefundPayment.refundResEz.get(Parameters.RESPONSE_SECURE_HASH);
                        System.out.println("Received secureHash: " + receivedSecureHash);

                        if (!receivedSecureHash.equals(generatedSecureHash)) {

                            /*IF they are not equal then the response shall not be accepted*/

                            System.out.println("Received secure hash does not equal with generated secure hash");
                            response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.NOT_MATCHED_VALUE);

                        } else {

                             /*Complete the Action get other parameters from res map and do your processes
                            Please refer to The Integration Manual to See The List of The Received Parameters*/

                            System.out.println("Received secureHash is equal with generated secureHash");
                            String PaymentStatus = EzRefundPayment.refundResEz.get(Parameters.EZ_CONNECT_RESPONSE_STATUS_ONE);
                            System.out.println("Refund status: " + PaymentStatus);
                            response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);

                        }

                    } else if (EzRefundPayment.refundResEz != null && !EzRefundPayment.refundResEz.containsKey("") && EzRefundPayment.refundResEz.size() != Parameters.NUMERIC_ZERO) {
                        if (!EzRefundPayment.refundResEz.containsKey(Parameters.RESPONSE_SECURE_HASH)) {
                            response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                        } else {

                              /*Now that we have the map, order it to generate secure hash and compare it with the received one*/

                            generatedSecureHash = utility.secureHashForResponse(authenticationToken, EzRefundPayment.refundResEz);
                            System.out.println("Generated secureHash: " + generatedSecureHash);

                            /*get the received secure hash from res map*/

                            receivedSecureHash = EzRefundPayment.refundResEz.get(Parameters.RESPONSE_SECURE_HASH);
                            System.out.println("Received secureHash: " + receivedSecureHash);

                            if (!receivedSecureHash.equals(generatedSecureHash)) {

                            /*IF they are not equal then the response shall not be accepted*/

                                System.out.println("Received secure hash does not equal with generated secureHash");
                                response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.NOT_MATCHED_VALUE);

                            } else {

                             /*Complete the Action get other parameters from res map and do your processes
                            Please refer to The Integration Manual to See The List of The Received Parameters*/

                                System.out.println("Received secure hash is equal with generated secureHash");
                                String PaymentStatus = EzRefundPayment.refundResEz.get(Parameters.EZ_CONNECT_RESPONSE_STATUS_ONE);
                                System.out.println("Refund status: " + PaymentStatus);
                                response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);

                            }
                        }
                    } else {
                        response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.NOT_MATCHED_VALUE);
                    }


                } else if (req.getGenericKeyValue().get(Parameters.BYPASS_SECURE_HASH_VALIDATION).equals(Parameters.Y_VALUE)) {
                    response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                }

                    /*Check first parameter is missing or not in Smart Payment
                     * if missing then status code and status description return to user */

            } else if (req.getPaymentType().equals(Parameters.SMART_ROUTE_VALUE)) {

                if (req.getGenericKeyValue().get(Parameters.BYPASS_SECURE_HASH_VALIDATION).equals(Parameters.N_VALUE)) {
                    if (!SmartRefundPayment.refundResSm.containsKey("") && SmartRefundPayment.refundResSm.get(Parameters.SMART_ROUTE_RESPONSE_STATUS).equals(Parameters.SMART_ROUTE_SUCCESS)) {

                        /*Now that we have the map, order it to generate secure hash and compare it with the received one*/

                        generatedSecureHash = utility.secureHashForResponse(authenticationToken, SmartRefundPayment.refundResSm);
                        System.out.println("Generated secureHash: " + generatedSecureHash);
                        receivedSecureHash = SmartRefundPayment.refundResSm.get(Parameters.RESPONSE_SECURE_HASH);
                        System.out.println("Received secureHash: " + receivedSecureHash);

                        System.out.println("Message ID: " + SmartRefundPayment.refundResSm.get("Response.MessageID") + " - transaction ID: " + SmartRefundPayment.refundResSm.get("Response.TransactionID") + ", Generated secureHash: " + generatedSecureHash);
                        System.out.println("Message ID: " + SmartRefundPayment.refundResSm.get("Response.MessageID") + " - transaction ID: " + SmartRefundPayment.refundResSm.get("Response.TransactionID") + ", Received secureHash: " + receivedSecureHash);
                        if (!receivedSecureHash.equals(generatedSecureHash)) {

                            /*IF they are not equal then the response shall not be accepted and return ResponseHashMatch is N*/

                            System.out.println("Received secureHash does not equal with generated secureHash");
                            response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.NOT_MATCHED_VALUE);

                        } else {

                              /*Complete the Action get other parameters from res map and do your processes
                            Please refer to The Integration Manual to See The List of The Received Parameters*/

                            System.out.println("Received secureHash is equal with generated secureHash");
                            String PaymentStatus = SmartRefundPayment.refundResSm.get(Parameters.SMART_ROUTE_RESPONSE_STATUS);
                            System.out.println("Refund status: " + PaymentStatus);
                            response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                        }
                    } else if (SmartRefundPayment.refundResSm != null && !SmartRefundPayment.refundResSm.containsKey("") && SmartRefundPayment.refundResSm.size() != Parameters.NUMERIC_ZERO) {
                        if (!SmartRefundPayment.refundResSm.containsKey(Parameters.RESPONSE_SECURE_HASH)) {
                            response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                        } else {

                            /*Now that we have the map, order it to generate secure hash and compare it with the received one*/

                            generatedSecureHash = utility.secureHashForResponse(authenticationToken, SmartRefundPayment.refundResSm);
                            System.out.println("Generated secureHash: " + generatedSecureHash);

                          /*get the received secure hash from res map*/

                            receivedSecureHash = SmartRefundPayment.refundResSm.get(Parameters.RESPONSE_SECURE_HASH);
                            System.out.println("Received secureHash: " + receivedSecureHash);

                            System.out.println("Message ID: " + SmartRefundPayment.refundResSm.get("Response.MessageID") + " - transaction ID: " + SmartRefundPayment.refundResSm.get("Response.TransactionID") + ", Generated secure hash: " + generatedSecureHash);
                            System.out.println("Message ID: " + SmartRefundPayment.refundResSm.get("Response.MessageID") + " - transaction ID: " + SmartRefundPayment.refundResSm.get("Response.TransactionID") + ", Received secure hash: " + receivedSecureHash);
                            if (!receivedSecureHash.equals(generatedSecureHash)) {

                            /*IF they are not equal then the response shall not be accepted and return ResponseHashMatch is N*/

                                System.out.println("Received secureHash does not equal generated secureHash");
                                response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.NOT_MATCHED_VALUE);

                            } else {

                              /*Complete the Action get other parameters from res map and do your processes
                                Please refer to The Integration Manual to See The List of The Received Parameters*/

                                System.out.println("Received SecureHash is equal with generated secureHash");
                                String PaymentStatus = SmartRefundPayment.refundResSm.get(Parameters.SMART_ROUTE_RESPONSE_STATUS);
                                System.out.println("Refund status: " + PaymentStatus);
                                response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                            }
                        }
                    } else {
                        response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.NOT_MATCHED_VALUE);
                    }

                } else if (req.getGenericKeyValue().get(Parameters.BYPASS_SECURE_HASH_VALIDATION).equals(Parameters.Y_VALUE)) {
                    response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                }
            }

        } catch (Exception e) {

              /*In case of Abnormal Error we return execCode is 3 */

            if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_THREE);
                response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
            }
            if (req.getLogging()) {
                Log.d("Error", e.toString());
            }
        }
    }

    //Smart route Refund sendRequest to payment Gateway
    private void sendDataToServer() {

        /*Get RefundURL from resource file */

        String url = req.getGenericKeyValue().get(Parameters.REFUND_URL);
        System.out.println("RefundURL: " + url);
        try {

            /*For secure hash send all parameter with secret key to secureHashForRequest method
            * for secure hash generation check detail inside method*/

            String secureHash = utility.secureHashForRequest(authenticationToken, req.getParameters());
            System.out.println("SecureHash for request: " + secureHash);

            /*In requestQuery we append all request parameter according to format and send it to the payment
            * more detail check inside method */

            StringBuffer requestQuery = utility.requestQuery(secureHash, req.getParameters());
            System.out.println("RequestQuery for server: " + requestQuery);

            /*Here we send request to the SmartRefundPayment gateway */

            SmartRefundPayment.sendRequest(context, authenticationToken, requestQuery, url);
        } catch (Exception e) {

            /*In case of Abnormal Error we return execCode is 3 */

            if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_THREE);
                response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
            }
            if (req.getLogging()) {
                Log.d("Error", e.toString());
            }
        }
    }

    //Ez connect Refund Request send to payment Gateway
    private void sendToServer() {

        /*Get PaymentURL from resource file */

        String url = req.getGenericKeyValue().get(Parameters.REFUND_URL);
        System.out.println("RefundURL: " + url);
        try {

            /*For secure hash send all parameter with secret key to secureHashForRequest method
             * for secure hash generation check detail inside method*/

            String secureHash = utility.secureHashForRequest(authenticationToken, req.getParameters());
            System.out.println("SecureHash for request: " + secureHash);

            /*In requestQuery we append all request parameter according to format and send it to the payment
             * more detail check inside method */

            StringBuffer requestQuery = utility.requestQuery(secureHash, req.getParameters());
            System.out.println("RequestQuery for server: " + requestQuery);

            /*Here we send request to the EzRefundPayment gateway */

            EzRefundPayment.sendRequest(context, authenticationToken, requestQuery, url);
        } catch (Exception e) {

             /*In case of Abnormal Error we return execCode is 3 */

            if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_THREE);
                response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
            }
            if (req.getLogging()) {
                Log.d("Error", e.toString());
            }
        }
    }

    //Method to read Generic.txt file
    private String readGenericFile() {
        StringBuilder buf = new StringBuilder();

        /*in this method we read all url from Generic.txt
        * and split on the basis of equal and save in addGeneric method on key value
        * key for url name and value for url link*/

        try {
            ArrayList<String> output = new ArrayList<String>();
            try {
                InputStream is = context.getAssets().open(Parameters.GENERIC_FILE);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.add(line);
                    buf.append(line);
                }
                reader.close();
                for (String pair : output) {
                    String[] nameValue = pair.split("=");
                    String name = nameValue[0].trim();
                    String value = "";
                    try {
                        value = nameValue[1].trim();
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        req.addGeneric(name, value);
                    }

                    req.addGeneric(name, value);
                }
                return buf.toString();
            } catch (IndexOutOfBoundsException e) {
                 /*In case of Abnormal Error we return execCode is 3 */
                response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_THREE);
                response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
                if (req.getLogging()) {
                    Log.d("Error", e.toString());
                }
            }
        } catch (IOException e) {

             /*In case of Abnormal Error we return execCode is 4 */

            if (!response.getResponse().containsKey(Parameters.EXEC_CODE)) {
                response.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_FOUR);
                response.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE);
            }
            if (req.getLogging()) {
                Log.d("Error", e.toString());
            }

        }
        return null;
    }

    //Method to get Secret key string from MerchantUrl
    private class GetMerchantUrl extends AsyncTask<String, Integer, String> {

        /*GetMerchantUrl for getting secret key from server using resource file MerchantAuthenticationTokenFetchURL*/

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {

                /*getGenericKeyValue return secret key
                  Generic file in assets folder provide url using MerchantAuthenticationTokenFetchURL
                */

                URL url = new URL(req.getGenericKeyValue().get(Parameters.MERCHANT_AUTHENTICATION_URL));
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String line = null;
                while ((line = in.readLine()) != null) {
                    //get lines
                    result += line;
                }
                in.close();
            } catch (MalformedURLException e) {
                if (req.getLogging()) {
                    Log.d("Error", e.toString());
                }
            } catch (IOException e) {
                if (req.getLogging()) {
                    Log.d("Error", e.toString());
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


}
