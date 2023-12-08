package com.edesign.paymentsdk.version2

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.edesign.paymentsdk.R
import com.edesign.paymentsdk.Utils.CountryCodeUtils
import com.edesign.paymentsdk.Utils.ErrorCodesMessage
import com.edesign.paymentsdk.Utils.Loader
import com.edesign.paymentsdk.Utils.Parameters
import com.edesign.paymentsdk.Utils.STSError
import com.edesign.paymentsdk.version2.authenticatePayer.*
import com.edesign.paymentsdk.version2.authenticatePayer.MobilePaymentService
import com.edesign.paymentsdk.version2.cardTypeAPi.CardTypeCallback
import com.edesign.paymentsdk.version2.cardTypeAPi.CardTypeRequest
import com.edesign.paymentsdk.version2.cardTypeAPi.CardTypeResponse
import com.edesign.paymentsdk.version2.cardTypeAPi.CardTypeService
import com.edesign.paymentsdk.version2.mPayment.MobilePaymentCallback
import com.edesign.paymentsdk.version2.mPayment.MobilePaymentRequest
import com.edesign.paymentsdk.version2.mPayment.MobilePaymentResponse
import com.edesign.paymentsdk.version2.savedCardAPI.SavedCardModel
import com.edesign.paymentsdk.version2.threedsVersionApi.ThreeDSCallback
import com.edesign.paymentsdk.version2.threedsVersionApi.ThreeDSRequest
import com.edesign.paymentsdk.version2.threedsVersionApi.ThreeDSResponse
import com.edesign.paymentsdk.version2.threedsVersionApi.ThreeDSService
import com.edesign.paymentsdk.version2.viewmodel.CardPaymentViewModel
import com.google.gson.Gson
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver
import com.netcetera.threeds.sdk.api.transaction.challenge.events.CompletionEvent
import com.netcetera.threeds.sdk.api.transaction.challenge.events.ProtocolErrorEvent
import com.netcetera.threeds.sdk.api.transaction.challenge.events.RuntimeErrorEvent
import com.skydoves.balloon.*
import com.skydoves.balloon.BalloonSizeSpec.WRAP
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class CardPaymentFragment : Fragment, View.OnClickListener {
    private var edtCardNumber: EditText? = null
    private var edtCode: EditText? = null
    private var edtCardHolderName: EditText? = null
    private var edtExpiryDate: EditText? = null
    private var imgCardInfo: ImageView? = null
    private var imgExpiryInfo: ImageView? = null
    private var imgCodeInfo: ImageView? = null
    private var imgCardHolderInfo: ImageView? = null
    private var textPayNow: TextView? = null
    private var llPayNow: LinearLayout? = null
    private var llSaveCard: LinearLayout? = null
    private var llScanCard: LinearLayout? = null
    private var imgCardVisa: ImageView? = null
    private var rvSupportedType: RecyclerView? = null
    private var textErrorCardNumber: TextView? = null
    private var textErrorDate: TextView? = null
    private var textErrorCode: TextView? = null
    private var textErrorCardHolderName: TextView? = null
    private var llSaveCardButton: LinearLayout? = null
    private var scrollView: NestedScrollView? = null
    private var switchSave: Switch? = null

    private var loader: Loader? = null
    private var request = OpenPaymentRequest()
    private lateinit var cardPaymentViewModel: CardPaymentViewModel

    var cardScanningEnabled = false
    var saveCard = false
    var threeDsEnable = false
    var token = ""
    var cvv = ""
    var type = ""
    var handler: Handler? = null
    var runnable: Runnable? = null
    var isApiRunning = false
    private var threeDSVersion = ""
    var cardTypeService : CardTypeService?=null
    var stsError=STSError()


    constructor() {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loader = Loader(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.card_payment_layout,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardPaymentViewModel = ViewModelProvider(this).get(CardPaymentViewModel::class.java)
        cardTypeService= CardTypeService(requireActivity())
        if (request.getParameters().get(Parameters.THREE_DS_ENABLE) != null) {
            threeDsEnable = request.getParameters().get(Parameters.THREE_DS_ENABLE) as Boolean
        }

        initialize(view)
        handler = Handler(Looper.getMainLooper())

        cardPaymentViewModel.getThreeDsLiveData().observe(viewLifecycleOwner, { it ->
            if (it.getthreeDS2ServiceInstance() != null) {
                cardPaymentViewModel.authenticateRequestParameter(edtCardNumber!!.text.trim().toString(), type,threeDSVersion)
            } else {
                loader!!.stop()
                PaymentFailedFragment().open(
                    requireActivity() as AppCompatActivity,
                    request.getParameters()[Parameters.TRANSACTION_ID].toString(),
                    object :PaymentFailedFragment.OnDismissInterface{
                        override fun dismiss() {
                            stsError!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kThreeDSInitializationErrorCode)
                            stsError!!.addResponse(ErrorCodesMessage.kErrorDescription, ErrorCodesMessage.kThreeDSInitializationErrorMessage)
                            Checkout.param.onPaymentFailed(stsError.response)
                        }

                    }
                )
            }

        })
        cardPaymentViewModel.getAuthenticateRequestParamLiveData()
            .observe(viewLifecycleOwner, { it ->
                if (it.getAuthenticationRequestParameters() != null) {
                    submitTransaction()
                } else {
                    loader!!.stop()
                    PaymentFailedFragment().open(
                        requireActivity() as AppCompatActivity,
                        request.getParameters()[Parameters.TRANSACTION_ID].toString(),
                        object :PaymentFailedFragment.OnDismissInterface{
                            override fun dismiss() {
                                stsError!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kAuthenticatePayerErrorCode)
                                stsError!!.addResponse(ErrorCodesMessage.kErrorDescription, ErrorCodesMessage.kAuthenticatePayerErrorMessage)
                                Checkout.param.onPaymentFailed(stsError.response)
                            }

                        }
                    )

                }

            })

        edtCardNumber!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length >= 9) {
                    callCardTypeApi(s!!.toString(),isApiRunning)
                } else if (s!!.length < 9) {
                    if (cardTypeService!=null){
                        cardTypeService!!.cancelCall()
                    }
                    imgCardVisa!!.visibility = View.GONE
                    rvSupportedType!!.visibility = View.VISIBLE
//                    imgCardVisa!!.visibility = View.VISIBLE
//                    imgCardMaster!!.visibility = View.VISIBLE
                }
            }
        })

        edtCardHolderName!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }

            override fun afterTextChanged(s: Editable?) {
               if (!s.isNullOrEmpty()){
                   textErrorCardHolderName!!.visibility = View.GONE
               }
            }
        })

        edtCode!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length > 2) {
                    textErrorCode!!.visibility = View.GONE
                }
            }
        })

        edtExpiryDate!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length >= 5) {
                    textErrorDate!!.visibility = View.GONE
                }
            }
        })

        edtCardNumber!!.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                //You can identify which key pressed by checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    if (edtCardNumber!!.text.toString().contains("*")) {
                        edtCardNumber!!.setText("")
                        token = ""
                        cvv = ""
                        type = ""
                    }
                }
                return false
            }
        })


    }

    // Returns true if given card number is valid
    private fun checkLuhn(cardNo: String): Boolean {
        val nDigits = cardNo.length
        var nSum = 0
        var isSecond = false
        for (i in nDigits - 1 downTo 0) {
            var d = cardNo[i].code - '0'.code
            if (isSecond) d *= 2

            // We add two digits to handle
            // cases that make two digits
            // after doubling
            nSum += d / 10
            nSum += d % 10
            isSecond = !isSecond
        }
        return nSum % 10 == 0
    }

    private fun callCardTypeApi(number: String,isRunning:Boolean) {
        if (number.replace(" ", "").length >= 8 && token.isNullOrEmpty() && !isRunning) {
            isApiRunning = true
            val paymentRequest = CardTypeRequest()
            paymentRequest.setPaymentAuthenticationToken("AuthenticationToken", request.getParameters()[Parameters.AUTHENTICATION_TOKEN].toString())
            paymentRequest.add("MessageID", Parameters.CARD_TYPE_MESSAGE_ID)
            paymentRequest.add("BIN", number.replace(" ", "").substring(0, 8))
            paymentRequest.add("MerchantID", request.getParameters()[Parameters.MERCHANT_ID].toString())
            cardTypeService!!.process(paymentRequest, object : CardTypeCallback {
                override fun onResponse(stsResponse: CardTypeResponse) {
                    if (stsResponse.get(Parameters.SMART_ROUTE_RESPONSE_STATUS) == Parameters.SMART_ROUTE_SUCCESS
                    ) {
                        textErrorCardNumber!!.visibility = View.GONE
                        rvSupportedType!!.visibility = View.GONE
                        if (stsResponse.get("Response.CardLogo")!=null){
                            try {
                                val bytes: ByteArray = Base64.decode(stsResponse.get("Response.CardLogo").toString().replace(" ", "+"), Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                imgCardVisa!!.setImageBitmap(bitmap)
                                imgCardVisa!!.visibility = View.VISIBLE
                                rvSupportedType!!.visibility = View.GONE
                            } catch (e: Exception) {
                                e.printStackTrace()
                                imgCardVisa!!.visibility = View.GONE
                            }
                        }else{
                            imgCardVisa!!.visibility = View.GONE
                        }

                    } else {
                        if (stsResponse.get(Parameters.SMART_ROUTE_RESPONSE_STATUS)!=null && (stsResponse.get(Parameters.SMART_ROUTE_RESPONSE_STATUS) == "00141" || stsResponse.get(Parameters.SMART_ROUTE_RESPONSE_STATUS) == "00015")){

                            PaymentFailedFragment().open(
                                requireActivity() as AppCompatActivity,
                                request.getParameters()[Parameters.TRANSACTION_ID].toString(),
                                object :PaymentFailedFragment.OnDismissInterface{
                                    override fun dismiss() {
                                        Checkout.param.onPaymentFailed(stsResponse.response)
                                    }

                                }
                            )
                        }
                        if (stsResponse.get(Parameters.RESPONSE_STATUS_DESCRIPTION)!=null){
                            imgCardVisa!!.visibility = View.GONE
                            rvSupportedType!!.visibility = View.GONE
                            textErrorCardNumber!!.visibility = View.VISIBLE
                            textErrorCardNumber!!.setText(stsResponse.get(Parameters.RESPONSE_STATUS_DESCRIPTION))
                        }
                    }
                    isApiRunning = false
                }

            })

        }

    }

    private fun initialize(view: View) {
        imgCardInfo = view.findViewById(R.id.imgCardInfo)
        imgExpiryInfo = view.findViewById(R.id.imgExpiryInfo)
        imgCodeInfo = view.findViewById(R.id.imgCodeInfo)
        imgCardHolderInfo = view.findViewById(R.id.imgCardHolderInfo)
        textPayNow = view.findViewById(R.id.textPayNow)
        llPayNow = view.findViewById(R.id.llPayNow)
        llSaveCard = view.findViewById(R.id.llSaveCard)
        llScanCard = view.findViewById(R.id.llScanCard)
        imgCardVisa = view.findViewById(R.id.imgCardVisa)
        rvSupportedType = view.findViewById(R.id.rvSupportedType)
        edtCardNumber = view.findViewById(R.id.edtCardNumber)
        textErrorCardNumber = view.findViewById(R.id.textErrorCardNumber)
        textErrorDate = view.findViewById(R.id.textErrorDate)
        textErrorCode = view.findViewById(R.id.textErrorCode)
        textErrorCardHolderName = view.findViewById(R.id.textErrorCardHolderName)
        edtCode = view.findViewById(R.id.edtCode)
        edtCardHolderName = view.findViewById(R.id.edtCardHolderName)
        llSaveCardButton = view.findViewById(R.id.llSaveCardButton)
        scrollView = view.findViewById(R.id.scrollView)
        switchSave = view.findViewById(R.id.switchSave)
        edtExpiryDate = view.findViewById(R.id.edtExpiryDate)

        imgCardInfo!!.setOnClickListener(this)
        imgExpiryInfo!!.setOnClickListener(this)
        imgCodeInfo!!.setOnClickListener(this)
        imgCardHolderInfo!!.setOnClickListener(this)
        llPayNow!!.setOnClickListener(this)
        llSaveCardButton!!.setOnClickListener(this)
        llScanCard!!.setOnClickListener(this)

//        edtCardNumber!!.setText("4012-0000-0000-3119")
//        edtCardNumber!!.setText("4111-1111-1111-1111")
//        edtCardNumber!!.setText("5123-4500-0000-0008")
//        edtCardNumber!!.setText("4440-0000-0990-0010")
//        edtExpiryDate!!.setText("01/31")
//        edtCode!!.setText("100")
//        edtCardHolderName!!.setText("Jay Bhatt")

        if (request != null) {
            if (request.getParameters().get(Parameters.CARD_SCANNING_ENABLED) != null) {
                cardScanningEnabled = request.getParameters().get(Parameters.CARD_SCANNING_ENABLED) as Boolean
            }
            if (request.getParameters().get(Parameters.SAVE_CARD) != null) {
                saveCard = request.getParameters().get(Parameters.SAVE_CARD) as Boolean
            }
            if (request.getParameters().get(Parameters.CARD_SCANNING_ENABLED) != null && request.getParameters().get(Parameters.CARD_SCANNING_ENABLED) as Boolean == true) {
                llScanCard!!.visibility = View.VISIBLE
            } else {
                llScanCard!!.visibility = View.GONE
            }
            if (request.getParameters().get(Parameters.SAVE_CARD) != null && request.getParameters().get(Parameters.SAVE_CARD) as Boolean == true) {
                llSaveCard!!.visibility = View.VISIBLE
                if (request!!.getParameters()["Tokens"]==null || request!!.getParameters()["Tokens"].toString().isNullOrEmpty() ) {
                    llSaveCardButton!!.visibility = View.GONE

                } else {
                    openSavedCard()
                    llSaveCardButton!!.visibility = View.VISIBLE
                }
            } else {
                llSaveCard!!.visibility = View.GONE
            }
            if (request.getParameters()
                    .get(Parameters.AMOUNT) != null
            ) {
                var number = request.getParameters().get(Parameters.AMOUNT).toString()
//                var amount = String.format(Locale.US, "%." + CountryCodeUtils.getCountryDecimalFormat(requireActivity(), request.getParameters()[Parameters.CURRENCY].toString()) + "f", a.toDouble())
                val numValue = number.toLong()
                val power=CountryCodeUtils.getCountryDecimalFormat(requireActivity(), request.getParameters()[Parameters.CURRENCY].toString())
                var formattedNumber=(numValue / Math.pow(10.00,power.toDouble()))

                var amount = String.format(Locale.US, "%." +power+ "f", formattedNumber.toDouble())

                textPayNow!!.setText(getString(R.string.pay_now,  amount+" "+CountryCodeUtils.getCountryCurrencyCode(
                    requireActivity(),
                    request.getParameters()[Parameters.CURRENCY].toString()
                ) )

                )
            }

            if (request.getParameters().get(Parameters.CARD_TYPE) != null) {
                var cardType : ArrayList<String> = request.getParameters().get(Parameters.CARD_TYPE) as ArrayList<String>
                rvSupportedType!!.adapter=SupportedCardTypeAdapter(cardType)

            }
        }

    }


    fun open(var1: PaymentMethodFragment, req: OpenPaymentRequest) {
        var1.childFragmentManager.beginTransaction().replace(R.id.child_fragment_container, this)
            .commit()
        this.request = req
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgCardInfo -> {
                showTooltip(getString(R.string.card_number_tooltip), imgCardInfo!!)
            }
            R.id.imgExpiryInfo -> {
                showTooltip(getString(R.string.card_expiry_tooltip), imgExpiryInfo!!)
            }
            R.id.imgCodeInfo -> {
                showTooltip(getString(R.string.security_code_tooltip), imgCodeInfo!!)
            }
            R.id.imgCardHolderInfo -> {
                showTooltip(getString(R.string.card_holder_tooltip), imgCardHolderInfo!!)
            }
            R.id.llPayNow -> {
                token = ""
                cvv = ""
                type = ""
                if (validateForm()) {
                    loader!!.start()
                    if (threeDsEnable) {
                        callThreeDSVersionApi(token)
                    } else {
                        submitTransaction()
                    }
                }


            }

            R.id.llScanCard -> {
                openCamera()

            }
            R.id.llSaveCardButton -> {
                openSavedCard()

            }

        }
    }

    private fun openSavedCard() {
        CardBottomSheetFragment.openFragment(object : AnotherCardInterface {
            override fun onCardSelected(model: SavedCardModel) {
                type = model.cardType
                token = model.token
                cvv = model.cvv

                if (threeDsEnable) {
                    callThreeDSVersionApi(token)
                } else {
                    submitTransaction()
                }

            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDeleteCard(token: String, deleted: Boolean) {
                //delete card code
                var a= request!!.getParameters()["Tokens"].toString()
                var array=a.split(",")
                val arrayList: ArrayList<String> = ArrayList(array)
                if (!arrayList.isNullOrEmpty()){
                    for (i in arrayList.indices){
                        if (arrayList[i]==token){
                            arrayList.removeAt(i)
                            break
                        }
                    }
                }
                TextUtils.join(",",arrayList)
                request!!.replaceValue("Tokens",TextUtils.join(",",arrayList).toString())
                Checkout.param.onDeleteCardResponse(token, deleted)
            }

            override fun onClickAnotherCard() {
                edtCardNumber!!.setText("")
                edtExpiryDate!!.setText("")
                edtCode!!.setText("")
                edtCardHolderName!!.setText("")
                textErrorCardNumber!!.visibility = View.GONE
                textErrorDate!!.visibility = View.GONE
                textErrorCode!!.visibility = View.GONE
                textErrorCardHolderName!!.visibility = View.GONE

        }
    }, request).show(requireActivity().supportFragmentManager, "")

}

private fun submitTransaction() {
    if (threeDsEnable) {
        loader!!.start()
        callAuthenticatePayer(token)
    } else {
        callPaymentService(token)
    }

}

private fun callPaymentService(token: String) {
    val paymentRequest = MobilePaymentRequest()
    paymentRequest.setPaymentType(request.paymentType)
    paymentRequest.setPaymentAuthenticationToken("AuthenticationToken", request.getParameters()[Parameters.AUTHENTICATION_TOKEN].toString())
    paymentRequest.add("MessageID", if (request.paymentType ==  PaymentType.SALES.name) {
        Parameters.API_M_PAYMENT_MESSAGE_ID
        } else {
            Parameters.API_M_PRE_AUTH_MESSAGE_ID
        }
    )
    paymentRequest.add("TransactionID",request.getParameters()[Parameters.TRANSACTION_ID].toString())
    paymentRequest.add("MerchantID", request.getParameters()[Parameters.MERCHANT_ID].toString())
    paymentRequest.add("Amount",(request.getParameters().get(Parameters.AMOUNT).toString().toInt()).toString())
    paymentRequest.add("CurrencyISOCode", request.getParameters()[Parameters.CURRENCY].toString())
    paymentRequest.add("PaymentMethod", "1")
    paymentRequest.add("ClientIPaddress", request.getParameters()[Parameters.CLIENT_IP_ADDRESS].toString())
    if (!token.isNullOrEmpty()) {
        paymentRequest.add("Token", token)
    } else {
        paymentRequest.add("CardNumber", edtCardNumber!!.text.toString().replace(" ", ""))
        paymentRequest.add("ExpiryDateYear", edtExpiryDate!!.text.toString().split("/")[1].toString())
        paymentRequest.add("ExpiryDateMonth", edtExpiryDate!!.text.toString().split("/")[0].toString())
        paymentRequest.add("SecurityCode", edtCode!!.text.toString().trim())
        paymentRequest.add("CardHolderName", edtCardHolderName!!.text.toString().trim())
        paymentRequest.add("GenerateToken", if (request.getParameters().get(Parameters.TOKENIZE_CARD) == true) "Yes" else "No")
    }
    if (request.getParameters()[Parameters.AGREEMENT_ID]!=null && !request.getParameters()[Parameters.AGREEMENT_TYPE].toString().isNullOrEmpty() &&
        request.getParameters()[Parameters.AGREEMENT_TYPE]!=null && !request.getParameters()[Parameters.AGREEMENT_TYPE].toString().isNullOrEmpty() ){
        paymentRequest.add("AgreementID", request.getParameters()[Parameters.AGREEMENT_ID].toString())
        paymentRequest.add("AgreementType", request.getParameters()[Parameters.AGREEMENT_TYPE].toString())

    }
    paymentRequest.add("PaymentDescription", request.getParameters()[Parameters.PAYMENT_DESCRIPTION].toString())
    if (request.getOptionalParam()!=null){
        for (i in 0 until request.getOptionalParam().getParameters().size){
            paymentRequest.add(request.getOptionalParam().getParameters().keys.elementAt(i).toString(),request.getOptionalParam().getParameters().values.elementAt(i).toString())
        }
    }
    paymentRequest.add("Channel", "1")
    paymentRequest.add("Language", request.getParameters().get(Parameters.LANGUAGE).toString())

    var paymentService = MobilePaymentService(requireActivity())
    loader!!.start()
    paymentService.process(paymentRequest, object : MobilePaymentCallback {
        override fun onResponse(stsResponse: MobilePaymentResponse) {
            loader!!.stop()
            if (stsResponse.get(Parameters.SMART_ROUTE_RESPONSE_STATUS) == Parameters.SMART_ROUTE_SUCCESS
                && stsResponse.get(Parameters.RESPONSE_HASH_MATCH) == Parameters.MATCHED_VALUE
            ) {
                PaymentSuccessFragment().open(
                    requireActivity() as AppCompatActivity,
                    stsResponse.get(Parameters.RESPONSE_TRANSACTION_ID).toString(),
                    object : PaymentSuccessFragment.OnDismissInterface{
                        override fun dismiss() {
                            stsResponse.addResponse(Parameters.SAVE_CARD,switchSave!!.isChecked.toString())
                            Checkout.param.onResponse(stsResponse.response)
                        }

                    }
                )


            } else  {
                PaymentFailedFragment().open(
                    requireActivity() as AppCompatActivity,
                    request.getParameters()[Parameters.TRANSACTION_ID].toString(),
                    object :PaymentFailedFragment.OnDismissInterface{
                        override fun dismiss() {
                            Checkout.param.onPaymentFailed(stsResponse.response)
                        }

                    }
                )

            }
        }

    })
}

private fun showError(error: String) {

    //scrollView!!.scrollTo(0, 0)
   /* if (handler != null && runnable != null) {
        handler!!.postDelayed(runnable!!, 5000)
    }*/
}

private fun callAuthenticatePayer(token: String) {

    val authenticatePayerRequest = AuthenticatePayerRequest()
    authenticatePayerRequest.setPaymentAuthenticationToken("AuthenticationToken", request.getParameters()[Parameters.AUTHENTICATION_TOKEN].toString())
    authenticatePayerRequest.add("MessageID", Parameters.AUTEHNTICATE_PAYER_MESSAGE_ID)
    authenticatePayerRequest.add("TransactionID", System.currentTimeMillis().toString() + randomNumber())
    authenticatePayerRequest.add("CorrelationId", request.getParameters().get(Parameters.TRANSACTION_ID).toString())
    authenticatePayerRequest.add("MerchantID", request.getParameters().get(Parameters.MERCHANT_ID).toString())
    authenticatePayerRequest.add("Amount", (request.getParameters().get(Parameters.AMOUNT).toString()).toInt().toString())
    authenticatePayerRequest.add("CurrencyISOCode", request.getParameters().get(Parameters.CURRENCY).toString())
    authenticatePayerRequest.add("PaymentMethod", "1")
    authenticatePayerRequest.add("ClientIPaddress", request.getParameters().get(Parameters.CLIENT_IP_ADDRESS).toString())
    if (!token.isNullOrEmpty()) {
        authenticatePayerRequest.add("Token", token)
        authenticatePayerRequest.add("SecurityCode", cvv)
    } else {
        authenticatePayerRequest.add("CardNumber", edtCardNumber!!.text.toString().replace(" ", ""))
        authenticatePayerRequest.add("ExpiryDateYear",edtExpiryDate!!.text.toString().split("/")[1].toString())
        authenticatePayerRequest.add("ExpiryDateMonth", edtExpiryDate!!.text.toString().split("/")[0].toString())
        authenticatePayerRequest.add("SecurityCode", edtCode!!.text.toString().trim())
        authenticatePayerRequest.add("CardHolderName", edtCardHolderName!!.text.toString().trim())
    }

    if (request.getParameters()[Parameters.AGREEMENT_ID]!=null && !request.getParameters()[Parameters.AGREEMENT_TYPE].toString().isNullOrEmpty() &&
        request.getParameters()[Parameters.AGREEMENT_TYPE]!=null && !request.getParameters()[Parameters.AGREEMENT_TYPE].toString().isNullOrEmpty() ){
        authenticatePayerRequest.add("AgreementID", request.getParameters()[Parameters.AGREEMENT_ID].toString())
        authenticatePayerRequest.add("AgreementType", request.getParameters()[Parameters.AGREEMENT_TYPE].toString())

    }

    authenticatePayerRequest.add("PaymentDescription", request.getParameters().get(Parameters.PAYMENT_DESCRIPTION).toString())
    authenticatePayerRequest.add("Channel", "3")
    authenticatePayerRequest.add("Language", request.getParameters().get(Parameters.LANGUAGE).toString())
    authenticatePayerRequest.add("AppID", cardPaymentViewModel.authenticationRequestParametersLiveData!!.value!!.getAuthenticationRequestParameters()!!.sdkAppID)
    authenticatePayerRequest.add("SDKEncryptedData", cardPaymentViewModel.authenticationRequestParametersLiveData!!.value!!.getAuthenticationRequestParameters()!!.deviceData)
    authenticatePayerRequest.add("EphemeralPublicKey", cardPaymentViewModel.authenticationRequestParametersLiveData!!.value!!.getAuthenticationRequestParameters()!!.sdkEphemeralPublicKey)
    authenticatePayerRequest.add("SDKReferenceNumber", cardPaymentViewModel.authenticationRequestParametersLiveData!!.value!!.getAuthenticationRequestParameters()!!.sdkReferenceNumber)
    authenticatePayerRequest.add("SDKTransactionID", cardPaymentViewModel.authenticationRequestParametersLiveData!!.value!!.getAuthenticationRequestParameters()!!.sdkTransactionID)
    authenticatePayerRequest.add("SDKInterface", "NATIVE")
    if (request.getOptionalParam()!=null){
        for (i in 0 until request.getOptionalParam().getParameters().size){
            authenticatePayerRequest.add(request.getOptionalParam().getParameters().keys.elementAt(i).toString(),request.getOptionalParam().getParameters().values.elementAt(i).toString())
        }
    }

    var authenticatePayerService = AuthenticatePayerService(requireActivity())
    authenticatePayerService.process(authenticatePayerRequest, object : MyCallback {
        override fun onResponse(authenticatePayerResponse: AuthenticatePayerResponse) {
            loader!!.stop()

            if (authenticatePayerResponse.get(Parameters.SMART_ROUTE_RESPONSE_STATUS) == "20001") {

                // do challenge flow
                val challengeParameter = ChallengeParameters().apply {
                    set3DSServerTransactionID(authenticatePayerResponse["Response.ThreeDServerTrxnID"])
                    acsRefNumber = authenticatePayerResponse["Response.ACSRefNumber"]
                    acsSignedContent = authenticatePayerResponse["Response.ChallengeContent"]
                    acsTransactionID = authenticatePayerResponse["Response.ACSTrxnID"]
//                    threeDSRequestorAppURL = authenticatePayerResponse["Response.ChallengeCompletionUrl"]
                    threeDSRequestorAppURL = "https://stspayone.com"
                }

                onChallenge(challengeParameter, authenticatePayerResponse, token)


            } else if (authenticatePayerResponse.get(Parameters.SMART_ROUTE_RESPONSE_STATUS) == "00000") {

                // Friction less

                callMobilePaymentApi(
                    authenticatePayerResponse,
                    null, token
                )

            } else {
                cardPaymentViewModel.mutableLiveData.value!!.getthreeDS2ServiceInstance()!!
                    .cleanup(App.appContext!!)

                PaymentFailedFragment().open(
                    requireActivity() as AppCompatActivity,
                    request.getParameters()[Parameters.TRANSACTION_ID].toString(),
                    object :PaymentFailedFragment.OnDismissInterface{
                        override fun dismiss() {
                            Checkout.param.onPaymentFailed(authenticatePayerResponse.response)
                        }

                    }
                )


            }

        }


    })

}

private fun callMobilePaymentApi(
    authenticatePayerResponse: AuthenticatePayerResponse,
    transactionStatus: String?,
    token: String
) {

    val paymentRequest = MobilePaymentRequest()
    paymentRequest.setPaymentType(request.paymentType)
    paymentRequest.setPaymentAuthenticationToken("AuthenticationToken", request.getParameters()[Parameters.AUTHENTICATION_TOKEN].toString())
    paymentRequest.add(
        "MessageID", if (request.paymentType ==  PaymentType.SALES.name) {
            Parameters.MOBILE_PAYMENT_MESSAGE_ID
        } else {
            Parameters.MOBILE_PRE_AUTH_MESSAGE_ID
        }
    )
    paymentRequest.add("TransactionID", request.getParameters()[Parameters.TRANSACTION_ID].toString())
    paymentRequest.add("MerchantID", request.getParameters()[Parameters.MERCHANT_ID].toString())
    paymentRequest.add("Amount", (request.getParameters().get(Parameters.AMOUNT).toString()).toInt().toString())
    paymentRequest.add("CurrencyISOCode", request.getParameters()[Parameters.CURRENCY].toString())
    paymentRequest.add("PaymentMethod", "1")
    paymentRequest.add("ClientIPaddress", request.getParameters()[Parameters.CLIENT_IP_ADDRESS].toString())
    if (!token.isNullOrEmpty()) {
        paymentRequest.add("Token", token)
        paymentRequest.add("SecurityCode", cvv)
    } else {
        paymentRequest.add("CardNumber", edtCardNumber!!.text.toString().replace(" ", ""))
        paymentRequest.add("ExpiryDateYear", edtExpiryDate!!.text.toString().split("/")[1].toString())
        paymentRequest.add("ExpiryDateMonth", edtExpiryDate!!.text.toString().split("/")[0].toString())
        paymentRequest.add("SecurityCode", edtCode!!.text.toString().trim())
        paymentRequest.add("CardHolderName", edtCardHolderName!!.text.toString().trim())
        paymentRequest.add("GenerateToken", if (request.getParameters().get(Parameters.TOKENIZE_CARD) == true) "Yes" else "No")
    }
    if (request.getParameters()[Parameters.AGREEMENT_ID]!=null && !request.getParameters()[Parameters.AGREEMENT_TYPE].toString().isNullOrEmpty() &&
        request.getParameters()[Parameters.AGREEMENT_TYPE]!=null && !request.getParameters()[Parameters.AGREEMENT_TYPE].toString().isNullOrEmpty() ){
        paymentRequest.add("AgreementID", request.getParameters()[Parameters.AGREEMENT_ID].toString())
        paymentRequest.add("AgreementType", request.getParameters()[Parameters.AGREEMENT_TYPE].toString())

    }


    paymentRequest.add("PaymentDescription", request.getParameters()[Parameters.PAYMENT_DESCRIPTION].toString())
    paymentRequest.add("Channel", "3")
    if (!transactionStatus.isNullOrEmpty()) {
        paymentRequest.add("ChallengeResult", transactionStatus)
    }
//        paymentRequest.add("FrameworkInfo", "Android 7.0")
        paymentRequest.add("Language", request.getParameters().get(Parameters.LANGUAGE).toString())
    paymentRequest.add("AuthPayerTransactionID", authenticatePayerResponse.get("Response.TransactionID").toString())
    if (request.getOptionalParam()!=null){
        for (i in 0 until request.getOptionalParam().getParameters().size){
            paymentRequest.add(request.getOptionalParam().getParameters().keys.elementAt(i).toString(),request.getOptionalParam().getParameters().values.elementAt(i).toString())
        }
    }

    var paymentService = MobilePaymentService(requireActivity())
    loader!!.start()
    paymentService.process(paymentRequest, object : MobilePaymentCallback {
        override fun onResponse(stsResponse: MobilePaymentResponse) {
            loader!!.stop()
            cardPaymentViewModel.mutableLiveData.value!!.getthreeDS2ServiceInstance()!!
                .cleanup(App.appContext!!)
            if (stsResponse.get(Parameters.SMART_ROUTE_RESPONSE_STATUS) == Parameters.SMART_ROUTE_SUCCESS
                && stsResponse.get(Parameters.RESPONSE_HASH_MATCH) == Parameters.MATCHED_VALUE
            ) {
                PaymentSuccessFragment().open(
                    requireActivity() as AppCompatActivity,
                    stsResponse.get(Parameters.RESPONSE_TRANSACTION_ID).toString(),
                    object : PaymentSuccessFragment.OnDismissInterface {
                        override fun dismiss() {
                            stsResponse.addResponse(Parameters.SAVE_CARD,switchSave!!.isChecked.toString())
                            Checkout.param.onResponse(stsResponse.response)
                        }

                    }
                )


            } else  {
                PaymentFailedFragment().open(
                    requireActivity() as AppCompatActivity,
                    request.getParameters()[Parameters.TRANSACTION_ID].toString(),
                    object :PaymentFailedFragment.OnDismissInterface{
                        override fun dismiss() {
                            Checkout.param.onPaymentFailed(stsResponse.response)
                        }

                    }
                )


            }
        }

    })

}

private fun onChallenge(
    challengeParamater: ChallengeParameters,
    authenticatePayerResponse: AuthenticatePayerResponse,
    token: String
) {
    cardPaymentViewModel.getTransactionLiveData().value!!.doChallenge(
        requireActivity(),
        challengeParamater,
        object : ChallengeStatusReceiver {
            override fun completed(completionEvent: CompletionEvent) {

                callMobilePaymentApi(
                    authenticatePayerResponse,
                    completionEvent.transactionStatus,
                    token
                )

            }

            override fun cancelled() {
                cardPaymentViewModel.mutableLiveData.value!!.getthreeDS2ServiceInstance()!!
                    .cleanup(App.appContext!!)
                PaymentFailedFragment().open(
                    requireActivity() as AppCompatActivity,
                    request.getParameters()[Parameters.TRANSACTION_ID].toString(),
                    object :PaymentFailedFragment.OnDismissInterface{
                        override fun dismiss() {
                            stsError!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kCancelPaymentCode)
                            stsError!!.addResponse(ErrorCodesMessage.kErrorDescription, ErrorCodesMessage.kCancelPaymentMessage)
                            Checkout.param.onPaymentFailed(stsError.response)
                        }

                    }
                )


            }

            override fun timedout() {
                cardPaymentViewModel.mutableLiveData.value!!.getthreeDS2ServiceInstance()!!
                    .cleanup(App.appContext!!)
                PaymentFailedFragment().open(
                    requireActivity() as AppCompatActivity,
                    request.getParameters()[Parameters.TRANSACTION_ID].toString(),
                    object :PaymentFailedFragment.OnDismissInterface{
                        override fun dismiss() {
                            stsError!!.addResponse(ErrorCodesMessage.kErrorCode, ErrorCodesMessage.kThreeDS2TimeOut)
                            stsError!!.addResponse(ErrorCodesMessage.kErrorDescription, ErrorCodesMessage.kThreeDS2TimeOutMessage)
                            Checkout.param.onPaymentFailed(stsError.response)
                        }

                    }
                )

            }

            override fun protocolError(protocolErrorEvent: ProtocolErrorEvent) {
                cardPaymentViewModel.mutableLiveData.value!!.getthreeDS2ServiceInstance()!!
                    .cleanup(App.appContext!!)
                showError(Gson().toJson(protocolErrorEvent))
                PaymentFailedFragment().open(
                    requireActivity() as AppCompatActivity,
                    request.getParameters()[Parameters.TRANSACTION_ID].toString(),
                    object :PaymentFailedFragment.OnDismissInterface{
                        override fun dismiss() {
                            stsError!!.addResponse(ErrorCodesMessage.kErrorCode, protocolErrorEvent.errorMessage.errorCode)
                            stsError!!.addResponse(ErrorCodesMessage.kErrorDescription, protocolErrorEvent.errorMessage.errorDescription)
                            Checkout.param.onPaymentFailed(stsError.response)
                        }

                    }
                )


            }

            override fun runtimeError(runtimeErrorEvent: RuntimeErrorEvent) {
                cardPaymentViewModel.mutableLiveData.value!!.getthreeDS2ServiceInstance()!!
                    .cleanup(App.appContext!!)
                showError(Gson().toJson(runtimeErrorEvent))
                PaymentFailedFragment().open(
                    requireActivity() as AppCompatActivity,
                    request.getParameters()[Parameters.TRANSACTION_ID].toString(),
                    object :PaymentFailedFragment.OnDismissInterface{
                        override fun dismiss() {
                            stsError!!.addResponse(ErrorCodesMessage.kErrorCode, runtimeErrorEvent.errorCode)
                            stsError!!.addResponse(ErrorCodesMessage.kErrorDescription, runtimeErrorEvent.errorMessage)
                            Checkout.param.onPaymentFailed(stsError.response)
                        }

                    }
                )



            }
        },
        15
    )

}

    private fun callThreeDSVersionApi(token: String){
        loader!!.start()
        val threeDSRequest = ThreeDSRequest()
        threeDSRequest.setPaymentAuthenticationToken("AuthenticationToken", request!!.getParameters()[Parameters.AUTHENTICATION_TOKEN].toString())
        threeDSRequest.add("MessageID", Parameters.THREE_DS_VERSION_MESSAGE_ID)
        threeDSRequest.add("MerchantID", request!!.getParameters()[Parameters.MERCHANT_ID].toString())
        threeDSRequest.add("CurrencyISOCode", request.getParameters()[Parameters.CURRENCY].toString())
        threeDSRequest.add("Version","1.0")
        threeDSRequest.add("Channel","3")
        threeDSRequest.add("TransactionID",request.getParameters()[Parameters.TRANSACTION_ID].toString())

        if (!token.isNullOrEmpty()) {
            threeDSRequest.add("Token", token)
        } else {
            threeDSRequest.add("CardNumber", edtCardNumber!!.text.toString().replace(" ", ""))
            threeDSRequest.add("ExpiryDateYear", edtExpiryDate!!.text.toString().split("/")[1].toString())
            threeDSRequest.add("ExpiryDateMonth", edtExpiryDate!!.text.toString().split("/")[0].toString())
        }
            var threeDSService = ThreeDSService(requireActivity())
            threeDSService.process(threeDSRequest,object :ThreeDSCallback{
                override fun onResponse(response: ThreeDSResponse) {
                    if (response.get(Parameters.SMART_ROUTE_RESPONSE_STATUS) == Parameters.SMART_ROUTE_SUCCESS) {
                        threeDSVersion=response.get("Response.ThreeDSVersion").toString()
                        Handler(Looper.getMainLooper()).postDelayed({ cardPaymentViewModel.callInitialize3ds() },300)

                    }else{
                        loader!!.stop()
                        PaymentFailedFragment().open(
                            requireActivity() as AppCompatActivity,
                            request.getParameters()[Parameters.TRANSACTION_ID].toString(),
                            object :PaymentFailedFragment.OnDismissInterface{
                                override fun dismiss() {
                                    Checkout.param.onPaymentFailed(response.response)
                                }

                            }
                        )

                    }

                }

            })

    }

