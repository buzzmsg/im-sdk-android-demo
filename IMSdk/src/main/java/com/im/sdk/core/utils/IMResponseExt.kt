package com.im.sdk.core.utils

import com.im.sdk.core.net.ResponseResult

/**
 * @description
 *
 * @time 2021/5/18 10:47 上午
 * @version
 */



inline fun <T : Any> ResponseResult<T?>.onSuccess(crossinline action: (T?) -> Unit): ResponseResult<T?> {
    if (this is ResponseResult.Success) {
        action(value)
    }
    return this
}

inline fun <T : Any> ResponseResult<T?>.onError(crossinline action: (code: Int, msg: String) -> Unit): ResponseResult<T?> {
    if (this is ResponseResult.Failure) {
        action(throwable?.code?:0,throwable?.message?:"")
    }
    return this
}
