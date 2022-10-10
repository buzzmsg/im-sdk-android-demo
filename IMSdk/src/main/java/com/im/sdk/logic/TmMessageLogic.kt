package com.im.sdk.logic

import android.util.Log
import com.im.sdk.api.*
import com.im.sdk.cache.MessageCache
import com.im.sdk.constant.MessageContentType
import com.im.sdk.constant.MessageDeleteStatus.IS_DEL
import com.im.sdk.constant.MessageDeleteStatus.NOT_DEL
import com.im.sdk.constant.MessageReadStatus
import com.im.sdk.constant.MessageStatus
import com.im.sdk.constant.UserConstant
import com.im.sdk.core.db.DataBaseManager
import com.im.sdk.core.id.MessageId
import com.im.sdk.core.net.ResponseResult
import com.im.sdk.db.MessageDb
import com.im.sdk.db.event.ConversationEvent
import com.im.sdk.db.event.MessageEvent
import com.im.sdk.db.model.MessageModel
import com.im.sdk.dto.TmMessage
import com.im.sdk.intercept.MessageInsertInterceptor
import com.im.sdk.intercept.MessageInterceptorChain
import com.im.sdk.message.content.TmTextMessageContent
import com.im.sdk.ui.view.vo.TmmMessageVo
import kotlinx.coroutines.sync.Mutex

/**
 * @description
 * @version
 */
class TmMessageLogic private constructor() {

    private val mutex = Mutex()

