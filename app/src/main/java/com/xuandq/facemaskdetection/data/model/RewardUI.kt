package com.xuandq.facemaskdetection.data.model

import android.content.Context
import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.xuandq.facemaskdetection.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class RewardUI (
    val id: Int,
    val name: String? = null,
    val description: String? = null,
    val point: Int? = null,
    val totalQuantity: Int? = null,
    val createdTime: Long? = null,
    val usedQuantity: Int? = null,
) : Parcelable {

    fun getPointValue() = point ?: 0F

    fun getQuantityText() : String {
        return "${usedQuantity ?: 0} / ${totalQuantity ?: 0}"
    }

    fun isFull(): Boolean = (usedQuantity ?: 0) == (totalQuantity ?: 0)

    fun getQuantityTextColor(context: Context) = if (isFull()) {
        ContextCompat.getColor(context, R.color.red)
    } else {
        ContextCompat.getColor(context, R.color.neutral_700)
    }
}