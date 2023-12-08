package com.edesign.paymentsdk.version2.model

import java.io.Serializable

/**
 * Payment card data class.
 */
data class PaymentCard(
  val cardAccountNumber: String,
  val cardExpiringDate: String,
  val cardHolderName: String
) : Serializable
