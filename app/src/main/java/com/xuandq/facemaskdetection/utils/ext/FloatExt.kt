package com.xuandq.facemaskdetection.utils.ext

import java.text.DecimalFormat

fun Float.toPointString(): String? {
    val decimalFormat = DecimalFormat("0.0#")
    return try {
        decimalFormat.format(this)
    } catch (e: Exception) {
        null
    }
}