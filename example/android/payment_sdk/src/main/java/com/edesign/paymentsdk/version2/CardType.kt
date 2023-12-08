package com.edesign.paymentsdk.version2

enum class CardType {
    VISA("VISA"),
    MASTERCARD("MASTERCARD"),
    AMEX("AMEX"),
    DINERS("DINERS"),
    UNION("UNION"),
    JCB("JCB"),
    DISCOVER("DISCOVER"),
    MADA("MADA");


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