package com.xuandq.facemaskdetection.utils

import java.text.DecimalFormat
import kotlin.math.roundToInt
import kotlin.math.withSign

object PointConverter {
    fun fromMoney(amount: Int?): Float? {
        amount ?: return null
        val point = amount.toFloat() / VNDPerPoint
        return (point * 100).roundToInt() / 100F
    }
}