package com.edesign.paymentsdk.version2

enum class PaymentMethod {
    Cards("Cards");


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