package com.edesign.paymentsdk.version2.api

import android.content.Context
import android.util.Log
import com.edesign.paymentsdk.Utils.Parameters
import com.edesign.paymentsdk.version2.App
import com.edesign.paymentsdk.version2.authenticatePayer.AuthenticatePayerRequest
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit


class ApiClient {


    companion object {

        private var req = AuthenticatePayerRequest()

        private val TAG = "ApiClient"

        private val NO_OF_LOG_CHAR = 1000

        private var timeout : Long = 120

        private var sRetrofitClient: Retrofit? = null

        private val sDispatcher: Dispatcher? = null
        var gson = GsonBuilder()
            .setLenient()
            .create()


        fun getClient(baseURL:String): Retrofit? {
            if (sRetrofitClient == null) {
                sRetrofitClient = Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(getOkHttpClientBuilder().build())
                    .build()
            }
            return sRetrofitClient
        }

        fun getDispatcher(): Dispatcher {
            return sDispatcher ?: Dispatcher()
        }

        private fun getOkHttpClientBuilder(): OkHttpClient.Builder {
            if (readGenericFile(App.appContext!!) != null){
                if (req.getGenericKeyValue().containsKey(Parameters.TIMEOUT) &&
                    !req.getGenericKeyValue()[Parameters.TIMEOUT]!!.isEmpty())
                    timeout=req.getGenericKeyValue()[Parameters.TIMEOUT]!!.toLong()

            }
            val oktHttpClientBuilder = OkHttpClient.Builder()
                .readTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .cookieJar(SessionCookieJar())
            oktHttpClientBuilder.dispatcher(getDispatcher())

            oktHttpClientBuilder.addInterceptor(getHttpLoggingInterceptor())
            oktHttpClientBuilder.addInterceptor { chain ->
                var request = chain.request()
                printPostmanFormattedLog(request)
                var response = chain.proceed(request)
                response
            }

            return oktHttpClientBuilder
        }


        fun getBaseUrl(data:String):String{
            var baseUrl=""
            try {
                var end=data.indexOf("/",data.indexOf("://")+4)
                val url = URL(data)
                baseUrl = url.getProtocol() + "://" + url.getHost()
            } catch (e: MalformedURLException) {
                // do something
            }
            return baseUrl
        }

        private fun getHttpLoggingInterceptor(): Interceptor {
            val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    if (message.length > NO_OF_LOG_CHAR) {
                        for (noOfLogs in 0..message.length / NO_OF_LOG_CHAR) {
                            if (noOfLogs * NO_OF_LOG_CHAR + NO_OF_LOG_CHAR < message.length) {
                                Log.e(
                                    TAG, message.substring(
                                        noOfLogs * NO_OF_LOG_CHAR,
                                        noOfLogs * NO_OF_LOG_CHAR + NO_OF_LOG_CHAR
                                    )
                                )
                            } else {
                                Log.e(
                                    TAG,
                                    message.substring(noOfLogs * NO_OF_LOG_CHAR, message.length)
                                )
                            }
                        }
                    } else {
                        Log.e(TAG, message)
                    }
                }
            })
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return loggingInterceptor
        }


        private fun printPostmanFormattedLog(request: Request) {
            try {
                val allParams: String
                allParams = if (request.method == "GET" || request.method == "DELETE") {
                    request.url.toString().substring(
                        request.url.toString().indexOf("?") + 1,
                        request.url.toString().length
                    )
                } else {
                    val buffer = Buffer()
                    request.body!!.writeTo(buffer)
                    buffer.readString(Charset.forName("UTF-8"))
                }
                val params =
                    allParams.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val paramsString = StringBuilder("\n")
                for (param in params) {
                    paramsString.append(decode(param.replace("=", ":")))
                    paramsString.append("\n")
                }
                Log.e(TAG, paramsString.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        private fun decode(url: String): String {
            return try {
                var prevURL = ""
                var decodeURL = url
                while (prevURL != decodeURL) {
                    prevURL = decodeURL
                    decodeURL = URLDecoder.decode(decodeURL, "UTF-8")
                }
                decodeURL
            } catch (e: UnsupportedEncodingException) {
                "Issue while decoding" + e.message
            }
        }

        private fun readGenericFile(context:Context): String? {
            val buf = java.lang.StringBuilder()

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


                }
            } catch (e: IOException) {

            }
            return null
        }



    }

    private class SessionCookieJar : CookieJar {


        private var cookies: List<Cookie>? = null

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            this.cookies = ArrayList(cookies)
        }


        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            if (cookies != null) {
                return cookies as List<Cookie>
            }
            return Collections.emptyList()
        }


    }





}