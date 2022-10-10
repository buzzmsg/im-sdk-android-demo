package com.im.sdk.logic

import com.im.sdk.api.CreateChat
import com.im.sdk.api.CreateChatRequest
import com.im.sdk.core.id.ChatId
import com.im.sdk.core.net.ResponseResult
import com.im.sdk.db.ConversationDbManager
import com.im.sdk.db.event.ConversationEvent
import com.im.sdk.db.model.ConversationModel

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


    fun createChat(aChatId: String, chatName: String ,auids: MutableList<String>): ResponseResult<Any?> {

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