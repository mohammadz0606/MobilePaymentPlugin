package com.edesign.paymentsdk.version2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.edesign.paymentsdk.R
import com.edesign.paymentsdk.Utils.ErrorCodesMessage
import com.edesign.paymentsdk.Utils.Parameters
import com.edesign.paymentsdk.Utils.STSError


class PaymentMethodFragment: Fragment, View.OnClickListener,PaymentTypeAdapter.OnClickPaymentMethod {
    private var recyclerView:RecyclerView?=null
    private var imgBack:ImageView?=null
    private var arrayListType=ArrayList<PaymentTypeModel>()
    private var request=OpenPaymentRequest()
    private lateinit var onClickPaymentMethod:PaymentTypeAdapter.OnClickPaymentMethod
    var isOpenFragment="cards"
    var paymentMethod=ArrayList<String>()


    constructor() {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onClickPaymentMethod=this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.payment_screen_fragment,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arrayListType.clear()
        if (request.getParameters().get(Parameters.PAYMENT_METHOD)!=null) {
            paymentMethod.clear()
            paymentMethod= request.getParameters().get(Parameters.PAYMENT_METHOD) as ArrayList<String>
        }

        for (i in paymentMethod.indices){
            if (paymentMethod[i].equals("cards",true)){
                arrayListType.add(PaymentTypeModel(paymentMethod[i],getString(R.string.cards),R.drawable.card,R.drawable.card,true))
            }
            if (paymentMethod[i].equals("apple pay",true)){
                arrayListType.add(PaymentTypeModel(paymentMethod[i],getString(R.string.apply_pay),R.drawable.ic_apple,R.drawable.ic_apple,false))
            }
            if (paymentMethod[i].equals("paypal",true)){
                arrayListType.add(PaymentTypeModel(paymentMethod[i],getString(R.string.paypal),R.drawable.ic_paypal,R.drawable.ic_paypal,false))
            }
            if (paymentMethod[i].equals("cash",true)){
                arrayListType.add(PaymentTypeModel(paymentMethod[i],getString(R.string.cash),R.drawable.ic_cash,R.drawable.ic_cash,false))
            }
        }
        recyclerView=view.findViewById(R.id.rvPaymentType)
        imgBack=view.findViewById(R.id.imgBack)
        imgBack!!.setOnClickListener(this)
        recyclerView!!.adapter=PaymentTypeAdapter(requireActivity(),arrayListType,onClickPaymentMethod)
        CardPaymentFragment().open(this,request)


    }


     fun open(var1: AppCompatActivity, req: OpenPaymentRequest) {
        var1.supportFragmentManager.beginTransaction().add(R.id.frameLayout, this)
                .commit()

         this.request=req
        }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.imgBack->{
                (requireActivity() as AppCompatActivity).finish()
                var sTSError =STSError()
                sTSError!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kCancelPaymentCode)
                sTSError!!.addResponse(ErrorCodesMessage.kErrorDescription, ErrorCodesMessage.kCancelPaymentMessage)
                Checkout.param.onPaymentFailed(sTSError.response)

            }

        }
    }

    override fun onClickPaymentMethod(type: String) {
        if (type=="cards"){
            if (isOpenFragment!=type){
                CardPaymentFragment().open(this,request)
            }
            isOpenFragment=type
        }else{
            //isOpenFragment=type
            Toast.makeText(requireActivity(),type,Toast.LENGTH_SHORT).show()
        }
    }

}