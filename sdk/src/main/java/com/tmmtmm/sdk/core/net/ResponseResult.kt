package com.tmmtmm.sdk.core.net

import com.tmmtmm.sdk.core.net.exception.TmException

sealed class ResponseResult<out T> {
    data class Success<out T>(val value: T) : ResponseResult<T>()

    data class Failure(val throwable: TmException?) : ResponseResult<Nothing>()
}