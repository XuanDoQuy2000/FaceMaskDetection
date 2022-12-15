package com.xuandq.facemaskdetection.data.model.common

sealed class BaseError(open val message: String) {
    data class Other(override val message: String): BaseError(message)
    data class DBError(override val message: String): BaseError(message)
    data class FileError(override val message: String): BaseError(message)
}
