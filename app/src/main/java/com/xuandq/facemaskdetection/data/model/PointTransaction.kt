package com.xuandq.facemaskdetection.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class PointTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val customerId: Int,
    val rewardId: Int,
    val pointTransaction: Float? = null,
    val timeStamp: Long? = null,
    val type: String? = null
) : Parcelable {
}