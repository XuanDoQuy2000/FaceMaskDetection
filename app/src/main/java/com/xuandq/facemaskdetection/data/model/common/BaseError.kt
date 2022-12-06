package com.xuandq.facemaskdetection.data.model.common

sealed class BaseError {
    data class Other(val message: String): BaseError()
    data class DBError(val message: String): BaseError()
}
