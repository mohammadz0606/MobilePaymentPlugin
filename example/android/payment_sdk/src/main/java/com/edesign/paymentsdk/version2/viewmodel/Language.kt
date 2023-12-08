package com.edesign.paymentsdk.version2.viewmodel

enum class Language {
    AR("ar"),
    EN("en"),
    TR("tr");


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