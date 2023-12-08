package com.edesign.paymentsdk.Utils

/**
 * Card number regular expressions.
 */
object CardRegExs {

  /** Visa regex.  */
  val VISA = Regex("4[0-9]*")

  /** Mastercard regex.  */
  val MASTERCARD = Regex("(2[0-1]|220[5-9]|22[1-9]|2[3-9]|5|6)[0-9]*")

  /** Amex regex.  */
  val AMEX = Regex("(34|37)[0-9]*")

  /** Diners regex.  */
  val DINERS = Regex("36[0-9]*")

  /** JCB regex.  */
  val JCB = Regex("35[0-9]*")

  /** Mir regex.  */
  val MIR = Regex("220[0-4][0-9]*")

  /** Union regex.  */
  val UNION = Regex("^(62[0-9]{14,17})$")

  /** CB regex.  */
  val CB = Regex("^(501767[0-9]{10}|778933512241[0-9]{4})$")

}
