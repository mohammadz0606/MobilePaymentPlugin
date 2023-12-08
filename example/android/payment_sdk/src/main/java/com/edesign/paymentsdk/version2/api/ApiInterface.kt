package com.edesign.paymentsdk.version2.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {

    @FormUrlEncoded
    @POST("{controller}")
    fun mPaymentApi(
        @Path(value = "controller", encoded = true) controller: String,
        @FieldMap data:Map<String, String>
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("{controller}")
    fun getCardTypeApi(
        @Path(value = "controller", encoded = true) controller: String,
        @Header("AuthenticationToken")  authenticationToken:String,
        @FieldMap data:Map<String, String>
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("{controller}")
    fun getSavedCardApi(
        @Path(value = "controller", encoded = true) controller: String,
        @Header("AuthenticationToken")  authenticationToken:String,
        @FieldMap data:Map<String, String>
    ): Call<ResponseBody>


    @FormUrlEncoded
    @POST("{controller}")
    fun get3DSVersionApi(
        @Path(value = "controller", encoded = true) controller: String,
        @Header("AuthenticationToken")  authenticationToken:String,
        @FieldMap data:Map<String, String>
    ): Call<ResponseBody>
}