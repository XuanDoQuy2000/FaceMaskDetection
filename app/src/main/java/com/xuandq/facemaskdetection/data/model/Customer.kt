package com.xuandq.facemaskdetection.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String? = null,
    val phoneNumber: String? = null,
    val createdTime: Long? = null,
    val imageUri: String? = null,
) : Parcelable {

}
