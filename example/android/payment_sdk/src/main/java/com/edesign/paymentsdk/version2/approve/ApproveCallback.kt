package com.edesign.paymentsdk.version2.approve

import com.edesign.paymentsdk.Approve.ApproveResponse


interface ApproveCallback {
    fun onResponse(response: ApproveResponse)
}