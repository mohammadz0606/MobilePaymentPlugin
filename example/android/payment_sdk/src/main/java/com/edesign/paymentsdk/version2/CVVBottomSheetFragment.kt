package com.edesign.paymentsdk.version2

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.edesign.paymentsdk.R
import com.edesign.paymentsdk.version2.savedCardAPI.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CVVBottomSheetFragment : BottomSheetDialogFragment {
    private var imgBack: ImageView? = null
    private var imgClose: ImageView? = null
    private var textContinue: TextView? = null
    private var textErrorCode: TextView? = null
    private var edtCode: EditText? = null
    lateinit var param: AnotherCardInterface
    var model: SavedCardModel? = null

    constructor() {}

    companion object {
        fun openFragment(
            param: AnotherCardInterface,
            model: SavedCardModel
        ): CVVBottomSheetFragment {
            var fragment = CVVBottomSheetFragment()
            fragment.param = param
            fragment.model = model
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
        return inflater.inflate(R.layout.add_cvv_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgBack = view.findViewById(R.id.imgBack)
        imgClose = view.findViewById(R.id.imgCancel)
        textContinue = view.findViewById(R.id.textContinue)
        edtCode = view.findViewById(R.id.edtCode)
        textErrorCode = view.findViewById(R.id.textErrorCode)




        imgBack!!.setOnClickListener { dismiss() }
        imgClose!!.setOnClickListener {
            param.onClickAnotherCard()
            dismiss()
        }
        textContinue!!.setOnClickListener {
            if (validate()) {
                textErrorCode!!.visibility = View.INVISIBLE
                model!!.cvv = edtCode!!.text.toString().trim()
                param.onCardSelected(model!!)
                dismiss()
            }
        }

    }

    private fun validate(): Boolean {
        if (edtCode!!.text.toString().trim().isNullOrEmpty()
        ) {
            textErrorCode!!.setText(R.string.enter_a_security_code)
            textErrorCode!!.visibility = View.VISIBLE
            return false
        }
        else if (edtCode!!.text.toString()
                .trim().length < 3
        ) {
            textErrorCode!!.setText(R.string.invalid_security_code)
            textErrorCode!!.visibility = View.VISIBLE
            return false
        }
        return true
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

}

