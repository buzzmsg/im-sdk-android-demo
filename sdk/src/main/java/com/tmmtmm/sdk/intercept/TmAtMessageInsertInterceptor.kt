package com.tmmtmm.sdk.intercept

import com.tmmtmm.im.core.TmHelper
import com.tmmtmm.im.core.message.TmAtMessageContent
import com.tmmtmm.im.core.message.core.TmMessageContentType
import com.tmmtmm.im.core.model.message.MessageModel
import com.tmmtmm.im.core.model.message.TmAtMessageManager
import com.tmmtmm.im.core.contact.logic.TmLoginManager
import com.tmmtmm.im.core.model.message.intercept.MessageInsertInterceptor
import com.tmmtmm.sdk.constant.MessageContentType
import com.tmmtmm.sdk.core.net.ResponseResult
import com.tmmtmm.sdk.db.model.MessageModel
import com.tmmtmm.sdk.logic.TmLoginLogic

/**
 * @description
 * @time 2022/4/19
 * @version
 */

class TmAtMessageInsertInterceptor : MessageInsertInterceptor {
    override fun intercept(chain: MessageInsertInterceptor.Chain): ResponseResult<MutableList<MessageModel>> {

        if (chain !is MessageInterceptorChain) {
            return ResponseResult.Success(mutableListOf())
        }

        for (MessageModel in chain.messageList) {
            if (MessageModel.type != MessageContentType.ContentType_At) continue

            val messageContentFromPayload = TmHelper.getInstance().messageContentFromPayload(
                MessageModel.type ?: MessageContentType.ContentType_Unknown,
                MessageModel.content,
            ) ?: continue

//            if (messageContentFromPayload !is TmAtMessageContent) continue

            val items = messageContentFromPayload.items
            if (items.isNullOrEmpty()) continue

            var at = 0
            for (item in items) {


                if (item.value == TmLoginLogic.getInstance().getUserId()) {
                    at = at or TmAtMessageManager.TYPE_AT_ME
                }

                if (item.value == TmAtMessageContent.AT_ALL) {
                    at = at or TmAtMessageManager.TYPE_AT_ALL
                }
            }
            MessageModel.atType = at
        }
        return ResponseResult.Success(chain.messageList)
    }
}