package com.xuandq.facemaskdetection.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PointTransactionUI(
    val id: Int,
    val customerId: Int,
    val customerName: String? = null,
    val rewardId: Int? = null,
    val rewardName: String? = null,
    val point: Float? = null,
    val money: Int? = null,
    val timeStamp: Long? = null,
    val type: String? = null,
    val quantity: Int? = null,
    val totalPoint: Float? = null
): Parcelable{
    fun getCustomerInfo(): String {
        return "ID: $customerId | Name: ${customerName ?: ""}"
    }

    fun getContentInfo(): String {
        return if (type == "PLUS") {
            "${money ?: ""}"
        } else {
            "Reward: ${rewardId ?: ""} | ${rewardName ?: ""} | ${point ?: ""} point  x ${quantity ?: ""}"
        }
    }

    fun getTypeString(): String {
        return when(type) {
            "PLUS" -> "Tích điểm"
            "MINUS" -> "Đổi quà"
            else -> ""
        }
    }
}