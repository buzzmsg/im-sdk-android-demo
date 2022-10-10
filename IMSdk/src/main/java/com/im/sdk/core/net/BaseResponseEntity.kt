package com.im.sdk.core.net

import androidx.annotation.Keep

@Keep
data class BaseResponseEntity<T>(val code: Int, val msg: String, var data: T? = null) {

    fun isSuccess(): Boolean = code == 200
}