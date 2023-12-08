package com.edesign.paymentsdk.Utils

import android.content.Context
import com.edesign.paymentsdk.version2.exceptions.InvalidPaymentCardNumber
import com.edesign.paymentsdk.R
import com.netcetera.threeds.sdk.api.utils.DsRidValues

/**
 * Utility class for matching card numbers to a Directory Server Id.
 */
class DsIdMatcher {

  companion object{
    @Throws(InvalidPaymentCardNumber::class)
    fun directoryServerIdForCard(context:Context,cardNumber: String): String {
      return when {
        cardNumber.matches(CardRegExs.VISA) -> DsRidValues.VISA
        cardNumber.matches(CardRegExs.AMEX) -> DsRidValues.AMEX
        cardNumber.matches(CardRegExs.DINERS) -> DsRidValues.DINERS
        cardNumber.matches(CardRegExs.UNION) -> DsRidValues.UNION
        cardNumber.matches(CardRegExs.CB) -> DsRidValues.CB
        cardNumber.matches(CardRegExs.MASTERCARD) -> DsRidValues.MASTERCARD
        cardNumber.matches(CardRegExs.JCB) -> DsRidValues.JCB
        cardNumber.matches(CardRegExs.MIR) -> throw invalidCardNumberExceptionForSchema("mir",context)
        else -> throw InvalidPaymentCardNumber(context.getString(R.string.missmatch_schema_card_number))
      }
    }

    fun directoryServerIdForType(context:Context,type: String): String {
      return when {
        type.equals("VISA",true) -> DsRidValues.VISA
        type.equals("AMEX",true) -> DsRidValues.AMEX
        type.equals("DINERS",true) -> DsRidValues.DINERS
        type.equals("UNION",true) -> DsRidValues.UNION
        type.equals("CB",true) -> DsRidValues.CB
        type.equals("MASTERCARD",true) -> DsRidValues.MASTERCARD
        type.equals("JCB",true) -> DsRidValues.JCB
        type.equals("MIR",true) -> throw invalidCardNumberExceptionForSchema("mir",context)
        else -> throw InvalidPaymentCardNumber(context.getString(R.string.missmatch_schema_card_number))
      }
    }
    private fun invalidCardNumberExceptionForSchema(schema: String,context: Context): InvalidPaymentCardNumber {
      return InvalidPaymentCardNumber(context.getString(R.string.missing_ds_implementation, schema))
    }
  }
}
