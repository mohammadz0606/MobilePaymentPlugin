package com.edesign.paymentsdk.version2

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.edesign.paymentsdk.R
import com.edesign.paymentsdk.Utils.Parameters
import java.util.*


class PaymentActivity:AppCompatActivity() {

    private var request=OpenPaymentRequest()
    var langauage="en"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent!=null){

            if (intent.hasExtra(Parameters.OPTIONS)){
                request = (intent.getSerializableExtra(Parameters.OPTIONS) as OpenPaymentRequest?)!!
                langauage=request.getParameters().get(Parameters.LANGUAGE).toString()
            }
        }
        setLocale(langauage)
        setContentView( R.layout.activity_payment)
        PaymentMethodFragment().open(this,request)
    }

    fun setLocale(lang: String?) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}