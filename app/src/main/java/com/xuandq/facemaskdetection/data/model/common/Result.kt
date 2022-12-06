package com.xuandq.facemaskdetection.data.model.common

sealed class Result<out T> {
    data class Success<out R>(val data: R) : Result<R>()
    data class Error(val error: BaseError): Result<Nothing>()
}
