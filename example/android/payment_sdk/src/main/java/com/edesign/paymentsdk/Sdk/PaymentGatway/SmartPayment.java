package com.edesign.paymentsdk.Sdk.PaymentGatway;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.edesign.paymentsdk.Sdk.Security.NoSSLv3SocketFactory;
import com.edesign.paymentsdk.Sdk.Security.NullHostNameVerifier;
import com.edesign.paymentsdk.Utils.Parameters;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by eDesign on 19/04/2017.
 */

public class SmartPayment {
    static Context ctx;
    static String resUrl;
    static StringBuffer requestQuery;
    private static NoSSLv3SocketFactory NoSSLv3Factory;
    public static String SECRET_KEY;
    public static Map<String, String> res;
    public static ArrayList<String> timeOutList;

    public static void sendRequest(final Context ctx1, String SECRET_KEY1, StringBuffer requestQuery1, String url) {
        ctx = ctx1;
        requestQuery = requestQuery1;
        SECRET_KEY = SECRET_KEY1;
        resUrl = url;
        final SmartPayment.AsyncCallWS2 task = new SmartPayment.AsyncCallWS2();
        res = new TreeMap<String, String>();
        timeOutList = new ArrayList<String>();
        try {
            task.execute().get(30000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            timeOutList.add(Parameters.TIME_OUT_ERROR);
        }


    }
    private static class AsyncCallWS2 extends AsyncTask<String, Void, Void> {

        final ProgressDialog dialog = new ProgressDialog(ctx);

        @Override
        protected Void doInBackground(String... params) {

            postPayment();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    /* Smart Route Payment gateway*/
    public static void postPayment() {
        String outputString = "";
        try {
            URL url = new URL(resUrl);
            SSLContext sslcontext = null;
            try {
                ProviderInstaller.installIfNeeded(ctx);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            try {
                HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
                //sslcontext = SSLContext.getInstance("TLSv1.2");
                sslcontext = SSLContext.getInstance("TLSv1.2");
                try {
                    sslcontext.init(null,
                            null,
                            null);
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
                NoSSLv3Factory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            try {
//            URLConnection conn =url.openConnection();
                HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter writer = null;

                writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");

//         write parameters
                writer.write(requestQuery.toString());
                writer.flush();
                // Get the response
                StringBuffer output = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line;

                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                writer.close();
                reader.close();
//         Output the response

                System.out.println("Original Response From Server is: " + output.toString());
                // this string is formatted as a "Query String" - name=value&name2=value2.......
                outputString = output.toString();
                Log.d("outputString", outputString);
            } catch (Exception e) {
                e.printStackTrace();
                res.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // To read the output string you might want to split it
        // on '&' to get pairs then on '=' to get name and value

        // and for a better and ease on verifying secure hash you should put them in a TreeMap
        try {
            String[] pairs = outputString.split("&");
            // now we have separated the pairs from each other {"name1=value1","name2=value2",....}
            try {
                for (String pair : pairs) {
                    // now we have separated the pair to {"name","value"}
                    String[] nameValue = pair.split("=");
                    String name = nameValue[0];// first element is the name
                    String value = "";
                    try {
                        if (nameValue.length>1 &&!nameValue[1].isEmpty()) {
                            value = nameValue[1];
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        res.put(name, value);
                    }
                    res.put(name, value);
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            //update key if exist estn
            String value = res.get("Response.estn");
            if (value != null) {
                res.put("Response.estn", res.get("Response.estn") + "=");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exception", e.toString());
        }
    }

}