private fun validateForm(): Boolean {
    var isValid = true
    if (edtCardNumber!!.text.trim().isNullOrEmpty()) {
        textErrorCardNumber!!.visibility = View.VISIBLE
        textErrorCardNumber!!.setText(R.string.enter_card_number)
        isValid = false
    } else if (edtCardNumber!!.text.trim().length < 19) {
        textErrorCardNumber!!.visibility = View.VISIBLE
        textErrorCardNumber!!.setText(R.string.this_card_number_is_invalid)
        isValid = false
    } else if (!checkLuhn(edtCardNumber!!.text.toString().replace(" ",""))){
        textErrorCardNumber!!.visibility = View.VISIBLE
        textErrorCardNumber!!.setText(R.string.this_card_number_is_invalid)
        isValid = false
    } else {
        textErrorCardNumber!!.visibility = View.GONE
    }
    if (edtExpiryDate!!.text.toString().trim().isNullOrEmpty() || edtExpiryDate!!.text.toString().trim().equals("MM/YY")){
        textErrorDate!!.visibility = View.VISIBLE
        textErrorDate!!.setText(R.string.enter_card_expiry_date)
        isValid = false
    } else if (edtExpiryDate!!.text.trim().length < 5 || edtExpiryDate!!.text.toString().trim().contains("M") || edtExpiryDate!!.text.toString().trim().contains("Y")) {
        textErrorDate!!.visibility = View.VISIBLE
        textErrorDate!!.setText(R.string.card_expiry_date_is_invalid)
        isValid = false
    } else {
        textErrorDate!!.setText(R.string.card_expiry_date_is_invalid)
        val dateFormat: DateFormat = SimpleDateFormat("MM")
        val dateFormat1: DateFormat = SimpleDateFormat("yy")
        val date = Date()
        var currentMonth=dateFormat.format(date).toInt()
        var currentYear=dateFormat1.format(date).toInt()
        var a=edtExpiryDate!!.text.toString().split("/")
        if (a[1].toInt()<currentYear){
            textErrorDate!!.visibility = View.VISIBLE
            isValid = false
        }else if (a[0].toInt()>12) {
            textErrorDate!!.visibility = View.VISIBLE
            isValid = false
        }else if (a[0].toInt()<currentMonth && a[1].toInt()<=currentYear){
            textErrorDate!!.visibility = View.VISIBLE
            isValid = false
        }else{
            textErrorDate!!.visibility = View.GONE
        }
    }
    if (edtCode!!.text.trim().isNullOrEmpty()) {
        textErrorCode!!.visibility = View.VISIBLE
        textErrorCode!!.setText(R.string.enter_a_security_code)
        isValid = false
    } else if (edtCode!!.text.trim().length < 3) {
        textErrorCode!!.setText(R.string.invalid_security_code)
        textErrorCode!!.visibility = View.VISIBLE
        isValid = false
    } else {
        textErrorCode!!.setText(R.string.invalid_security_code)
        textErrorCode!!.visibility = View.GONE
    }
    if (edtCardHolderName!!.text.trim().isNullOrEmpty()) {
        textErrorCardHolderName!!.visibility = View.VISIBLE
        isValid = false
    } else {
        textErrorCardHolderName!!.visibility = View.GONE
    }
    return isValid
}

