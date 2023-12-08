package com.edesign.paymentsdk.version2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.edesign.paymentsdk.version2.model.AuthenticateRequestParamData
import com.edesign.paymentsdk.version2.model.ThreeDSData
import com.edesign.paymentsdk.version2.repository.CardPaymentRepository
import com.netcetera.threeds.sdk.api.transaction.Transaction

class CardPaymentViewModel:ViewModel() {
    var repository:CardPaymentRepository
    var mutableLiveData: MutableLiveData<ThreeDSData> = MutableLiveData()
    var authenticationRequestParametersLiveData: MutableLiveData<AuthenticateRequestParamData> = MutableLiveData()
    var mutableTransaction: MutableLiveData<Transaction> = MutableLiveData()


    init {
        repository= CardPaymentRepository()
        mutableLiveData= repository.getThreeDsLiveData()
        mutableTransaction= repository.getTransactionLiveData()
        authenticationRequestParametersLiveData= repository.getAuthenticateRequestParamLiveData()
    }


    fun callInitialize3ds(){
        repository.initialize3ds()
    }

    fun authenticateRequestParameter(cardNumber: String,type:String,threeDSVersion:String) {
        repository.authenticateRequestParameter(cardNumber.replace(" ","").toString(),type,threeDSVersion)
    }

    fun getThreeDsLiveData():MutableLiveData<ThreeDSData>{
        return  mutableLiveData
    }

    fun getAuthenticateRequestParamLiveData():MutableLiveData<AuthenticateRequestParamData>{
        return  authenticationRequestParametersLiveData
    }
    fun getTransactionLiveData():MutableLiveData<Transaction>{
        return  mutableTransaction
    }
}