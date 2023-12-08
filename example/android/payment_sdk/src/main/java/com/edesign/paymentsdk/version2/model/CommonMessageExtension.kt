package com.edesign.paymentsdk.version2.model

import androidx.annotation.Keep
import java.io.Serializable

/**
 * Class containing the common message extension.
 */
@Keep
open class CommonMessageExtension(val messageExtensionType: MessageExtensionType) : Serializable {
    var name: String? = null
    var id: String? = null
    var criticalityIndicator = false

    enum class MessageExtensionType {
        CUSTOM, MASTERCARD, VISA
    }
}
