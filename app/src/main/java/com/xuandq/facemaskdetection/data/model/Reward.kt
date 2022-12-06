package com.xuandq.facemaskdetection.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Reward(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String? = null,
    val description: String? = null,
    val point: Int? = null,
    val totalQuantity: Int? = null,
    val createdTime: Long? = null
) : Parcelable {
}