package com.edesign.paymentsdk.version2.inquiry

import com.edesign.paymentsdk.Inquiry.InquiryResponse


interface InquiryCallback {
    fun onResponse(response: InquiryResponse)
}