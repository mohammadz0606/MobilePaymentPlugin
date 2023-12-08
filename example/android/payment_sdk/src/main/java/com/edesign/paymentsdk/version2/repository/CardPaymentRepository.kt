package com.edesign.paymentsdk.version2.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.edesign.paymentsdk.BuildConfig
import com.edesign.paymentsdk.Utils.DsIdMatcher
import com.edesign.paymentsdk.Utils.Parameters
import com.edesign.paymentsdk.version2.App
import com.edesign.paymentsdk.version2.authenticatePayer.AuthenticatePayerRequest
import com.edesign.paymentsdk.version2.exceptions.InvalidPaymentCardNumber
import com.edesign.paymentsdk.version2.model.AuthenticateRequestParamData
import com.edesign.paymentsdk.version2.model.ThreeDSData

import com.netcetera.threeds.sdk.ThreeDS2ServiceInstance
import com.netcetera.threeds.sdk.api.ThreeDS2Service
import com.netcetera.threeds.sdk.api.configparameters.ConfigParameters
import com.netcetera.threeds.sdk.api.configparameters.builder.ConfigurationBuilder
import com.netcetera.threeds.sdk.api.configparameters.builder.SchemeConfiguration
import com.netcetera.threeds.sdk.api.exceptions.InvalidInputException
import com.netcetera.threeds.sdk.api.exceptions.SDKAlreadyInitializedException
import com.netcetera.threeds.sdk.api.exceptions.SDKNotInitializedException
import com.netcetera.threeds.sdk.api.exceptions.SDKRuntimeException
import com.netcetera.threeds.sdk.api.security.Warning
import com.netcetera.threeds.sdk.api.transaction.Transaction
import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.IndexOutOfBoundsException
import java.lang.StringBuilder
import java.util.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class CardPaymentRepository {
    var mutableLiveData: MutableLiveData<ThreeDSData> = MutableLiveData()
    var authenticationRequestParametersLiveData: MutableLiveData<AuthenticateRequestParamData> =
        MutableLiveData()
    var mutableTransaction: MutableLiveData<Transaction> = MutableLiveData()
    private var req = AuthenticatePayerRequest()


    fun initialize3ds() {
        var a = ThreeDSData()


        try {

            CoroutineScope(Dispatchers.IO).launch {


                val configParameters: ConfigParameters = createConfigParameters()!!
                val deviceLocale: String = "en"
                val uiCustomization: Map<UiCustomization.UiCustomizationType, UiCustomization> =
                    createUiCustomization()!!
                val threeDS2Service = ThreeDS2ServiceInstance.get()
                threeDS2Service.initialize(
                    App.appContext!!,
                    configParameters,
                    deviceLocale,
                    uiCustomization,
                    object : ThreeDS2Service.InitializationCallback {
                        override fun onCompleted() {
                            a.success(threeDS2Service)
                        }

                        override fun onError(throwable: Throwable) {
                            throwable.printStackTrace()
                            when (throwable) {
                                is InvalidInputException, is SDKRuntimeException -> {
                                    a.error(throwable)
                                }

                                is SDKAlreadyInitializedException -> {
                                    a.success(threeDS2Service)
                                }
                            }
                        }
                    }

                )


                withContext(Dispatchers.Main) {
                    mutableLiveData.value = a

                }

            }

        } catch (e: Exception) {
            a.error(e)
            mutableLiveData.value = a
        }
    }

    fun authenticateRequestParameter(cardNumber: String, type: String, threeDSVersion: String) {
        var a = AuthenticateRequestParamData()
        var directoryServerID = ""
        try {
            try {
                if (type.isNullOrEmpty()) {
                    directoryServerID =
                        DsIdMatcher.directoryServerIdForCard(App.appContext!!, cardNumber)
                } else {
                    directoryServerID = DsIdMatcher.directoryServerIdForType(App.appContext!!, type)
                }
            } catch (e: InvalidPaymentCardNumber) {
                a.error(e)
            }
//            val messageVersion = "2.2.0"
            val messageVersion = threeDSVersion

            var transaction = mutableLiveData.value!!.getthreeDS2ServiceInstance()!!
                .createTransaction(directoryServerID, messageVersion)
            mutableTransaction.value = transaction
            try {
                val authenticationRequestParameters = transaction.authenticationRequestParameters
                a.success(authenticationRequestParameters)
                authenticationRequestParametersLiveData.value = a
                // Send request with AuthenticationRequestParameters to the 3DS Server
            } catch (e: SDKRuntimeException) {
                e.printStackTrace()
                a.error(e)
                transaction.close()
                authenticationRequestParametersLiveData.value = a

            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            when (exception) {
                is InvalidInputException, is SDKRuntimeException, is SDKNotInitializedException -> {
                    a.error(exception)
                    authenticationRequestParametersLiveData.value = a

                }
            }
        }

    }

    private fun createUiCustomization(): Map<UiCustomization.UiCustomizationType, UiCustomization>? {

        val uiCustomizationMap = hashMapOf<UiCustomization.UiCustomizationType, UiCustomization>()
        uiCustomizationMap.apply {
            put(UiCustomization.UiCustomizationType.DEFAULT, UiCustomization())
        }
        return uiCustomizationMap
    }

    private fun createConfigParameters(): ConfigParameters? {
        val assetManager = App.appContext!!.assets
        var isProduction = false
        if (readGenericFile(App.appContext!!) != null && req.getGenericKeyValue()
                .containsKey(Parameters.PRODUCTION) &&
            !req.getGenericKeyValue()[Parameters.PRODUCTION]!!.isEmpty()
        ) {
            try {
                isProduction = req.getGenericKeyValue()[Parameters.PRODUCTION]!!.toBoolean()
            } catch (e: Exception) {

            }
        }
        return if (isProduction) {
            ConfigurationBuilder()
                .apiKey(BuildConfig.NCA_THREE_DS_LICENSE) //Mastercard configuration
                .build()

        } else {
            ConfigurationBuilder()
                .apiKey(BuildConfig.NCA_THREE_DS_LICENSE) //Mastercard configuration
                .configureScheme(
                    SchemeConfiguration.mastercardSchemeConfiguration()
                        .encryptionPublicKeyFromAssetCertificate(
                            assetManager,
                            "nca_demo_mastercard_encryption.crt"
                        )
                        .rootPublicKeyFromAssetCertificate(assetManager, "certificate.pem")
                        .build()
                ) //Visa configuration
                .configureScheme(
                    SchemeConfiguration.visaSchemeConfiguration()
                        .encryptionPublicKeyFromAssetCertificate(
                            assetManager,
                            "nca_demo_visa_encryption.crt"
                        )
                        .rootPublicKeyFromAssetCertificate(assetManager, "certificate.pem")
//                    .encryptionPublicKeyFromAssetCertificate(assetManager, "pbds_rsa.p7b")
//                    .rootPublicKeyFromAssetCertificate(assetManager, "VSTS.pem")
                        .build()
                )
                .build()
        }
    }


    fun getThreeDsLiveData(): MutableLiveData<ThreeDSData> {
        return mutableLiveData
    }

    fun getAuthenticateRequestParamLiveData(): MutableLiveData<AuthenticateRequestParamData> {
        return authenticationRequestParametersLiveData
    }

    fun getTransactionLiveData(): MutableLiveData<Transaction> {
        return mutableTransaction
    }


    private fun readGenericFile(context: Context): String? {
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
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                        req.addGeneric(name, value)
                    }
                    req.addGeneric(name, value)
                }
                return buf.toString()
            } catch (e: IndexOutOfBoundsException) {


            }
        } catch (e: IOException) {

        }
        return null
    }

}