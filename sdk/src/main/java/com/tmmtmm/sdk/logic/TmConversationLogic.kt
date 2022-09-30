package com.tmmtmm.sdk.logic

import com.tmmtmm.sdk.constant.MessageDeleteStatus.IS_DEL
import com.tmmtmm.sdk.core.db.DataBaseManager
import com.tmmtmm.sdk.core.id.ChatId
import com.tmmtmm.sdk.db.ConversationDbManager
import com.tmmtmm.sdk.db.event.ConversationEvent
import com.tmmtmm.sdk.db.model.ConversationModel
import com.tmmtmm.sdk.db.model.MessageModel
import com.tmmtmm.sdk.dto.TmConversation
import com.tmmtmm.sdk.ui.view.vo.TmmConversationVo

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
                if (maxMessageEntity.sender == TmLoginLogic.getInstance()
                        .getUserId()
                ) maxMessageEntity.displayTime
                    ?: 0 else maxMessageEntity.sendTime ?: 0L
            val conversationModel = ConversationModel(
                chatId = chatId,
                aChatId = maxMessageEntity.aChatId,
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
            if (messageEntity.sender == TmLoginLogic.getInstance()
                    .getUserId()
            ) messageEntity.crateTime
                ?: 0 else messageEntity.sendTime ?: 0
        val localConversation =
            ConversationDbManager.INSTANCE.queryRawConversations(mutableSetOf(messageEntity.chatId))
                ?.elementAtOrNull(0)

        if (localConversation == null) {
            val mChatId = ChatId.createById(messageEntity.chatId)
            val uid = if (mChatId.isSingle()) mChatId.getTargetId() else mChatId.encode()
            val conversationModel = ConversationModel(
                chatId = messageEntity.chatId,
                aChatId = messageEntity.aChatId,
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


    fun getConversationCombination(chatIds: MutableSet<String>?): MutableList<TmmConversationVo>? {

        if (chatIds.isNullOrEmpty()) {
            return null
        }

        val conversations = getConversationByChatIds(chatIds)

//        val draftMessagesMap =
//            MessageDraftManager.getInstance().getDraftMessageMap(chatIds = chatIds.toMutableList())


        return conversations?.map { tmConversationInfo ->

//            val draftMessage = draftMessagesMap?.getOrDefault(tmConversationInfo.chatId, null)

            val conTime = tmConversationInfo.timestamp
//            val draftTime = draftMessage?.displayTime ?: 0
//            val timeStamp = if (draftTime > conTime) draftTime else conTime

            tmConversationInfo.timestamp = conTime
//            tmConversationInfo.draftTmMessage = draftMessage

            tmConversationInfo

        }?.toMutableList()?.transform()?.sortedByDescending { it.dateUpdated }?.toMutableList()

    }

    fun getConversationByChatIds(chatIds: MutableSet<String>?): MutableList<TmConversation>? {

        if (chatIds.isNullOrEmpty()) {
            return null
        }

        val conversationList =
            DataBaseManager.getInstance().splitArray(chatIds.toMutableList()) { value ->
                ConversationDbManager.INSTANCE.queryConversations(value.toMutableSet())
                    ?: mutableListOf()
            }


//        val maxMessageList = TmMessageLogic.INSTANCE
//            .queryMaxMessageIndexByChatId(chatIds)
//            .associateBy({ it.chatId }, { it.id })
//
//        val maxSequenceMessageList = TmMessageLogic.INSTANCE
//            .queryMaxMessageSequenceByChatId(chatIds)
//            .associateBy({ it.chatId }, { it.sequence })
//
//
//        val normalConversationList = conversationList.filter { conversationEntity ->
//            //max message index
//            val headIndex = maxMessageList?.get(conversationEntity.chatId) ?: 0
//            //conversation max sequence
//            val maxSequence = maxSequenceMessageList?.get(conversationEntity.chatId) ?: 0
//            !conversationIsRealHide(
//                chatId = conversationEntity.chatId,
//                messageIndex = headIndex,
//                messageMaxSequence = maxSequence,
//                hideIndex = conversationEntity.lastMessageIndex,
//                hideSequence = conversationEntity.hideSequence
//            )
//        }.toMutableList()
        val normalConversationList = conversationList

        if (normalConversationList.isEmpty()) {
            return mutableListOf()
        }

        val lastMids = normalConversationList.map {
            it.lastMid ?: ""
        }.toMutableSet()

        val lastMessagesMap = TmMessageLogic.INSTANCE.queryTmMessageMapByMids(lastMids)


//        val conversationUserInfoMap =
//            TmConversationApiManager.getInstance()
//                .getConversationUserInfoMap(normalConversationList)

        val conversationInfoList = mutableListOf<TmConversation>()

        normalConversationList.forEach { conversationEntity ->
            val lastMessage = lastMessagesMap?.get(conversationEntity.lastMid ?: "")
//            if (ChatId.createById(conversationEntity.chatId).isSingle()) {
//                val userInfo = conversationUserInfoMap[conversationEntity.uid]
//                if ((userInfo?.isFriend == UserRelationChangeLogic.FRIEND_SHIP_BOTH_FRIEND
//                            || userInfo?.isFriend == UserRelationChangeLogic.FRIEND_SHIP_SINGLE_DELETE_YOU)
//                    && !RelationShipManager.isBlockUser(userInfo.blockStatus ?: 0)
//                ) {
            val conversationInfo = TmConversation(
                id = 0,
                chatId = conversationEntity.chatId,
                aChatId = conversationEntity.aChatId,
                uid = conversationEntity.uid,
                lastMid = conversationEntity.lastMid,
                topTimestamp = conversationEntity.topTime ?: 0,
                timestamp = conversationEntity.timeStamp ?: 0,
                lastTmMessage = lastMessage,
                isMute = conversationEntity.isMute,
                isMuteShow = false,
                headIndex = conversationEntity.lastMessageIndex,
                hideSequence = conversationEntity.hideSequence,
                name = conversationEntity.uid,
            )
            conversationInfoList.add(conversationInfo)
//                }
//            } else {
//                val userInfo = conversationUserInfoMap[conversationEntity.chatId]
//
//                val conversationInfo = TmConversation(
//                    id = conversationEntity.id ?: 0,
//                    chatId = conversationEntity.chatId,
//                    uid = conversationEntity.uid,
//                    lastMid = conversationEntity.lastMid,
//                    topTimestamp = conversationEntity.topTime ?: 0,
//                    timestamp = conversationEntity.timeStamp ?: 0,
//                    lastTmMessage = lastMessage,
//                    isMute = conversationEntity.isMute,
//                    isMuteShow = true,
//                    headIndex = conversationEntity.lastMessageIndex,
//                    hideSequence = conversationEntity.hideSequence,
//                    name = userInfo?.getShowName() ?: conversationEntity.chatId,
//                )
//                conversationInfoList.add(conversationInfo)
//            }
        }
        return conversationInfoList
    }


    private fun loadConversationCombinationList(conversationList: MutableList<ConversationModel>?): MutableList<TmConversation> {
        val chatIds = conversationList
            ?.map { tmConversationInfo ->
                tmConversationInfo.chatId
            }
            ?.toMutableSet()

        val conversations = loadConversationListCommon(conversationList)

//        val draftMessagesMap =
//            MessageDraftManager.getInstance().getDraftMessageMap(chatIds = chatIds?.toMutableList())

        return conversations.map { tmConversationInfo ->
//            val draftMessage = draftMessagesMap?.getOrDefault(tmConversationInfo.chatId, null)
//
//            val conTime = tmConversationInfo.timestamp
//            val draftTime = draftMessage?.displayTime ?: 0
//            val timeStamp = if (draftTime > conTime) draftTime else conTime

//            tmConversationInfo.draftTmMessage = draftMessage
            tmConversationInfo.timestamp = tmConversationInfo.timestamp


            tmConversationInfo
        }.sortedByDescending { it.timestamp }.toMutableList()
    }

    private fun loadConversationListCommon(conversationList: MutableList<ConversationModel>?): MutableList<TmConversation> {
        val lastMids = conversationList
            ?.map { tmConversationInfo ->
                tmConversationInfo.lastMid ?: ""
            }
            ?.toMutableSet()


        val lastTmMessageMap = TmMessageLogic.INSTANCE.queryTmMessageMapByMids(lastMids)


//        val conversationUserInfoMap =
//            getConversationUserInfoMap(conversationList)
//
        val conversationInfoList = mutableListOf<TmConversation>()

        conversationList?.forEach { conversationEntity ->

            val lastMessage = lastTmMessageMap?.get(conversationEntity.lastMid ?: "")


//            if (ChatId.createById(conversationEntity.chatId).isSingle()) {
//                val userInfo = conversationUserInfoMap[conversationEntity.uid]
//                if ((userInfo?.isFriend == UserRelationChangeLogic.FRIEND_SHIP_BOTH_FRIEND
//                            || userInfo?.isFriend == UserRelationChangeLogic.FRIEND_SHIP_SINGLE_DELETE_YOU)
//                    && !RelationShipManager.isBlockUser(userInfo.blockStatus ?: 0)
//                ) {
            val conversationInfo = TmConversation(
                id = 0,
                chatId = conversationEntity.chatId,
                aChatId = conversationEntity.aChatId,
                uid = conversationEntity.uid,
                lastMid = conversationEntity.lastMid,
                timestamp = conversationEntity.timeStamp ?: 0,
                topTimestamp = conversationEntity.topTime ?: 0,
                lastTmMessage = lastMessage,
                isMute = conversationEntity.isMute,
                isMuteShow = false,
                headIndex = conversationEntity.lastMessageIndex,
                hideSequence = conversationEntity.hideSequence,
//                        name = userInfo.getShowName() ?: conversationEntity.uid,
//                        avatarInfo = userInfo.avatarInfo,
            )
            conversationInfoList.add(conversationInfo)
//                }
//            } else {
//                val userInfo = conversationUserInfoMap[conversationEntity.chatId]
//
//                val conversationInfo = TmConversationInfo(
//                    id = conversationEntity.id ?: 0,
//                    chatId = conversationEntity.chatId,
//                    uid = conversationEntity.uid,
//                    lastMid = conversationEntity.lastMid,
//                    timestamp = conversationEntity.timeStamp ?: 0,
//                    topTimestamp = conversationEntity.topTime ?: 0,
//                    lastTmMessage = lastMessage,
//                    isMute = conversationEntity.isMute,
//                    isMuteShow = true,
//                    headIndex = conversationEntity.lastMessageIndex,
//                    hideSequence = conversationEntity.hideSequence,
//                    name = conversationEntity.name ?: conversationEntity.chatId,
//                    avatarInfo = userInfo?.avatarInfo
//                )
//                conversationInfoList.add(conversationInfo)
//            }
        }

        return conversationInfoList.toMutableList()
    }


    private fun loadConversations(timeStamp: Long, count: Int): MutableList<TmConversation> {
        val conversationList =
            ConversationDbManager.INSTANCE.loadMoreConversations(timeStamp, count)

        return loadConversationCombinationList(conversationList)
    }

//    fun loadConversationListByCache(timeStamp: Long, count: Int): MutableList<TmConversation> {
//        val cachedConversations = TmConversationApiManager.getInstance().getCachedConversations()
//        if (timeStamp == Long.MAX_VALUE && cachedConversations.isNotEmpty()) {
//            return cachedConversations
//        }
//        return TmConversationApiManager.getInstance().loadConversationList(timeStamp, count)
//    }

    fun loadConversationList(
        timeStamp: Long,
        count: Int
    ): MutableList<TmmConversationVo> {
        val tmConversationList = loadConversations(timeStamp, count)

        if (tmConversationList.isEmpty()) return mutableListOf()

        val chatIds = tmConversationList
            .map { tmConversationInfo ->
                tmConversationInfo.chatId
            }
            .toMutableSet()
//        val maxMessageList = TmMessageLogic.INSTANCE
//            .queryMaxMessageIndexByChatId(chatIds)
//            ?.associateBy({ it.chatId }, { it.id })
//
//        val maxSequenceMessageList = MessageManager.getInstance()
//            .queryMaxMessageSequenceByChatId(chatIds)
//            ?.associateBy({ it.chatId }, { it.sequence })
        val result = tmConversationList.transform()
//            .filter { tmConversationInfo ->
//                val messageIndex = maxMessageList?.get(tmConversationInfo.chatId) ?: 0
//                val maxSequence = maxSequenceMessageList?.get(tmConversationInfo.chatId) ?: 0
//                !ConversationManager.conversationIsRealHide(
//                    chatId = tmConversationInfo.chatId,
//                    messageIndex = messageIndex,
//                    messageMaxSequence = maxSequence,
//                    hideIndex = tmConversationInfo.headIndex,
//                    hideSequence = tmConversationInfo.hideSequence
//                )
//            }
//            .toMutableList().transform()
        if (result.isEmpty()) {
            return loadConversationList(
                tmConversationList.elementAtOrNull(tmConversationList.lastIndex)?.timestamp
                    ?: Long.MAX_VALUE, count
            )
        }
        return result
    }

}