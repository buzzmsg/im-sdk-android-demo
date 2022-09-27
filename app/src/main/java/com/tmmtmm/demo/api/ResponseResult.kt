package com.tmmtmm.demo.api

import com.tmmtmm.demo.exception.TmException


sealed class ResponseResult<out T> {
    data class Success<out T>(val value: T) : ResponseResult<T>()

    data class Failure(val throwable: TmException?) : ResponseResult<Nothing>()
}