    companion object {

        private const val SEQUENCE_CHUNK_SIZE = 1000

        val INSTANCE: TmMessageLogic by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            TmMessageLogic()
        }
    }

    fun receiveMessage(sequence: Long? = null): ResponseResult<Any> {
        val maxSequence = MessageCache.getMessageMaxSequence()
        if (sequence != null && sequence < maxSequence) {
            return ResponseResult.Success(Any())
        }


        val sequenceResult = GetMessageIds.fetch(SequenceListRequest(maxSequence))
        if (sequenceResult !is ResponseResult.Success<MessageSequenceResponse?>) {
            return ResponseResult.Failure((sequenceResult as ResponseResult.Failure).throwable)
        }

        val sequenceResponse = sequenceResult.value
        val sequenceItems = sequenceResponse?.items?.sortedBy { it.sequence }


        val sequenceSets = mutableSetOf<String>()
        val sequenceMap = hashMapOf<String, MessageSequenceItemResponse>()
        for (messageSequenceDto in sequenceItems ?: mutableListOf()) {
            sequenceSets.add(messageSequenceDto.mid ?: "")
            sequenceMap[messageSequenceDto.mid ?: ""] = messageSequenceDto
        }

        val diffMidsList = diffMids(sequenceSets.toMutableList())

        if (diffMidsList.isEmpty()) {
            return ResponseResult.Success(Any())
        }

        val midsList = diffMidsList.chunked(SEQUENCE_CHUNK_SIZE)


        for (diffMids in midsList) {
            val messageInfoResponse = GetMessageInfo.load(diffMids)

            if (messageInfoResponse !is ResponseResult.Success<MessageInfoResponse?>) {
                return ResponseResult.Failure((messageInfoResponse as ResponseResult.Failure).throwable)
            }

            val messageInfoItems = messageInfoResponse.value?.items
            if (messageInfoItems == null && messageInfoItems?.size == 0) {
                return ResponseResult.Success(Any())
            }
            var messageList = messageInfoItems?.map { messageInfoResponseItem ->
                val toMessageEntity = messageInfoResponseItem.toMessageEntity()
                if (toMessageEntity.sender == TmLoginLogic.getInstance()
                        .getUserId() || toMessageEntity.sender == UserConstant.THIRD_USER_ID
                ) {
                    toMessageEntity.readStatus = MessageReadStatus.IS_READ
                }
                toMessageEntity.sequence = sequenceMap[toMessageEntity.mid]?.sequence ?: 0
                toMessageEntity.readStatus = sequenceMap[toMessageEntity.mid]?.isRead ?: 0
                val messageInfoStatus = messageInfoResponseItem.status
                val messageSequenceStatus = sequenceMap[toMessageEntity.mid]?.status ?: 0
                val isDelete =
                    if (messageInfoStatus == IS_DEL || messageSequenceStatus == IS_DEL) {
                        IS_DEL
                    } else {
                        NOT_DEL
                    }
                toMessageEntity.delStatus = isDelete
                toMessageEntity
            }?.toMutableList()

            messageList = messageList?.sortedBy {
                it.sequence
            }?.toMutableList()

            if (messageList.isNullOrEmpty()) {
                return ResponseResult.Success(Any())
            }

            val needInsertMessages = insert(messageList)

            val availableMessages =
                needInsertMessages?.filter {
                    it.type != MessageContentType.ContentType_Read_Receipt
                            && it.type != MessageContentType.ContentType_Delete
                }
                    ?.toMutableList() ?: mutableListOf()

            val messageMap =
                availableMessages.groupBy(
                    keySelector = { it.chatId },
                    valueTransform = { it })

            //send message event
            val chatIds = mutableSetOf<String>()
            messageMap.forEach {
                val mids = mutableSetOf<String>()
                for (messageEntity in it.value) {
                    mids.add(messageEntity.mid)
                }

                MessageEvent.send(mids, it.key)
                chatIds.add(it.key)
            }
            TmConversationLogic.INSTANCE.receiveConversation(availableMessages, chatIds)
//            MessageHelper.handleMessages(messageList)
        }

        return ResponseResult.Success(Any())
    }

    fun insert(message: MessageModel): Long? {
        message.displayTime = message.crateTime
//        val result = try {
//            DataBaseManager.getInstance().getDataBase()
//                ?.messageDao()?.insertMessage(message)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            0
//        }
        val result = try {
//            FtsDataMigrationLogic.INSTANCE.writeMessages(mutableListOf(message))
            DataBaseManager.getInstance().getDataBase()
                ?.messageDao()?.insertMessage(message)

        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            Log.w("OnConflictButOK", "object: ${message.mid} " + e.toString())
            0
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("MessageInsertOnConflict", e.toString())
            0
        }
        return result
    }

    fun insert(messageList: MutableList<MessageModel>?): MutableList<MessageModel>? {
        val chatIds = mutableSetOf<String>()
        messageList?.forEach {
            chatIds.add(it.chatId)
        }

        if (chatIds.isEmpty()) {
            return messageList
        }

        val lastMessages = DataBaseManager.getInstance().splitArray(chatIds.toMutableList()) {
            DataBaseManager.getInstance().getDataBase()
                ?.messageDao()?.queryMessagesLimit(it.toMutableSet()) ?: mutableListOf()
        }

        val lastMessagesMap = lastMessages.associateBy({ it.chatId }, { it }).toMutableMap()

        messageList?.forEach { messageEntity ->

            val lastMessage = lastMessagesMap[messageEntity.chatId]
            val displayTime = if (lastMessage != null) {
                if ((messageEntity.crateTime
                        ?: 0) > (lastMessage.displayTime ?: 0)
                ) {
                    messageEntity.crateTime
                } else {
                    lastMessage.displayTime
                }
            } else {
                messageEntity.crateTime
            }
            messageEntity.displayTime = displayTime
            lastMessagesMap[messageEntity.chatId] = messageEntity
        }

        val maxSequence = messageList?.maxByOrNull { messageEntity: MessageModel ->
            messageEntity.sequence ?: 0L
        }?.sequence ?: 0L

        if (maxSequence != 0L) {
            MessageCache.saveMessageMaxSequence(maxSequence)
        }

        if (messageList.isNullOrEmpty()) return messageList


        //insert message intercept(as: AtMessage)
        val interceptors = mutableListOf<MessageInsertInterceptor>()
//        interceptors.add(TmAtMessageInsertInterceptor())

        val chain = MessageInterceptorChain(interceptors, 0, messageList)

        val responseResult = chain.proceed(messageList)

        if (responseResult is ResponseResult.Success) {
            DataBaseManager.getInstance().getDataBase()
                ?.messageDao()
                ?.insertMessages(responseResult.value)
//            FtsDataMigrationLogic.INSTANCE.writeMessages(responseResult.value)
        }
        return messageList
    }

    fun diffMids(remoteMids: MutableList<String>): MutableList<String> {
        val existMids = DataBaseManager.getInstance().splitArray(remoteMids) { value ->
            DataBaseManager.getInstance().getDataBase()
                ?.messageDao()
                ?.existMids(value) ?: mutableListOf()

        }
        val diffList = mutableListOf<String>()
        for (mid in remoteMids) {
            if (!existMids.contains(mid)) {
                diffList.add(mid)
            }
        }
        return diffList
    }


    fun queryMessagesByMid(mid: String?): TmMessage {
        val messageEntity = DataBaseManager.getInstance().getDataBase()
            ?.messageDao()?.queryMessagesByMid(mid)
        return MessageContentLogic.getInstance().convertToTmMessage(messageEntity)
    }

    fun sendTextMessage(
        content: String,
        chatId: String?,
        aChatId: String?,
        amid: String,
        mids: MutableList<String>? = null
    ) {
        val tmMessage = TmMessage.create()
//        if (!mids.isNullOrEmpty()) {
//            val extra = TmMessageExtra(
//                mids = mids,
//                act = TmMessage.MESSAGE_ACT_REFERENCE,
//                op = OpCodeUtil.getOpCodeString(),
//                ids = mids
//            )
//            tmMessage.extra = extra
//        }
        //create message
        tmMessage.content =
            TmTextMessageContent(content)
        sendMessage(tmMessage, chatId = chatId ?: "", aChatId = aChatId ?: "", amid = amid)
//        if (!ChatId.createById(chatId ?: "").isSingle()) {
//
//        } else {
//            sendMessage(tmMessage, ChatId.createById(chatId ?: "").getTargetId() ?: "")
//        }
    }


    fun sendExistMessage(messageEntity: MessageModel) {
        val result = SendMessage.send(messageEntity)
        if (result is ResponseResult.Success) {
            messageEntity.crateTime = System.currentTimeMillis()
            MessageDb.INSTANCE
                .updateStatus(messageEntity, MessageStatus.Sent.value(), false)
        } else if (result is ResponseResult.Failure) {

        }
    }

    fun sendMessage(
        message: TmMessage,
        chatId: String = "",
        aChatId: String = "",
        mid: String = "",
        amid: String = ""
    ) {
//        delDraftMessage(uid, groupId, mid)

        val messageEntity =
            createMessageEntity(message = message, chatId = chatId, aChatId = aChatId, mid, amid = amid)
        TmConversationLogic.INSTANCE.insertOrUpdateConversation(messageEntity)

        //send event
        MessageEvent.send(mutableSetOf(messageEntity.mid), messageEntity.chatId)

        ConversationEvent.send(mutableSetOf(messageEntity.chatId))

        val result = SendMessage.send(messageEntity)
        if (result is ResponseResult.Success) {
            messageEntity.crateTime = System.currentTimeMillis()
            MessageDb.INSTANCE
                .updateStatus(messageEntity, MessageStatus.Sent.value())
            Log.w("sendMessage - success", "$messageEntity")
        } else if (result is ResponseResult.Failure) {

        }
    }

    //create message
    fun createMessageEntity(
        message: TmMessage,
        chatId: String,
        aChatId: String,
        mid: String = "",
        amid: String = ""
    ): MessageModel {
        val mUid = TmLoginLogic.getInstance().getUserId()
        val messageEntity: MessageModel = MessageContentLogic.getInstance().transform(message)
        val messageId: String = mid.ifBlank {
            MessageId.create(mUid)
        }

//        val chatId = if (TextUtils.isEmpty(groupId)) {
//            ChatId.createSingle(mUid, uid).encode()
//        } else {
//            groupId
//        }

        //create message
        messageEntity.status = MessageStatus.Sending.value()
        messageEntity.sender = mUid
        messageEntity.chatId = chatId
        messageEntity.aChatId = aChatId
        messageEntity.mid = messageId

        messageEntity.amid = amid
//        if (message.extra != null) {
//            message.extra?.from = MessageFromConstant.FROM_ANDROID
//            messageEntity.extra = message.extra?.toJson().toString()
//        } else {
//            val messageExtra = TmMessageExtra()
//            messageExtra.from = MessageFromConstant.FROM_ANDROID
//            messageEntity.extra = message.extra?.toJson().toString()
//        }
        messageEntity.type = message.content?.getMessageContentType()
        messageEntity.crateTime = System.currentTimeMillis()
        messageEntity.sendTime = System.currentTimeMillis()
        messageEntity.readStatus = MessageReadStatus.IS_READ
//        messageEntity.isLocalSend = true
//        messageEntity.isBrowse = MessageBrowseConstant.ALREADY_BROWSED

        //save message,status=sending
        val id = insert(messageEntity)
        messageEntity.id = id ?: 0
        return messageEntity
    }


    fun retrySendMessages() {
        val sendingMessages =
            queryMessagesByStatus(MessageStatus.Sending.value())

//        val needUploadMessage = sendingMessages?.filter {
//            it.type == MessageContentType.ContentType_Image
//                    || it.type == MessageContentType.ContentType_File
//                    || it.type == MessageContentType.ContentType_Video
//                    || it.type == MessageContentType.ContentType_Voice
//        }?.toMutableList()

        val retryMessages =
            sendingMessages?.filter {
                it.type != MessageContentType.ContentType_Image
                        && it.type != MessageContentType.ContentType_File
                        && it.type != MessageContentType.ContentType_Video
                        && it.type != MessageContentType.ContentType_Voice
            }
                ?.toMutableList()

        if (retryMessages.isNullOrEmpty()) return

        val chatIds = retryMessages.map { it.chatId }.toMutableSet()

        retryMessages.forEach { messageEntity ->
            insert(messageEntity)
            sendExistMessage(messageEntity)

            MessageEvent.send(mutableSetOf(messageEntity.mid), messageEntity.chatId)
        }

        ConversationEvent.send(chatIds)
//        FileProgressManager.getInstance().retryUploadFiles(needUploadMessage)
    }

    fun queryMessagesByStatus(status: Int): MutableList<MessageModel>? {
        return DataBaseManager.getInstance().getDataBase()
            ?.messageDao()
            ?.queryMessagesByStatus(status)
    }

