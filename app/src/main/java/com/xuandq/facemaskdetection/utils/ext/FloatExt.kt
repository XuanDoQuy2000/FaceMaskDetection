package com.xuandq.facemaskdetection.utils.ext

import java.text.DecimalFormat

fun Float.toPointString(): String? {
    val decimalFormat = DecimalFormat("#.##")
    return try {
        decimalFormat.format(this)
    } catch (e: Exception) {
        null
    }
}