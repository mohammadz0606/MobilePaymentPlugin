package com.example.sts_one_pay_example

import io.flutter.embedding.android.FlutterActivity


class MainActivity : FlutterActivity() {
}

/*
private fun paymentMethod() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val request = OpenPaymentRequest()
        request.paymentType = PaymentType.SALES.name
        PaymentType.PREAUTH.name
        request.add("AuthenticationToken", "MmQ2OTQyMTQyNjUyZmIzYTY4ZGZhOThh")
        request.add("TransactionID", System.currentTimeMillis().toString())
        request.add("MerchantID", "AirrchipMerchant")
        request.add("ClientIPaddress", "3.7.21.24")
        request.add("Amount", "200")
        request.add("Currency", "682")
        request.add("PaymentDescription", "Sample Payment")
        request.add("AgreementID", "")
        request.add("AgreementType", "")
        request.add("Language", "en")
        request.add("ThreeDSEnable", true)
        request.add("TokenizeCard", true)
        request.add("CardScanningEnable", true)
        request.add("SaveCard", true)
        request.add("PaymentMethod", arrayListOf<String>(PaymentMethod.Cards.name))
        request.add(
            "CardType",
            arrayListOf<String>(
                VISA.name,
                MASTERCARD.name,
                AMEX.name,
                DINERS.name,
                UNION.name,
                JCB.name,
                DISCOVER.name,
                MADA.name,
            ),
        )
        //optional param
        //request.addOptional("ItemID", "Item1")
        //request.addOptional("Quantity", "1")
        //request.addOptional("Version", "1.0")
        request.addOptional("FrameworkInfo", "Android 7.0")
        request.add("Tokens", arrayOf<String>())
        val checkout = Checkout(this, this)
        checkout.open(request)
    }
 */