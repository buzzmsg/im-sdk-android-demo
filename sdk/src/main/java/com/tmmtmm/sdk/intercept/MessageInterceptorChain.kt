package com.tmmtmm.sdk.intercept

import com.tmmtmm.im.core.model.message.intercept.MessageInsertInterceptor
import com.tmmtmm.sdk.core.net.ResponseResult
import com.tmmtmm.sdk.db.model.MessageModel


/**
 * @description
 * @time 2022/4/19
 * @version
 */
class MessageInterceptorChain(
    private val interceptors: MutableList<MessageInsertInterceptor>?,
    val index: Int = 0,
    val messageList: MutableList<MessageModel>
) : MessageInsertInterceptor.Chain {

    override fun proceed(messages: MutableList<MessageModel>): ResponseResult<MutableList<MessageModel>> {

        if (index >= (interceptors?.size ?: 0)) return ResponseResult.Success(messages)

        val next = MessageInterceptorChain(interceptors, index + 1, messageList)
        val interceptor = interceptors?.elementAtOrNull(index)//[0]

        return interceptor?.intercept(next) ?: ResponseResult.Success(messages)
    }
}