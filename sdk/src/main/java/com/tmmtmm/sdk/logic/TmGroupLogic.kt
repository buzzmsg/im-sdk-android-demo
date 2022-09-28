package com.tmmtmm.sdk.logic

import com.tmmtmm.sdk.core.id.ChatId
import com.tmmtmm.sdk.db.ConversationDbManager
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


    fun createGroup(aChatId: String, auids: MutableList<String>){
        val chatId = ChatId.create(aChatId)

        //todo

        val linkModel = ConversationLinkModel()
        linkModel.aChatId = aChatId
        linkModel.chatId = chatId

        ConversationDbManager.INSTANCE.insertGroupConversationLink(linkModel)


        val conversationModel = ConversationModel()
        conversationModel.chatId = chatId

        ConversationDbManager.INSTANCE.insertGroupConversation(conversationModel)

    }
}