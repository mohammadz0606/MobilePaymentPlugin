package com.edesign.paymentsdk.version2.model

import androidx.annotation.Keep
import java.io.Serializable

/**
 * Data necessary to support requirements not otherwise defined in the 3D Secure message are carried in a Message
 * Extension.
 *
 *
 * Requirements of this field are set by each Directory Server.
 *
 * The fields for each message extension attribute are: id -> A unique identifier for the extension. Payment System
 * Registered Application Provider Identifier (RID) is required as prefix of the ID. The maximum length is 64
 * characters. name -> The name of the extension data set as defined by the extension owner. Maximum length is 64
 * characters. criticalityIndicator -> A boolean value indicating whether the recipient must understand the contents of
 * the extension to interpret the entire message. data -> The data carried in the extension. The maximum length is 8059
 * characters.
 */
@Keep
class MessageExtension(messageExtensionType: MessageExtensionType) : CommonMessageExtension(messageExtensionType),
  Serializable {
  var data: String? = null

  companion object {
    /**
     * Creates a [MessageExtension] with predefined Mastercard PSD2 values.
     *
     * @return populated [MessageExtension].
     */
    fun withPredefinedMastercardValues(): MessageExtension {
      val messageExtension = MessageExtension(MessageExtensionType.MASTERCARD)
      messageExtension.apply {
        name = "Merchant Data"
        id = "A000000004-merchantData"
        criticalityIndicator = false
      }
      return messageExtension
    }

    /**
     * Creates a [MessageExtension] with predefined Visa PSD2 values.
     *
     * @return populated [MessageExtension].
     */
    fun withPredefinedVisaValues(): MessageExtension {
      val messageExtension = MessageExtension(MessageExtensionType.VISA)
      messageExtension.apply {
        name = "Acquirer Country Code Extension"
        id = "A000000003-001"
        criticalityIndicator = false
      }
      return messageExtension
    }
  }
}
