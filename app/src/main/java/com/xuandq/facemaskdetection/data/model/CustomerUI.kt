package com.xuandq.facemaskdetection.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerUI(
    val id: Int,
    val name: String? = null,
    val phoneNumber: String? = null,
    val createdTime: Long? = null,
    val imageUri: String? = null,
    var currentPoint: Float? = null,
    var totalPoint: Float? = null,
) : Parcelable{
    fun getTotalPointValue() = totalPoint ?: 0F

    fun getCurrentPointValue() = currentPoint ?: 0F
}
