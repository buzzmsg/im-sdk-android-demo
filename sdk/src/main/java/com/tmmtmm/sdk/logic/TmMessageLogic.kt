package com.tmmtmm.sdk.logic

import android.util.Log
import com.tmmtmm.sdk.intercept.MessageInsertInterceptor
import com.tmmtmm.sdk.api.*
import com.tmmtmm.sdk.cache.MessageCache
import com.tmmtmm.sdk.constant.MessageContentType
import com.tmmtmm.sdk.constant.MessageDeleteStatus.IS_DEL
import com.tmmtmm.sdk.constant.MessageDeleteStatus.NOT_DEL
import com.tmmtmm.sdk.constant.MessageReadStatus
import com.tmmtmm.sdk.constant.MessageStatus
import com.tmmtmm.sdk.constant.UserConstant
import com.tmmtmm.sdk.core.db.DataBaseManager
import com.tmmtmm.sdk.core.id.MessageId
import com.tmmtmm.sdk.core.net.ResponseResult
import com.tmmtmm.sdk.db.MessageDb
import com.tmmtmm.sdk.db.event.ConversationEvent
import com.tmmtmm.sdk.db.event.MessageEvent
import com.tmmtmm.sdk.db.model.MessageModel
import com.tmmtmm.sdk.dto.TmMessage
import com.tmmtmm.sdk.intercept.MessageInterceptorChain
import com.tmmtmm.sdk.message.content.TmTextMessageContent
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
        sendMessage(tmMessage, groupId = chatId ?: "")
//        if (!ChatId.createById(chatId ?: "").isSingle()) {
//
//        } else {
//            sendMessage(tmMessage, ChatId.createById(chatId ?: "").getTargetId() ?: "")
//        }
    }


    fun sendMessage(message: TmMessage,groupId: String = "", mid: String = "") {
//        delDraftMessage(uid, groupId, mid)

        val messageEntity = createMessageEntity(message = message,groupId =  groupId, mid)
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
        groupId: String,
        mid: String = ""
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
        messageEntity.chatId = groupId
        messageEntity.mid = messageId
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
}