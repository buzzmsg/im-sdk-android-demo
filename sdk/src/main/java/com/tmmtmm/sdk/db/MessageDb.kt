package com.tmmtmm.sdk.db

import androidx.lifecycle.LifecycleOwner
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tmmtmm.sdk.core.event.EventCenter
import com.tmmtmm.sdk.constant.MessageContentType
import com.tmmtmm.sdk.constant.MessageDeleteStatus
import com.tmmtmm.sdk.constant.MessageReadStatus
import com.tmmtmm.sdk.core.db.DataBaseManager
import com.tmmtmm.sdk.db.event.ConversationEvent
import com.tmmtmm.sdk.db.event.MessageEvent
import com.tmmtmm.sdk.db.model.MessageModel

/**
 * @description
 * @version
 */
class MessageDb private constructor(){

    companion object {
        val INSTANCE: MessageDb by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MessageDb()
        }
    }

    fun addMessageCallback(
        lifecycleOwner: LifecycleOwner?,
        callback: MessageEvent.MessageListener
    ) =
        EventCenter.handle<MessageEvent>(lifecycleOwner)
            .addCallback(MessageEvent().observe(callback))

    fun removeMessageCallback(
        lifecycleOwner: LifecycleOwner?,
    ) =
        EventCenter.handle<MessageEvent>(lifecycleOwner)
            .removeCallback()

    fun updateStatus(message: MessageModel, status: Int, sendEvent: Boolean = true) {
        message.status = status
        //update message status to sending
        DataBaseManager.getInstance().getDataBase()
            ?.messageDao()?.updateStatus(message.mid, status)

        //update message send time
        DataBaseManager.getInstance().getDataBase()
            ?.messageDao()
            ?.updateCreateTime(message.mid, message.crateTime ?: System.currentTimeMillis())

        val lastMessage = DataBaseManager.getInstance().getDataBase()
            ?.messageDao()
            ?.queryMessageLimit(message.id, message.chatId)

        val displayTime = if (lastMessage != null) {
            if ((message.crateTime
                    ?: 0) > (lastMessage.displayTime ?: 0)
            ) {
                message.crateTime
            } else {
                lastMessage.displayTime
            }
        } else {
            message.crateTime
        }

        //update displayTime
        DataBaseManager.getInstance().getDataBase()
            ?.messageDao()
            ?.updateDisplayTime(message.mid, displayTime ?: System.currentTimeMillis())

        //send event
        if (message.type != MessageContentType.ContentType_Read_Receipt) {
            DataBaseManager.getInstance().getDataBase()?.conversationDao()
                ?.updateConversationTimeStamp(
                    message.chatId,
                    displayTime ?: System.currentTimeMillis()
                )
            if (sendEvent){
                MessageEvent.send(mutableSetOf(message.mid), message.chatId)
                ConversationEvent.send(mutableSetOf(message.chatId))
            }
        }
    }

}

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertMessage(messageModel: MessageModel): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertMessages(messageEntities: List<MessageModel>?)

    @Query("SELECT count(id) from tmm_message")
    fun getMessageCount(): Long

