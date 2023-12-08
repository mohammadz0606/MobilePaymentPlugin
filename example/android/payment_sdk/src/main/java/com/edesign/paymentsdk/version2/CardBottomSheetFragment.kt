package com.edesign.paymentsdk.version2

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edesign.paymentsdk.Utils.Parameters
import com.edesign.paymentsdk.version2.savedCardAPI.*
import com.edesign.paymentsdk.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONArray


class CardBottomSheetFragment : BottomSheetDialogFragment {
    private var rvCard: RecyclerView? = null
    private var imgBack: ImageView? = null
    private var llAnotherCard: LinearLayout? = null
    private var progressBar: ProgressBar? = null
    private var textNoData: TextView? = null
    lateinit var param: AnotherCardInterface
    var request: OpenPaymentRequest? = null
    var arrayList = ArrayList<SavedCardModel>()

    constructor() {}

    companion object {
        fun openFragment(
            param: AnotherCardInterface,
            tokens: OpenPaymentRequest
        ): CardBottomSheetFragment {
            var fragment = CardBottomSheetFragment()
            fragment.param = param
            fragment.request = tokens
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.select_card_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvCard = view.findViewById(R.id.rvCard)
        imgBack = view.findViewById(R.id.imgBack)
        llAnotherCard = view.findViewById(R.id.llAnotherCard)
        progressBar = view.findViewById(R.id.progressBar)
        textNoData = view.findViewById(R.id.textNoData)

        rvCard!!.adapter = CardAdapter(this,arrayList)

        if (request!!.getParameters()["Tokens"]==null || request!!.getParameters()["Tokens"].toString().isNullOrEmpty()){
            textNoData!!.visibility=View.VISIBLE
        }else{
            val savedCardRequest = SavedCardRequest()
            savedCardRequest.setPaymentAuthenticationToken("AuthenticationToken", request!!.getParameters()[Parameters.AUTHENTICATION_TOKEN].toString())
            savedCardRequest.add("MessageID", Parameters.SAVED_CARD_MESSAGE_ID)
            savedCardRequest.add("MerchantID", request!!.getParameters()[Parameters.MERCHANT_ID].toString())
            savedCardRequest.add("Tokens", request!!.getParameters()["Tokens"].toString())
            var savedCardService = SavedCardService(requireActivity())
            progressBar!!.visibility=View.VISIBLE
            savedCardService.process(savedCardRequest, object : SavedCardCallback {
                override fun onResponse(response: SavedCardResponse) {
                    arrayList.clear()
                    progressBar!!.visibility=View.GONE
                    if (response.get(Parameters.SMART_ROUTE_RESPONSE_STATUS) == Parameters.SMART_ROUTE_SUCCESS) {
                        var data=response.response.get("Response.Tokens")
                        val jsonArray = JSONArray(data)
                        for (i in 0 until jsonArray.length()) {
                            var jsonObject = jsonArray.getJSONObject(i)
                            arrayList.add(SavedCardModel(
                                jsonObject.getString("Response.CardNumber"),
                                jsonObject.getString("Response.CardLogo"),
                                jsonObject.getString("Response.CardType"),
                                jsonObject.getString("Response.CardExpiryDate"),
                                jsonObject.getString("Response.Token"),
                                jsonObject.getString("Response.CardHolderName"),
                            ))
                        }

                        rvCard!!.adapter!!.notifyDataSetChanged()

                    }
                    if (arrayList.isNullOrEmpty()){
                        textNoData!!.visibility=View.VISIBLE
                    }

                }
            })
        }




        imgBack!!.setOnClickListener { dismiss() }
        llAnotherCard!!.setOnClickListener {
            param.onClickAnotherCard()
            dismiss()
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

}

public interface AnotherCardInterface {
    fun onClickAnotherCard()
    fun onCardSelected(model:SavedCardModel)
    fun onDeleteCard(token:String,deleted:Boolean)
}