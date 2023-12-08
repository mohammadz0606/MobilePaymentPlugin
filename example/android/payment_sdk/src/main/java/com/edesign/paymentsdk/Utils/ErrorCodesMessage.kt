package com.edesign.paymentsdk.Utils

/**
 * SDK error code and message
 */
open class ErrorCodesMessage {

    companion object {
        var kSDKNotInitializedErrorCode = "1000"
        var kThreeDSInitializationErrorCode = "1001"
        var kNetworkConnection = "1002"
        var kConfigFileErrorCode = "1003"
        var kAuthenticatePayerErrorCode = "1005"
        var kInvalidCurrencyCodeErrorCode = "1010"
        var kInvalidTransactionIdCode = "1015"
        var kInvalidAmountCode = "1016"
        var kGenericFileError = "1017"
        var kAbnormalErrorCode = "500"
        var kCancelPaymentCode = "1018"
        var kThreeDS2TimeOut = "1019"

        var kSDKNotInitialiazedErrorMessage = "Error Initializing SDK, Verify your merchant ID and Secret Key"
        var kThreeDSInitializationErrorMessage = "Error Initializing 3ds sdk"
        var kNetworkConnectionMessage = "No Internet Connection detected"
        var kConfigFileMessage = "Missing Configuration File"
        var kAuthenticatePayerErrorMessage = "Error authenticating payer"
        var kInvalidCurrencyCodeErrorMessage = "Invalid Currency Code"
        var kInvalidTransactionIdMessage = "Error in transaction Id"
        var kInvalidAmountMessage = "Amount is Missing"
        var kGenericFileMessage = "Error reading generic files"
        var kCancelPaymentMessage = "Payment Cancelled"
        var kThreeDS2TimeOutMessage= "3ds timeout"


        var kErrorCode = "Code"
        var kErrorDescription = "Description"
    }
}