package com.edesign.paymentsdk.version2.model

import java.io.Serializable
import java.util.*

/**
 * Used for storing data configurations for schemas that will be used for doing transactions.
 */
data class TransactionModel(
  val name: String,
  val purchaseAmount: String,
  val purchaseCurrency: String,
  val messageCategory: String,
  val sdkInterface: String,
  val merchantName: String,
  val merchantCountryCode: String,
  val paymentCard: PaymentCard,
  val messageExtensions: List<MessageExtension> = ArrayList()
) : Serializable {

  companion object {
    /**
     * Creates transaction model with some preconfigured default values.
     *
     * @param name [String]
     * @param accountNumber [String]
     * @return [TransactionModel]
     */
    fun defaultModel(name: String, accountNumber: String): TransactionModel {
      return TransactionModel(
        name = name,
        purchaseAmount = "500",
        purchaseCurrency = "840",
        messageCategory = "01",
        sdkInterface = "03",
        merchantName = "NDM",
        merchantCountryCode = "807",
        paymentCard = PaymentCard(accountNumber, "1225", "John Smith"),
      )
    }
  }
}
