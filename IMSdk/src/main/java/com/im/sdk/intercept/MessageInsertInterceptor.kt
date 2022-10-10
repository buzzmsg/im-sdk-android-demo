package com.im.sdk.intercept


import com.im.sdk.core.net.ResponseResult
import com.im.sdk.db.model.MessageModel

/**
 * @description
 * @time 2022/4/19
 * @version
 */
interface MessageInsertInterceptor {

    fun intercept(chain: Chain): ResponseResult<MutableList<MessageModel>>

    interface Chain {
        fun proceed(messages: MutableList<MessageModel>): ResponseResult<MutableList<MessageModel>>
    }
}


