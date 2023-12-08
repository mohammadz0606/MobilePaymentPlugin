package com.edesign.paymentsdk.version2

enum class PaymentType {
    SALES("Sale"),
    PREAUTH("Pre-Auth");


    private final var text :String="";

    /**
     * @param text
     */
    private constructor(text:String) {
        this.text = text;
    }

    override fun toString(): String {
        return super.toString()
    }
}