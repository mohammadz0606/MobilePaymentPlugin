package com.edesign.paymentsdk.version2.refund

import android.content.Context
import android.util.Log
import com.edesign.paymentsdk.Refund.RefundRequest
import com.edesign.paymentsdk.Refund.RefundResponse
import com.edesign.paymentsdk.Utils.ErrorCodesMessage
import com.edesign.paymentsdk.Utils.ErrorCodesMessage.Companion.kErrorCode
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
import java.util.*


/**
 * Created by Airrchip on 20/04/2017.
 * AuthenticatePayer Services class handle the AuthenticatePayer request
 * process the app request on the bases of app request
 * and return the proper response object to the app
 */
class SmartRouteRefundService
/*Context used as a parameter*/(private val context: Context) {
    private var authenticationToken: String? = ""
    private var req = RefundRequest()
    private var response: RefundResponse? = null
    private var utility: Utility? = null
    private var param: RefundCallback? = null

    /* Merchant mobile application will invoke this  process method
         This method process the user request and return the response back to the app
         reading url from resource file
         generating secure hash
         getting the query string
         making server request
         getting response from server and set response in addResponse method according to the response
         and return back the response object to the app
     */
    fun process(requestSTS: RefundRequest, param1: RefundCallback) {

        /*Here we assign request object to class level because we used or update ApproveRequest different method*/
        req = requestSTS
        response = RefundResponse()
        utility = Utility(context)
        param = param1
        var requestSecret: String? = ""
        var secretKey = ""

        /*check for Internet Network is available*/
        if (utility!!.isNetworkAvailable) {

            if (readGenericFile() != null &&
                utility!!.readWordExcludedParameterFromSecureHash() != null &&
                utility!!.readWordParameterToEncodeForSecureHash() != null &&
                utility!!.readWordsFromParameterToEncode() != null &&
                req.getGenericKeyValue().containsKey(Parameters.BYPASS_SECURE_HASH_VALIDATION) &&
                !req.getGenericKeyValue()[Parameters.BYPASS_SECURE_HASH_VALIDATION]!!.isEmpty() &&
                req.getGenericKeyValue().containsKey(Parameters.REFUND_END_POINT) &&
                !req.getGenericKeyValue()[Parameters.REFUND_END_POINT]!!.isNullOrEmpty() &&
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
                        var call = apiInterface.mPaymentApi(
                            req.getGenericKeyValue()[Parameters.REFUND_END_POINT]!!,
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
                                        val pairs = html.split("&")
                                        try {
                                            for (i in pairs.indices) {
                                                val nameValue = pairs[i].split("=").toTypedArray()
                                                val name = nameValue[0] // first element is the name
                                                var value = ""
                                                try {
                                                    if (nameValue.size > 1 && !nameValue[1].isEmpty()) {
                                                        value = nameValue[1]
                                                    }
                                                } catch (e: IndexOutOfBoundsException) {
                                                    e.printStackTrace()
                                                }
                                                response!!.addResponse(name, value)
                                            }

                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }


                                /*This method for creating secureHash using response from server match with secureHash
                                     * more detail check inside method*/
                                secureHashValidation()
                                param!!.onResponse(response!!)
                            }

                            override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
                                //t.printStackTrace()
                                /*In case of Abnormal Error */
                                if (!response!!.response.containsKey(kErrorCode)) {
                                    response!!.addResponse(kErrorCode, ErrorCodesMessage.kAbnormalErrorCode)
                                    response!!.addResponse(
                                        ErrorCodesMessage.kErrorDescription,t.printStackTrace().toString())

                                }
                                if (req.logging) {
                                    Log.d("Error", t.toString())
                                }
                                param!!.onResponse(response!!)
                            }
                        })
                    } catch (e: Exception) {


                        /*In case of Abnormal Error */
                        if (!response!!.response.containsKey(kErrorCode)) {
                            response!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kAbnormalErrorCode)
                            response!!.addResponse(
                                ErrorCodesMessage.kErrorDescription,
                                e.printStackTrace().toString())

                        }
                        if (req.logging) {
                            Log.d("Error", e.toString())
                        }
                        param!!.onResponse(response!!)
                    }


                    /*If Authentication Token is NOT configured in the mobile SDK or the mobile SDK is unable to obtain
                 the authentication token from merchant side*/
                } else {
                    if (!response!!.response.containsKey(kErrorCode)) {
                        response!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kSDKNotInitializedErrorCode)
                        response!!.addResponse(
                            ErrorCodesMessage.kErrorDescription,
                            ErrorCodesMessage.kSDKNotInitialiazedErrorMessage)
                        param!!.onResponse(response!!)
                    }
                }


                /*if Missing Configuration in resource files*/
            } else {
                if (!response!!.response.containsKey(kErrorCode)) {
                    response!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kConfigFileErrorCode)
                    response!!.addResponse(
                        ErrorCodesMessage.kErrorDescription,
                        ErrorCodesMessage.kConfigFileMessage)

                    param!!.onResponse(response!!)
                }
            }

        } else {

            /*when Internet Network is not available  */
            if (!response!!.response.containsKey(kErrorCode)) {
                response!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kNetworkConnection)
                response!!.addResponse(
                    ErrorCodesMessage.kErrorDescription,
                    ErrorCodesMessage.kNetworkConnectionMessage)


                param!!.onResponse(response!!)
            }
        }
        println("Response object contain: " + response!!.response.toString())

    }

    //This method for creating secureHash using response from server match with secureHash
    //that is received from server side
    fun secureHashValidation() {
        val generatedSecureHash: String
        val receivedSecureHash: String?

        if (req.getGenericKeyValue()[Parameters.BYPASS_SECURE_HASH_VALIDATION] == Parameters.N_VALUE) {
            /*Now that we have the map, order it to generate secure hash and compare it with the received one*/
            generatedSecureHash =
                utility!!.secureHashForResponse(authenticationToken, response!!.response)
            println("Generated secureHash: $generatedSecureHash")

            receivedSecureHash = response!!.response[Parameters.RESPONSE_SECURE_HASH]
            println("receivedSecureHash secureHash: $receivedSecureHash")

            if (receivedSecureHash != generatedSecureHash) {

                /*IF they are not equal then the response shall not be accepted*/
                println("Received secure hash does not equal with generated secure hash")
                response!!.addResponse(
                    Parameters.RESPONSE_HASH_MATCH,
                    Parameters.NOT_MATCHED_VALUE
                )
            } else {

                /*Complete the Action get other parameters from res map and do your processes
                                Please refer to The Integration Manual to See The List of The Received Parameters*/
                println("Received secureHash is equal with generated secureHash")

                response!!.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE)
            }

        } else if (req.getGenericKeyValue()[Parameters.BYPASS_SECURE_HASH_VALIDATION] == Parameters.Y_VALUE) {
            response!!.addResponse(Parameters.RESPONSE_HASH_MATCH, Parameters.MATCHED_VALUE)
        }


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
                response!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kGenericFileError)
                response!!.addResponse(
                    ErrorCodesMessage.kErrorDescription,
                    ErrorCodesMessage.kGenericFileMessage)

                if (req.logging) {
                    Log.d("Error", e.toString())
                }
                param!!.onResponse(response!!)
            }
        } catch (e: IOException) {
            /*In case of Abnormal Error  */
            if (!response!!.response.containsKey(kErrorCode)) {
                response!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kGenericFileError)
                response!!.addResponse(
                    ErrorCodesMessage.kErrorDescription,
                    ErrorCodesMessage.kGenericFileMessage)

            }
            if (req.logging) {
                Log.d("Error", e.toString())
            }
            param!!.onResponse(response!!)
        }
        return null
    }

}