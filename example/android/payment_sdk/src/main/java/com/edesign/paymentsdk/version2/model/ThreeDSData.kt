package com.edesign.paymentsdk.version2.model

import com.netcetera.threeds.sdk.api.ThreeDS2Service


class ThreeDSData {
    private var error: Throwable? = null
    private var threeDS2Service: ThreeDS2Service? = null

    fun error(error: Throwable): ThreeDSData {
        this.error = error
        return this
    }

    fun success(data: ThreeDS2Service): ThreeDSData {
        threeDS2Service = data
        error = null
        return this
    }

    fun getthreeDS2ServiceInstance(): ThreeDS2Service? {
        return threeDS2Service
    }

    fun getError(): Throwable? {
        return error
    }

}