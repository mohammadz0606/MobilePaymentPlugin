package com.edesign.paymentsdk.version2.savedCardAPI

class SavedCardModel(
    var cardNumber: String,
    var cardLogo: String,
    var cardType: String,
    var cardExpDate: String,
    var token: String,
    var cardHolderName: String
) {


    var cvv=""
}