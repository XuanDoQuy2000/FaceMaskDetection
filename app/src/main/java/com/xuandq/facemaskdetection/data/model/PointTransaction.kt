package com.xuandq.facemaskdetection.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xuandq.facemaskdetection.utils.PointConverter
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class PointTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val customerId: Int,
    val rewardId: Int? = null,
    val point: Float? = null,
    val money: Int? = null,
    val timeStamp: Long? = null,
    val type: String? = null,
    val quantity: Int? = null,
    val totalPoint: Float? = null
) : Parcelable {
    companion object {
        fun createWithMoney(money: Int, customerId: Int): PointTransaction {
            return PointTransaction(
                id = 0,
                customerId = customerId,
                type = "PLUS",
                timeStamp = System.currentTimeMillis(),
                totalPoint = PointConverter.fromMoney(money)
            )
        }
    }
}