//    fun queryMaxMessageIndexByChatId(chatId: MutableSet<String>?): MutableList<MessageModel> {
//        return DataBaseManager.getInstance()
//            .splitArray(chatId?.toMutableList() ?: mutableListOf()) {
//                DataBaseManager.getInstance().getDataBase()
//                    ?.messageDao()
//                    ?.queryMessageIndexByChatIds(it.toMutableSet()) ?: mutableListOf()
//            }
//    }
//
//    fun queryMaxMessageSequenceByChatId(chatId: MutableSet<String>?): MutableList<MessageModel> {
//        return DataBaseManager.getInstance()
//            .splitArray(chatId?.toMutableList() ?: mutableListOf()) {
//                DataBaseManager.getInstance().getDataBase()
//                    ?.messageDao()
//                    ?.queryMessageSequenceByChatIds(it.toMutableSet()) ?: mutableListOf()
//            }
//    }

    fun queryTmMessageMapByMids(mids: MutableSet<String>?): Map<String, TmMessage>? {
        if (mids.isNullOrEmpty()) {
            return null
        }
        val messageEntities =
            DataBaseManager.getInstance().splitArray(mids.toMutableList()) { value ->
                DataBaseManager.getInstance().getDataBase()
                    ?.messageDao()?.queryMessagesByMids(value.toMutableSet()) ?: mutableListOf()
            }
        val tmMessages = messageEntities.map { messageEntity ->
            MessageContentLogic.getInstance().convertToTmMessage(messageEntity)
        }.toMutableList()
        return tmMessages.associateBy({ it.mid }, { it })
    }

    fun getUnreadCount(chatIds: MutableList<String>?): Map<String, Int?> {
        val mChatIds = chatIds ?: mutableListOf()
        val messageList = DataBaseManager.getInstance()
            .splitArray(mChatIds) { ids ->
                DataBaseManager.getInstance().getDataBase()
                    ?.messageDao()
                    ?.queryUnReadConversationMessages(ids) ?: mutableListOf()
            }.filter {
                it.sender != UserConstant.THIRD_USER_ID
            }

        return messageList.groupBy { it.chatId }.mapValues { it.value.size }

    }


    fun loadMessage(
        lastMessageId: Long,
        chatId: String?,
    ): MutableList<TmmMessageVo> {
        val messageList = loadMessage(
            chatId = chatId, lastId = lastMessageId, messageCount = 20
        )
        return handleMessages(chatId, messageList)
    }

    fun loadMoreMessages(
        lastMessageId: Long,
        chatId: String?
    ): MutableList<TmmMessageVo> {
        val messageList = loadMoreMessages(
            chatId = chatId, lastId = lastMessageId, messageCount = 20
        )

        return handleMessages(chatId, messageList)
    }

    fun handleMessages(
        chatId: String?,
        messageList: MutableList<TmMessage>
    ): MutableList<TmmMessageVo> {
//        val quoteMessageVoMap = hashMapOf<String, TmmMessageVo>()
//
//        val atTmMessageVoList = mutableListOf<AtMessageContentItemVo>()
//        val quoteMids = mutableSetOf<String>()
//
//        val tmmMessageVoList = mutableListOf<TmmMessageVo>()
//        messageList.forEachIndexed { index, tmMessage ->
//            val tmmMessageVo = tmMessage.transformToTmmMessage()
//            val quoteMid = tmMessage.extra?.mids?.elementAtOrNull(0) ?: ""
//            if (!CollectionUtils.isEmpty(tmMessage.extra?.mids)) {
//                quoteMids.add(quoteMid)
//                quoteMessageVoMap[quoteMid] =
//                    TmmMessageVo(messageId = 0, mid = quoteMid, uid = "", messageBody = "")
//            }
//
//            if (tmMessage.type == TmMessageContentType.ContentType_At) {
//                val atMessageContentItemVo = AtMessageContentItemVo()
//                atMessageContentItemVo.mid = tmMessage.mid
//                atMessageContentItemVo.items = tmmMessageVo.tmmAtMessageContent?.items
//                atTmMessageVoList.add(atMessageContentItemVo)
//            }
//        }
//
//        val quoteMessageMap =
//            MessageManager.getInstance().queryTmMessageMapByMids(quoteMids) ?: hashMapOf()
//
//        quoteMessageMap.values.toMutableList().filter {
//            it.type == TmMessageContentType.ContentType_At
//        }.forEach { tmMessage ->
//            val tmmMessageVo = tmMessage.transformToTmmMessage()
//            val atMessageContentItemVo = AtMessageContentItemVo()
//            atMessageContentItemVo.mid = tmMessage.mid
//            atMessageContentItemVo.items = tmmMessageVo.tmmAtMessageContent?.items
//            atTmMessageVoList.add(atMessageContentItemVo)
//        }
//
//        val atMessageMap =
//            TmAtMessageManager.getInstance().getAtMessageList(chatId, atTmMessageVoList)
//
//        for (mutableEntry in quoteMessageMap) {
//            val mid = mutableEntry.value.mid
//
//            val quoteMessageVo = quoteMessageMap[mid]?.transformToTmmMessage() ?: continue
//            if (atMessageMap.keys.contains(quoteMessageVo.mid)) {
//                quoteMessageVo.atUserList = atMessageMap[mid]
//            }
//            quoteMessageVoMap[mid] = quoteMessageVo
//        }

        val tmmMessageVoList = messageList.map { tmMessage ->
            val tmmMessageVo = MessageContentLogic.getInstance().transformToTmmMessage(tmMessage)
//            val mid = tmmMessageVo.mid
//            val quoteMid = tmMessage.extra?.mids?.elementAtOrNull(0) ?: ""
//            if (atMessageMap.keys.contains(tmMessage.mid)) {
//                tmmMessageVo.atUserList = atMessageMap[mid]
//            }
//
//            if (quoteMessageVoMap.keys.contains(quoteMid)) {
//                tmmMessageVo.quoteMessageVo?.tmmMessageVo = quoteMessageVoMap[quoteMid]
//            }

            tmmMessageVo
        }.toMutableList()

        return tmmMessageVoList
    }

    private fun loadMessage(
        chatId: String?,
        lastId: Long,
        messageCount: Int
    ): MutableList<TmMessage> {
        val moreMessageEntityList =
            DataBaseManager.getInstance().getDataBase()
                ?.messageDao()
                ?.loadMoreMessagesFromTop(chatId, lastId, messageCount)
                ?.reversed()
        val moreMessageList = mutableListOf<TmMessage>()
        moreMessageEntityList
            ?.forEach {
                val message = MessageContentLogic.getInstance().convertToTmMessage(it)
                moreMessageList.add(message)
            }
        return moreMessageList
    }

    private fun loadMoreMessages(
        chatId: String?,
        lastId: Long,
        messageCount: Int
    ): MutableList<TmMessage> {
        val moreMessageEntityList =
            DataBaseManager.getInstance().getDataBase()
                ?.messageDao()
                ?.loadMoreMessages(chatId, lastId, messageCount)
                ?.reversed()
        val moreMessageList = mutableListOf<TmMessage>()
        moreMessageEntityList
            ?.forEach {
                val message = MessageContentLogic.getInstance().convertToTmMessage(it)
                moreMessageList.add(message)
            }
        return moreMessageList
    }


    fun getMessages(chatId: String?, mids: MutableSet<String>?): MutableList<TmmMessageVo> {
        if (mids.isNullOrEmpty()) return mutableListOf()

        val messageList = queryMessagesByMids(mids)

        if (messageList.isNullOrEmpty()) return mutableListOf()

//        val quoteMessageVoMap = hashMapOf<String, TmmMessageVo>()
//
//        val atTmMessageVoList = mutableListOf<AtMessageContentItemVo>()
//        val quoteMids = mutableSetOf<String>()
//
//        val tmmMessageVoList = mutableListOf<TmmMessageVo>()
//        messageList.forEachIndexed { index, tmMessage ->
//            val tmmMessageVo = tmMessage.transformToTmmMessage()
//            val quoteMid = tmMessage.extra?.mids?.elementAtOrNull(0) ?: ""
//            if (!CollectionUtils.isEmpty(tmMessage.extra?.mids)) {
//                quoteMids.add(quoteMid)
//                quoteMessageVoMap[quoteMid] =
//                    TmmMessageVo(messageId = 0, mid = quoteMid, uid = "", messageBody = "")
//            }
//
//            if (tmMessage.type == TmMessageContentType.ContentType_At) {
//                val atMessageContentItemVo = AtMessageContentItemVo()
//                atMessageContentItemVo.mid = tmMessage.mid
//                atMessageContentItemVo.items = tmmMessageVo.tmmAtMessageContent?.items
//                atTmMessageVoList.add(atMessageContentItemVo)
//            }
//
//        }
//
//        val quoteMessageMap =
//            MessageManager.getInstance().queryTmMessageMapByMids(quoteMids) ?: hashMapOf()
//
//        quoteMessageMap.values.toMutableList().filter {
//            it.type == TmMessageContentType.ContentType_At
//        }.forEach { tmMessage ->
//            val tmmMessageVo = tmMessage.transformToTmmMessage()
//            val atMessageContentItemVo = AtMessageContentItemVo()
//            atMessageContentItemVo.mid = tmMessage.mid
//            atMessageContentItemVo.items = tmmMessageVo.tmmAtMessageContent?.items
//            atTmMessageVoList.add(atMessageContentItemVo)
//        }
//
//        val atMessageMap =
//            TmAtMessageManager.getInstance().getAtMessageList(chatId, atTmMessageVoList)
//
//        for (mutableEntry in quoteMessageMap) {
//            val mid = mutableEntry.value.mid
//
//            val quoteMessageVo = quoteMessageMap[mid]?.transformToTmmMessage() ?: continue
//            if (atMessageMap.keys.contains(quoteMessageVo.mid)) {
//                quoteMessageVo.atUserList = atMessageMap[mid]
//            }
//            quoteMessageVoMap[mid] = quoteMessageVo
//        }

        val tmmMessageVoList = messageList.map { tmMessage ->
            val tmmMessageVo = MessageContentLogic.getInstance().transformToTmmMessage(tmMessage)
//            val mid = tmmMessageVo.mid
//            val quoteMid = tmMessage.extra?.mids?.elementAtOrNull(0) ?: ""
//            if (atMessageMap.keys.contains(tmMessage.mid)) {
//                tmmMessageVo.atUserList = atMessageMap[mid]
//            }
//
//            if (quoteMessageVoMap.keys.contains(quoteMid)) {
//                tmmMessageVo.quoteMessageVo?.tmmMessageVo = quoteMessageVoMap[quoteMid]
//            }
//
//            tmmMessageVoList.add(tmmMessageVo)
            tmmMessageVo
        }.toMutableList()


        return tmmMessageVoList
    }

    private fun queryMessagesByMids(mids: MutableSet<String>?): MutableList<TmMessage>? {

        if (mids.isNullOrEmpty()) {
            return null
        }

        val messageEntities =
            DataBaseManager.getInstance().splitArray(mids.toMutableList()) { value ->
                DataBaseManager.getInstance().getDataBase()
                    ?.messageDao()
                    ?.queryMessagesByMids(value.toMutableSet()) ?: mutableListOf()

            }
//        val messageEntities = DataBaseManager.getInstance().getDataBase()
//            ?.messageDao()?.queryMessagesByMids(mids)
        return messageEntities.map { messageEntity ->
            MessageContentLogic.getInstance().convertToTmMessage(messageEntity)
        }.toMutableList()
    }
}