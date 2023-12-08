package com.edesign.paymentsdk.version2.model

import com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters


class AuthenticateRequestParamData {
    private var error: Throwable? = null
    private var authenticationRequestParameters: AuthenticationRequestParameters? = null

    fun error(error: Throwable): AuthenticateRequestParamData {
        this.error = error
        return this
    }

    fun success(data: AuthenticationRequestParameters): AuthenticateRequestParamData {
        authenticationRequestParameters = data
        error = null
        return this
    }

    fun getAuthenticationRequestParameters(): AuthenticationRequestParameters? {
        return authenticationRequestParameters
    }

    fun getError(): Throwable? {
        return error
    }

}