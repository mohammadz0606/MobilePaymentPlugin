package com.edesign.paymentsdk.version2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.edesign.paymentsdk.R
import com.edesign.paymentsdk.Utils.Utility


class PaymentSuccessFragment: Fragment {

    private var transactionId=""
    var param:OnDismissInterface?=null

       constructor() {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.payment_success,
            container,
            false
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utility(requireActivity()).hideKeyboard(requireActivity())
        view.findViewById<TextView>(R.id.textTransactionId).setText(getString(R.string.transaction_number_n,transactionId))
        view.findViewById<ImageView>(R.id.imgClose).setOnClickListener {
            val returnIntet = Intent()
            returnIntet.putExtra("data", "123456")
            requireActivity().setResult(444, returnIntet)
            requireActivity().finish()
        }

    }


    fun open(var1: AppCompatActivity, transactionId: String, param: OnDismissInterface) {
        this.transactionId=transactionId
        this.param=param
        var1.supportFragmentManager.beginTransaction().add(R.id.frameLayout, this)
                .commit()
        }


    override fun onDestroy() {
        super.onDestroy()
        if (param!=null){
            param!!.dismiss()
        }
    }

    interface OnDismissInterface{
        fun dismiss()
    }

}