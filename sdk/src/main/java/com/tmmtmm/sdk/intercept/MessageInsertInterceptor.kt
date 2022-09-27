package com.tmmtmm.im.core.model.message.intercept

import com.tmmtmm.im.core.model.message.MessageEntity
import com.tmmtmm.netcore.ResponseResult

/**
 * @description
 * @time 2022/4/19
 * @version
 */
interface MessageInsertInterceptor {

    fun intercept(chain: Chain): ResponseResult<MutableList<MessageEntity>>

    interface Chain {
        fun proceed(messages: MutableList<MessageEntity>): ResponseResult<MutableList<MessageEntity>>
    }
}


