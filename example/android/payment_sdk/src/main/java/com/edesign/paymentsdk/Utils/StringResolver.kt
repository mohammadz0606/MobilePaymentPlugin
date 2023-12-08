package com.edesign.paymentsdk.Utils

import android.content.res.Resources
import androidx.annotation.StringRes

/**
 * Resolves string resources.
 */
class StringResolver constructor(private val resources: Resources) {

  /**
   * Return the string value associated with a particular resource ID.
   *
   * @param resId `int`
   * @return [String]
   */
  fun getString(@StringRes resId: Int): String {
    return resources.getString(resId)
  }

  /**
   * Return the string value associated with a particular resource ID, substituting the format arguments.
   *
   * @param resId `int`
   * @param formatArgs [Object] the format arguments
   * @return [String]
   */
  fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
    return resources.getString(resId, *formatArgs)
  }
}