private fun openCamera() {

    Permissions.check(
        context /*context*/,
        Manifest.permission.CAMERA,
        null,
        object : PermissionHandler() {
            override fun onGranted() {
                // do your task.
                var i = Intent(requireActivity(), CameraActivity::class.java)
                resultLauncher.launch(i)
            }

            override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                super.onDenied(context, deniedPermissions)
                showSettingsDialog(requireActivity(), "Camera")
            }

        })


}

var resultLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data != null) {
                if (data.hasExtra("number")) {
                    edtCardNumber!!.setText(
                        data.extras!!["number"].toString().replace(" ", " ")
                    )
                    edtCardHolderName!!.setText("")
                    edtExpiryDate!!.setText("")
                    edtCode!!.setText("")

                    if (data.hasExtra("month") && data.hasExtra("year")) {
                        edtExpiryDate!!.setText(data.extras!!["month"].toString()+"/"+ data.extras!!["year"].toString())

                    }

                    if (data.hasExtra("owner")) {
                        edtCardHolderName!!.setText(data.extras!!["owner"].toString())

                    }
                }

            }

        }
    }

fun showSettingsDialog(context: Context, data: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Permission Denied")
    builder.setMessage("You have denied the $data permission. Please enable it from the 'Settings' section of your phone.")
    builder.setPositiveButton(
        "Go to Settings"
    ) { dialog, which ->
        dialog.cancel()
        openSettings(context)
    }
    builder.setNegativeButton(
        "Cancel"
    ) { dialog, which -> dialog.cancel() }
    builder.show()

}

fun openSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    (context as AppCompatActivity).startActivityForResult(intent, 101)
}

private fun showTooltip(message: String, view: View) {
    val balloon: Balloon = Balloon.Builder(requireActivity())
        .setArrowSize(10)
        .setArrowOrientation(ArrowOrientation.TOP)
        .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
        .setArrowPosition(0.5f)
        .setWidth(WRAP)
        .setHeight(WRAP)
        .setTextSize(10f)
        .setTextIsHtml(true)
        .setCornerRadius(4f)
        .setMarginLeft(40)
        .setMarginRight(35)
        .setPadding(10)
        .setText(message)
        .setTextColor(ContextCompat.getColor(context!!, R.color.white))
        .setTextIsHtml(true)
        .setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_primary
            )
        )
        .setBalloonAnimation(BalloonAnimation.FADE)
        .setLifecycleOwner(viewLifecycleOwner)
        .build()

    requireView().showAlignTop(balloon)

}

private fun randomNumber(): Int {
    return Random().nextInt(900) + 100
}

override fun onDestroyView() {
    if (handler != null) {
        handler!!.removeCallbacksAndMessages(null)
    }
    super.onDestroyView()
}

}