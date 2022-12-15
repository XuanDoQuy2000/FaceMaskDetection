package com.xuandq.facemaskdetection.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    val path: String,
    val name: String,
    val extension: String,
    val customerId: Int? = null,
    var isSelected: Boolean = false,
): Parcelable {

}