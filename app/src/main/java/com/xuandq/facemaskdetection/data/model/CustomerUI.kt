package com.xuandq.facemaskdetection.data.model

import android.os.Parcelable
import androidx.room.Ignore
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerUI(
    val id: Int,
    val name: String? = null,
    val phoneNumber: String? = null,
    val createdTime: Long? = null,
    var currentPoint: Float? = null,
    var totalPoint: Float? = null,
) : Parcelable{
    @IgnoredOnParcel
    @Ignore
    var images: List<Image>? = null

    fun getTotalPointValue() = totalPoint ?: 0F

    fun getCurrentPointValue() = currentPoint ?: 0F
}
