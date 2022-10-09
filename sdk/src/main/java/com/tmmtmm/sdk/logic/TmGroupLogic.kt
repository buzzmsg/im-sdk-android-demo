package com.tmmtmm.sdk.logic

import com.tmmtmm.sdk.api.CreateChat
import com.tmmtmm.sdk.api.CreateChatRequest
import com.tmmtmm.sdk.api.CreateChatResponse
import com.tmmtmm.sdk.core.id.ChatId
import com.tmmtmm.sdk.core.net.ResponseResult
import com.tmmtmm.sdk.db.ConversationDbManager
import com.tmmtmm.sdk.db.event.ConversationEvent
import com.tmmtmm.sdk.db.model.ConversationLinkModel
import com.tmmtmm.sdk.db.model.ConversationModel

/**
 * @description
 * @version
 */
class TmGroupLogic {

    companion object {

        val INSTANCE: TmGroupLogic by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            TmGroupLogic()
        }
    }


    fun createChat(aChatId: String, chatName: String ,auids: MutableList<String>): ResponseResult<Any?>{

        val chatId = ChatId.create(aChatId)

        val existChatIds = ConversationDbManager.INSTANCE.queryExistChat(mutableListOf(chatId))

        if (existChatIds.isNotEmpty()){
            return ResponseResult.Success(Any())
        }

        val result = CreateChat.excute(CreateChatRequest(id = chatId, aChatId = aChatId, name = chatName, auids = auids, type = CreateChat.CHAT_SINGLE_TYPE))

        if (result !is ResponseResult.Success){
            return result
        }
        val conversationModel = ConversationModel()
        conversationModel.chatId = chatId
        conversationModel.aChatId = aChatId
        conversationModel.timeStamp = System.currentTimeMillis()
        conversationModel.name = chatName

        ConversationDbManager.INSTANCE.insertGroupConversation(conversationModel)

        ConversationEvent.send(mutableSetOf(chatId))

        return result

    }
}