package com.edesign.paymentsdk.Utils

import android.content.Context
import com.edesign.paymentsdk.version2.CountryModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * Utility class which return decimal format and currency code from countrylist.json file in asset
 */

class CountryCodeUtils {

    companion object{
        fun getCountryDecimalFormat(context: Context,currency:String): String {
            val codeList = ArrayList<CountryModel>()
            var jsonString = ""
            var decimalFormat = ""
            try {
                jsonString = context.assets.open("countrylist.json")
                    .bufferedReader()
                    .use { it.readText() }
            } catch (ioException: IOException) {

            }
            val listCountryType = object : TypeToken<List<CountryModel>>() {}.type
            codeList.addAll(Gson().fromJson(jsonString, listCountryType))
            for (i in codeList.indices){
                if (codeList[i].UN_Code == currency){
                    decimalFormat=codeList[i].currency_decimal_format.toString()
                    break
                }
            }
            return decimalFormat
        }

        fun getCountryCurrencyCode(context: Context,currency:String): String {
            val codeList = ArrayList<CountryModel>()
            var jsonString = ""
            var currency_code = ""
            try {
                jsonString = context.assets.open("countrylist.json")
                    .bufferedReader()
                    .use { it.readText() }
            } catch (ioException: IOException) {

            }
            val listCountryType = object : TypeToken<List<CountryModel>>() {}.type
            codeList.addAll(Gson().fromJson(jsonString, listCountryType))
            for (i in codeList.indices){
                if (codeList[i].UN_Code == currency){
                    currency_code= codeList[i].currency_code
                    break
                }
            }
            return currency_code
        }



    }
}