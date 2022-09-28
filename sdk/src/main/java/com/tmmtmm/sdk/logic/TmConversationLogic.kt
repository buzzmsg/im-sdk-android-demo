package com.tmmtmm.sdk.logic

import com.tmmtmm.sdk.constant.MessageDeleteStatus.IS_DEL
import com.tmmtmm.sdk.core.db.DataBaseManager
import com.tmmtmm.sdk.core.id.ChatId
import com.tmmtmm.sdk.db.ConversationDbManager
import com.tmmtmm.sdk.db.event.ConversationEvent
import com.tmmtmm.sdk.db.model.ConversationModel
import com.tmmtmm.sdk.db.model.MessageModel

/**
 * @description
 * @version
 */
class TmConversationLogic private constructor() {


    companion object {
        val INSTANCE: TmConversationLogic by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            TmConversationLogic()
        }
    }

    fun receiveConversation(
        availableMessages: MutableList<MessageModel>,
        chatIds: MutableSet<String>
    ) {
        //save conversation
        if (availableMessages.isEmpty()) {
            return
        }
        saveOrUpdateConversations(availableMessages)

        //send conversation event
        ConversationEvent.send(chatIds)

        //todo unread
    }

    fun saveOrUpdateConversations(messageList: MutableList<MessageModel>?) {
        val messageMap = messageList
            ?.filter { messageEntity ->
                messageEntity.delStatus != IS_DEL
            }?.groupBy(keySelector = { it.chatId }, valueTransform = { messageEntity ->
                messageEntity
            })

        if (messageMap.isNullOrEmpty()) {
            return
        }
        val receiveConversations: MutableList<ConversationModel> = mutableListOf()
        //get last message in conversation group and generate a new conversation list
        for ((_, value) in messageMap) {
            val maxMessageEntity = value.maxByOrNull { messageEntity ->
                messageEntity.sequence ?: 0
            }

            val chatId = maxMessageEntity?.chatId
            val mid = maxMessageEntity?.mid
            if (chatId.isNullOrBlank() || mid.isNullOrBlank()) {
                continue
            }
            val mChatId = ChatId.createById(chatId)
            val uid = if (mChatId.isSingle()) mChatId.getTargetId() else mChatId.encode()
            if (uid.isBlank()) {
                continue
            }
//            val timestamp = maxMessageEntity.displayTime
            val timestamp =
                if (maxMessageEntity.sender == TmLoginLogic.getInstance().getUserId()) maxMessageEntity.displayTime
                    ?: 0 else maxMessageEntity.sendTime ?: 0L
            val conversationModel = ConversationModel(
                chatId = chatId,
                uid = uid,
                timeStamp = timestamp,
                lastMid = maxMessageEntity.mid
            )
            receiveConversations.add(conversationModel)
        }

        val existConversations = ConversationDbManager.INSTANCE.queryRawConversations()

        val notExistConversations = if (existConversations.isNullOrEmpty()) {
            receiveConversations
        } else receiveConversations.minus(existConversations).toMutableList()

        //update
        if (!existConversations.isNullOrEmpty() && receiveConversations.isNotEmpty()) {
            val receiveConversationMap = receiveConversations.associateBy({ it.chatId }, { it })
            val needUpdateConversations = existConversations.intersect(receiveConversations.toSet())

            for (needUpdateConversation in needUpdateConversations) {
                val existConversation = receiveConversationMap[needUpdateConversation.chatId]
                if (existConversation != null) {
                    needUpdateConversation.timeStamp = existConversation.timeStamp
                    needUpdateConversation.lastMid = existConversation.lastMid
                    needUpdateConversation.lastMessageIndex = -1
                    notExistConversations.add(needUpdateConversation)
                }
            }
        }

        if (notExistConversations.isEmpty()) {
            return
        }

        //insert
        DataBaseManager.getInstance().getDataBase()?.conversationDao()
            ?.insertGroupConversations(notExistConversations)

        //update not exist groupInfo
        val groupIds =
            notExistConversations.map { conversationEntity ->
                conversationEntity.chatId
            }.toMutableList()
        if (groupIds.isEmpty()) {
            return
        }

//        globalIO {
//            ConversationManager.refreshRemoteConversationList(groupIds, forceUpdate = false)
//        }
    }

    fun insertOrUpdateConversation(messageEntity: MessageModel) {
        val receiveConversations: MutableList<ConversationModel> = mutableListOf()

        val timestamp =
            if (messageEntity.sender == TmLoginLogic.getInstance().getUserId()) messageEntity.crateTime
                ?: 0 else messageEntity.sendTime ?: 0
        val localConversation = ConversationDbManager.INSTANCE.queryRawConversations(mutableSetOf(messageEntity.chatId))?.elementAtOrNull(0)

        if (localConversation == null) {
            val mChatId = ChatId.createById(messageEntity.chatId)
            val uid = if (mChatId.isSingle()) mChatId.getTargetId() else mChatId.encode()
            val conversationModel = ConversationModel(
                chatId = messageEntity.chatId,
                uid = uid,
                timeStamp = timestamp,
                lastMid = messageEntity.mid,
                lastMessageIndex = -1
            )
            receiveConversations.add(conversationModel)
        } else {
            localConversation.lastMid = messageEntity.mid
            localConversation.timeStamp = timestamp
            localConversation.lastMessageIndex = -1
            receiveConversations.add(localConversation)
        }

        //insert
        DataBaseManager.getInstance().getDataBase()?.conversationDao()
            ?.insertGroupConversations(receiveConversations)
    }


}