package com.edesign.paymentsdk.version2.savedCardAPI

import android.content.Context
import android.net.Uri
import android.util.Log
import com.edesign.paymentsdk.Utils.Parameters
import com.edesign.paymentsdk.Utils.Utility
import com.edesign.paymentsdk.version2.api.ApiClient
import com.edesign.paymentsdk.version2.api.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * Created by Airrchip on 20/04/2017.
 * SavedCard Services class handle the SavedCard request
 * process the app request on the bases of app request
 * and return the proper response object to the app
 */
class SavedCardService
/*Context used as a parameter*/(private val context: Context) {
    private var authenticationToken: String? = ""
    private var req = SavedCardRequest()
    private var response: SavedCardResponse? = null
    private var utility: Utility? = null
    private var param: SavedCardCallback? = null

    /* Merchant mobile application will invoke this  process method
         This method process the user request and return the response back to the app
         reading url from resource file
         generating secure hash
         getting the query string
         making server request
         getting response from server and set response in addResponse method according to the response
         and return back the response object to the app
     */
    fun process(requestSTS: SavedCardRequest, param1: SavedCardCallback) {

        /*Here we assign request object to class level because we used or update ApproveRequest different method*/
        req = requestSTS
        response = SavedCardResponse()
        utility = Utility(context)
        param = param1
        var requestSecret: String? = ""
        var secretKey = ""

        /*check for Internet Network is available*/
        if (utility!!.isNetworkAvailable) {

            if (readGenericFile() != null &&
                readWordsFromParameterToEncrypt() != null &&
                utility!!.readWordExcludedParameterFromSecureHash() != null &&
                utility!!.readWordParameterToEncodeForSecureHash() != null &&
                utility!!.readWordsFromParameterToEncode() != null &&
                req.getGenericKeyValue().containsKey(Parameters.SAVED_CARD_END_POINT) &&
                !req.getGenericKeyValue()[Parameters.SAVED_CARD_END_POINT]!!.isNullOrEmpty() &&
                req.getGenericKeyValue().containsKey(Parameters.BASE_URL) &&
                !req.getGenericKeyValue()[Parameters.BASE_URL]!!.isNullOrEmpty()
            ) {
                /*Get authentication token from setPaymentAuthenticationToken method if user set during request*/
                requestSecret =
                    req.getSecKey()[Parameters.AUTHENTICATION_TOKEN]


                /*We give preference to the secret key that is loaded from the merchants’s URL */
                if (secretKey != null && !secretKey.isEmpty()) {
                    authenticationToken = secretKey

                    /*If merchants’s URL is not get from server than we give second preference to AuthenticationToken that is set by user*/
                } else if (requestSecret != null && !requestSecret.isEmpty()) {
                    authenticationToken = requestSecret
                }


                /*For secure hash send all parameter with secret key to secureHashForRequest method
             * for secure hash generation check detail inside method*/
                val secureHash =
                    utility!!.secureHashForRequest(authenticationToken, req.getParameters())
                req.add("SecureHash", secureHash)

                if (authenticationToken != null && !authenticationToken!!.isEmpty()) {
                    try {
                        val apiInterface: ApiInterface =
                            ApiClient.getClient(req.getGenericKeyValue()[Parameters.BASE_URL]!!)!!
                                .create(ApiInterface::class.java)
                        var call = apiInterface.getSavedCardApi(
                            req.getGenericKeyValue()[Parameters.PAYMENT_END_POINT]!!,
                            authenticationToken!!,
                            req.getParameters()
                        )
                        call.enqueue(object : Callback<okhttp3.ResponseBody> {
                            override fun onResponse(
                                call: Call<okhttp3.ResponseBody>,
                                res: Response<okhttp3.ResponseBody>
                            ) {
                                if (res.isSuccessful) {
                                    if (res.body() != null) {
                                        val html = res.body()!!.string()
                                        val uri = Uri.parse(req.getGenericKeyValue()[Parameters.BASE_URL]+"?"+html)
                                        val args :Set<String> = uri.queryParameterNames
                                        for (elem in args) {
                                            var value=uri.getQueryParameter(elem)
                                            response!!.addResponse(elem, value!!)

                                        }

                                    }
                                }


                                param!!.onResponse(response!!)
                            }

                            override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
                                //t.printStackTrace()
                                /*In case of Abnormal Error we return execCode is 3 */
                                if (!response!!.response.containsKey(Parameters.EXEC_CODE)) {
                                    response!!.addResponse(
                                        Parameters.EXEC_CODE,
                                        Parameters.EXEC_CODE_THREE
                                    )
                                    response!!.addResponse(
                                        Parameters.RESPONSE_HASH_MATCH,
                                        Parameters.MATCHED_VALUE
                                    )
                                }
                                if (req.logging) {
                                    Log.d("Error", t.toString())
                                }
                                param!!.onResponse(response!!)
                            }
                        })
                    } catch (e: Exception) {


                        /*In case of Abnormal Error we return execCode is 3 */
                        if (!response!!.response.containsKey(Parameters.EXEC_CODE)) {
                            response!!.addResponse(
                                Parameters.EXEC_CODE,
                                Parameters.EXEC_CODE_THREE
                            )
                            response!!.addResponse(
                                Parameters.RESPONSE_HASH_MATCH,
                                Parameters.MATCHED_VALUE
                            )
                        }
                        if (req.logging) {
                            Log.d("Error", e.toString())
                        }
                        param!!.onResponse(response!!)
                    }


                    /*If Authentication Token is NOT configured in the mobile SDK or the mobile SDK is unable to obtain
                 the authentication token from merchant side than return user execCode is 5*/
                } else {
                    if (!response!!.response.containsKey(Parameters.EXEC_CODE)) {
                        response!!.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_FIVE)
                        response!!.addResponse(
                            Parameters.RESPONSE_HASH_MATCH,
                            Parameters.MATCHED_VALUE
                        )
                        param!!.onResponse(response!!)
                    }
                }


                /*We return exec code 4 if Missing Configuration in resource files*/
            } else {
                if (!response!!.response.containsKey(Parameters.EXEC_CODE)) {
                    response!!.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_FOUR)
                    response!!.addResponse(
                        Parameters.RESPONSE_HASH_MATCH,
                        Parameters.MATCHED_VALUE
                    )
                    param!!.onResponse(response!!)
                }
            }

        } else {

            /*when Internet Network is not available then we return execCode is 1 */
            if (!response!!.response.containsKey(Parameters.EXEC_CODE)) {
                response!!.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_ONE)
                response!!.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE)

                param!!.onResponse(response!!)
            }
        }
        println("Response object contain: " + response!!.response.toString())

    }

    private fun readGenericFile(): String? {
        val buf = StringBuilder()

        /*in this method we read all url from Generic.txt
        * and split on the basis of equal and save in addGeneric method on key value
        * key for url name and value for url link*/
        try {
            val output = ArrayList<String>()
            try {
                val `is` = context.assets.open(Parameters.GENERIC_FILE)
                val reader = BufferedReader(InputStreamReader(`is`, "UTF8"))
                var line = reader.readLine()
                while (line != null) {
                    if (line != null) {
                        output.add(line)
                        buf.append(line)
                    }
                    line = reader.readLine()
                }
                reader.close()
                for (pair in output) {
                    val nameValue = pair.split("=").toTypedArray()
                    val name = nameValue[0].trim { it <= ' ' }
                    var value = ""
                    try {
                        value = nameValue[1].trim { it <= ' ' }
                    } catch (e: java.lang.IndexOutOfBoundsException) {
                        e.printStackTrace()
                        req.addGeneric(name, value)
                    }
                    req.addGeneric(name, value)
                }
                return buf.toString()
            } catch (e: java.lang.IndexOutOfBoundsException) {

                /*In case of Abnormal Error we return execCode is 3 */
                response!!.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_THREE)
                response!!.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE)
                if (req.logging) {
                    Log.d("Error", e.toString())
                }
                param!!.onResponse(response!!)
            }
        } catch (e: IOException) {
            /*In case of Abnormal Error we return execCode is 4 */
            if (!response!!.response.containsKey(Parameters.EXEC_CODE)) {
                response!!.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_FOUR)
                response!!.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE)
            }
            if (req.logging) {
                Log.d("Error", e.toString())
            }
            param!!.onResponse(response!!)
        }
        return null
    }


    //Method to read ParametersToEncrypt.txt file
    private fun readWordsFromParameterToEncrypt(): String? {
        val buf = StringBuilder()

        /*This method read file ParametersToEncrypt.txt line by line and put all value inside outputParameterToEncrypt method*/
        try {
            val `is` = context.assets.open(Parameters.PARAMETER_TO_ENCRYPT_FILE)
            val reader = BufferedReader(InputStreamReader(`is`, "UTF8"))
            var line = reader.readLine()
            while (line != null) {
                line = reader.readLine()
                if (line != null) {
                    buf.append(line)
                    req.outputParameterToEncrypt(line)
                }
            }

            reader.close()
            return buf.toString()
        } catch (e: IOException) {

            /*In case of Abnormal Error we return execCode is 4 */
            if (!response!!.response.containsKey(Parameters.EXEC_CODE)) {
                response!!.addResponse(Parameters.EXEC_CODE, Parameters.EXEC_CODE_FOUR)
                response!!.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE)
            }
            if (req.logging) {
                Log.d("Error", e.toString())
            }
            param!!.onResponse(response!!)
        }
        return null
    }

}