//    @Query("SELECT * FROM tmm_message where id >= :lastId ORDER BY id LIMIT :count")
//    fun loadMoreMessagesFromStart(
//        lastId: Long,
//        count: Int
//    ): MutableList<MessageModel>?

    @Query("SELECT * FROM tmm_message where id < :lastId and chatId = :chatId and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ORDER BY id Desc LIMIT :count")
    fun loadMoreMessages(
        chatId: String?,
        lastId: Long,
        count: Int
    ): MutableList<MessageModel>?

    @Query("SELECT id FROM tmm_message order by id desc limit 1")
    fun queryMessageMaxIndex(): Long?

    @Query("SELECT id FROM tmm_message where sequence == :sequence")
    fun queryMessageIndexBySequence(sequence: Long?): Long?

    @Query("SELECT sequence FROM tmm_message where id == :id")
    fun queryMessageSequenceByIndex(id: Long?): Long?

    @Query("update tmm_message set delStatus = ${MessageDeleteStatus.IS_DEL} where mid in (:mids)")
    fun deleteMessagesByMids(mids: MutableSet<String>?)

    @Query("update tmm_message set delStatus = ${MessageDeleteStatus.IS_DEL} where chatId == :chatId and mid not in (:mids)")
    fun deleteMessagesByChatId(chatId: String?, mids: MutableList<String>?)

    @Query("update tmm_message set delStatus = ${MessageDeleteStatus.IS_DEL} where chatId == :chatId and sender == :uid and mid not in (:mids)")
    fun deleteMyMessagesByChatId(chatId: String?, uid: String?, mids: MutableList<String>?)

    @Query("SELECT * FROM tmm_message where delStatus != ${MessageDeleteStatus.IS_DEL} ORDER BY sequence DESC LIMIT 1")
    fun queryLocalSequence(): List<MessageModel>

    @Query("SELECT * FROM tmm_message where chatId == :chatId and type != ${MessageContentType.ContentType_Read_Receipt} ORDER BY sequence DESC LIMIT 1")
    fun queryLocalChatSequence(chatId: String?): MessageModel

    @Query("SELECT mid FROM tmm_message where mid in (:mids)")
    fun existMids(mids: MutableList<String>): MutableList<String>

    @Query("update tmm_message set status = :status Where mid = :mid and type != ${MessageContentType.ContentType_Read_Receipt}")
    fun updateStatus(mid: String, status: Int)

    @Query("update tmm_message set status = :status Where mid in (:mids) and type != ${MessageContentType.ContentType_Read_Receipt}")
    fun updateMessageStatusByMids(status: Int, mids: MutableSet<String>?)

    @Query("update tmm_message set crateTime = :createTime Where mid = :mid and type != ${MessageContentType.ContentType_Read_Receipt}")
    fun updateCreateTime(mid: String, createTime: Long)

    @Query("update tmm_message set displayTime = :displayTime Where mid = :mid and type != ${MessageContentType.ContentType_Read_Receipt}")
    fun updateDisplayTime(mid: String, displayTime: Long)

    @Query("update tmm_message set sequence = :sequence Where mid = :mid")
    fun updateSequence(mid: String, sequence: Long?)

    @Query("SELECT * FROM tmm_message where id < :lastId and chatId = :chatId and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ORDER BY id Desc LIMIT :count")
    fun loadMoreMessagesFromTop(
        chatId: String?,
        lastId: Long,
        count: Int
    ): MutableList<MessageModel>?

    @Query("SELECT * from tmm_message where status = :status and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
    fun queryMessagesByStatus(status: Int): MutableList<MessageModel>

    @Query("SELECT * FROM tmm_message where mid in (:mids) and type != ${MessageContentType.ContentType_Read_Receipt} ")
    fun queryMessagesByMidsIncludeDel(mids: MutableList<String>?): MutableList<MessageModel>

    @Query("SELECT * FROM tmm_message where mid in (:mids) and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
    fun queryMessagesByMids(mids: MutableSet<String>?): MutableList<MessageModel>

    @Query("SELECT * FROM tmm_message where mid = :mid and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
    fun queryMessagesByMid(mid: String?): MessageModel?

    @Query("SELECT id FROM tmm_message where mid = :mid and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
    fun queryMessageIndexByMid(mid: String?): Long?

    @Query("SELECT * FROM tmm_message where chatId == :chatId and sender == :uid and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
    fun queryMyMessagesByChatId(chatId: String?, uid: String?): MutableList<MessageModel>

    @Query("SELECT * FROM tmm_message where chatId == :chatId and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
    fun queryAllMessagesByChatId(chatId: String?): MutableList<MessageModel>

    @Query("SELECT * from tmm_message where readStatus = ${MessageReadStatus.NOT_READ} and delStatus != ${MessageDeleteStatus.IS_DEL}")
    fun queryMessagesByUnRead(): MutableList<MessageModel>?

    @Query("SELECT * from tmm_message where id <:lastId and chatId = :chatId and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt}  ORDER BY id desc limit 1 ")
    fun queryMessageLimit(lastId: Long?, chatId: String?): MessageModel?

    @Query("SELECT * from tmm_message where chatId in (:chatIds) and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt}  ORDER BY id desc limit 1 ")
    fun queryMessagesLimit(chatIds: MutableSet<String>?): MutableList<MessageModel>?

    @Query("SELECT * from tmm_message where readStatus = ${MessageReadStatus.NOT_READ} and chatId = :chatId and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
    fun queryUnReadConversationMessagesByChatId(chatId: String?): MutableList<MessageModel>?

    @Query("SELECT * from tmm_message where readStatus = ${MessageReadStatus.NOT_READ} and chatId in (:chatIds) and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
    fun queryUnReadConversationMessages(chatIds: MutableList<String>): MutableList<MessageModel>?

//    @Query("SELECT count(*) from tmm_message where readStatus = ${MessageReadStatus.NOT_READ} and chatId = :chatId and atType in (:atTypes) and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
//    fun queryAtMessagesByChatId(chatId: String?, atTypes: MutableList<Int>): Int?

    @Query("SELECT * from tmm_message where readStatus = ${MessageReadStatus.NOT_READ} and chatId in (:chatIds) and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
    fun queryUnReadConversationMessagesByChatIds(chatIds: MutableSet<String>?): MutableList<MessageModel>?

    @Query("update tmm_message set readStatus = :readStatus Where chatId = :chatId and type != ${MessageContentType.ContentType_Read_Receipt}")
    fun updateMessageReadByChatId(chatId: String, readStatus: Int)

    @Query("update tmm_message set content = :content Where mid = :mid")
    fun updateMessageContent(mid: String?, content: String?)

    @Query("update tmm_message set readStatus = ${MessageReadStatus.IS_READ} Where mid in (:mids) and type != ${MessageContentType.ContentType_Read_Receipt}")
    fun updateMessageIsReadByMids(mids: MutableSet<String>?)

    @Query("update tmm_message set delStatus = ${MessageDeleteStatus.IS_DEL} where chatId in (:chatIds)")
    fun clearMessagesByChatIds(chatIds: MutableList<String>?)

    @Query("SELECT * from tmm_message where chatId in (:chatIds) and delStatus != ${MessageDeleteStatus.IS_DEL}  and type != ${MessageContentType.ContentType_Read_Receipt} group by chatId having id = max(id)")
    fun queryMessageIndexByChatIds(chatIds: MutableSet<String>?): MutableList<MessageModel>?

    @Query("SELECT * from tmm_message where chatId in (:chatIds) and type != ${MessageContentType.ContentType_Read_Receipt} group by chatId having sequence = max(sequence)")
    fun queryMessageSequenceByChatIds(chatIds: MutableSet<String>?): MutableList<MessageModel>?

    @Query("SELECT * from tmm_message where chatId in (:chatIds) and delStatus != ${MessageDeleteStatus.IS_DEL} and type in (:types) ORDER BY id desc")
    fun queryMessagesByType(chatIds: String?, types: MutableList<Int>): MutableList<MessageModel>?

    @Query("SELECT mid from tmm_message where mid not in (:mids) and chatId = :chatId and delStatus != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt}  ORDER BY id desc limit 1 ")
    fun queryMaxMessageMidByChatId(chatId: String?, mids: MutableList<String>?): String?

}