package com.xuandq.facemaskdetection.data.model

data class CustomerUI(
    val id: Int,
    val name: String? = null,
    val phoneNumber: String? = null,
    val createdTime: Long? = null,
    val imageUri: String? = null,
    val currentPoint: Float? = null,
    val totalPoint: Float? = null,
)
