package com.tmmtmm.sdk.messagecore.db

import androidx.lifecycle.LifecycleOwner
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tmmtmm.sdk.core.event.EventCenter
import com.tmmtmm.sdk.messagecore.MessageContentType
import com.tmmtmm.sdk.messagecore.constant.MessageDeleteStatus
import com.tmmtmm.sdk.messagecore.constant.MessageReadStatus
import com.tmmtmm.sdk.messagecore.db.event.MessageEvent
import com.tmmtmm.sdk.messagecore.model.MessageEntity

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

    @Dao
    interface MessageDao {
        @Insert(onConflict = OnConflictStrategy.ABORT)
        fun insertMessage(messageEntity: MessageEntity): Long

        @Insert(onConflict = OnConflictStrategy.ABORT)
        fun insertMessages(messageEntities: List<MessageEntity>?)

        @Query("SELECT count(id) from tmm_message")
        fun getMessageCount(): Long

        @Query("SELECT * FROM tmm_message where id >= :lastId ORDER BY id LIMIT :count")
        fun loadMoreMessagesFromStart(
            lastId: Long,
            count: Int
        ): MutableList<MessageEntity>?

        @Query("SELECT * FROM tmm_message where id > :lastId and chatId = :chatId and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ORDER BY id LIMIT :count")
        fun loadMoreMessagesFromBottom(
            chatId: String?,
            lastId: Long,
            count: Int
        ): MutableList<MessageEntity>?

        @Query("SELECT id FROM tmm_message order by id desc limit 1")
        fun queryMessageMaxIndex(): Long?

        @Query("SELECT id FROM tmm_message where sequence == :sequence")
        fun queryMessageIndexBySequence(sequence: Long?): Long?

        @Query("SELECT sequence FROM tmm_message where id == :id")
        fun queryMessageSequenceByIndex(id: Long?): Long?

        @Query("update tmm_message set isDel = ${MessageDeleteStatus.IS_DEL} where mid in (:mids)")
        fun deleteMessagesByMids(mids: MutableSet<String>?)

        @Query("update tmm_message set isDel = ${MessageDeleteStatus.IS_DEL} where chatId == :chatId and mid not in (:mids)")
        fun deleteMessagesByChatId(chatId: String?, mids: MutableList<String>?)

        @Query("update tmm_message set isDel = ${MessageDeleteStatus.IS_DEL} where chatId == :chatId and sender == :uid and mid not in (:mids)")
        fun deleteMyMessagesByChatId(chatId: String?, uid: String?, mids: MutableList<String>?)

        @Query("SELECT * FROM tmm_message where isDel != ${MessageDeleteStatus.IS_DEL} ORDER BY sequence DESC LIMIT 1")
        fun queryLocalSequence(): List<MessageEntity>

        @Query("SELECT * FROM tmm_message where chatId == :chatId and type != ${MessageContentType.ContentType_Read_Receipt} ORDER BY sequence DESC LIMIT 1")
        fun queryLocalChatSequence(chatId: String?): MessageEntity

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

        @Query("SELECT * FROM tmm_message where id < :lastId and chatId = :chatId and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ORDER BY id Desc LIMIT :count")
        fun loadMoreMessagesFromTop(
            chatId: String?,
            lastId: Long,
            count: Int
        ): MutableList<MessageEntity>?

        @Query("SELECT * from tmm_message where status = :status and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
        fun queryMessagesByStatus(status: Int): MutableList<MessageEntity>

        @Query("SELECT * FROM tmm_message where mid in (:mids) and type != ${MessageContentType.ContentType_Read_Receipt} ")
        fun queryMessagesByMidsIncludeDel(mids: MutableList<String>?): MutableList<MessageEntity>

        @Query("SELECT * FROM tmm_message where mid in (:mids) and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
        fun queryMessagesByMids(mids: MutableSet<String>?): MutableList<MessageEntity>

        @Query("SELECT * FROM tmm_message where mid = :mid and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
        fun queryMessagesByMid(mid: String?): MessageEntity?

        @Query("SELECT id FROM tmm_message where mid = :mid and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
        fun queryMessageIndexByMid(mid: String?): Long?

        @Query("SELECT * FROM tmm_message where chatId == :chatId and sender == :uid and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
        fun queryMyMessagesByChatId(chatId: String?, uid: String?): MutableList<MessageEntity>

        @Query("SELECT * FROM tmm_message where chatId == :chatId and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
        fun queryAllMessagesByChatId(chatId: String?): MutableList<MessageEntity>

        @Query("SELECT * from tmm_message where isRead = ${MessageReadStatus.NOT_READ} and isDel != ${MessageDeleteStatus.IS_DEL}")
        fun queryMessagesByUnRead(): MutableList<MessageEntity>?

        @Query("SELECT * from tmm_message where id <:lastId and chatId = :chatId and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt}  ORDER BY id desc limit 1 ")
        fun queryMessageLimit(lastId: Long?, chatId: String?): MessageEntity?

        @Query("SELECT * from tmm_message where chatId in (:chatIds) and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt}  ORDER BY id desc limit 1 ")
        fun queryMessagesLimit(chatIds: MutableSet<String>?): MutableList<MessageEntity>?

        @Query("SELECT * from tmm_message where isRead = ${MessageReadStatus.NOT_READ} and chatId = :chatId and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
        fun queryUnReadConversationMessagesByChatId(chatId: String?): MutableList<MessageEntity>?

        @Query("SELECT * from tmm_message where isRead = ${MessageReadStatus.NOT_READ} and chatId in (:chatIds) and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
        fun queryUnReadConversationMessages(chatIds: MutableList<String>): MutableList<MessageEntity>?

        @Query("SELECT count(*) from tmm_message where isRead = ${MessageReadStatus.NOT_READ} and chatId = :chatId and atType in (:atTypes) and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
        fun queryAtMessagesByChatId(chatId: String?, atTypes: MutableList<Int>): Int?

        @Query("SELECT * from tmm_message where isRead = ${MessageReadStatus.NOT_READ} and chatId in (:chatIds) and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt} ")
        fun queryUnReadConversationMessagesByChatIds(chatIds: MutableSet<String>?): MutableList<MessageEntity>?

        @Query("update tmm_message set isRead = :isRead Where chatId = :chatId and type != ${MessageContentType.ContentType_Read_Receipt}")
        fun updateMessageReadByChatId(chatId: String, isRead: Int)

        @Query("update tmm_message set content = :content Where mid = :mid")
        fun updateMessageContent(mid: String?, content: String?)

        @Query("update tmm_message set isRead = ${MessageReadStatus.IS_READ} Where mid in (:mids) and type != ${MessageContentType.ContentType_Read_Receipt}")
        fun updateMessageIsReadByMids(mids: MutableSet<String>?)

        @Query("update tmm_message set isDel = ${MessageDeleteStatus.IS_DEL} where chatId in (:chatIds)")
        fun clearMessagesByChatIds(chatIds: MutableList<String>?)

        @Query("SELECT * from tmm_message where chatId in (:chatIds) and isDel != ${MessageDeleteStatus.IS_DEL}  and type != ${MessageContentType.ContentType_Read_Receipt} group by chatId having id = max(id)")
        fun queryMessageIndexByChatIds(chatIds: MutableSet<String>?): MutableList<MessageEntity>?

        @Query("SELECT * from tmm_message where chatId in (:chatIds) and type != ${MessageContentType.ContentType_Read_Receipt} group by chatId having sequence = max(sequence)")
        fun queryMessageSequenceByChatIds(chatIds: MutableSet<String>?): MutableList<MessageEntity>?

        @Query("SELECT * from tmm_message where chatId in (:chatIds) and isDel != ${MessageDeleteStatus.IS_DEL} and type in (:types) ORDER BY id desc")
        fun queryMessagesByType(chatIds: String?, types: MutableList<Int>): MutableList<MessageEntity>?

        @Query("SELECT mid from tmm_message where mid not in (:mids) and chatId = :chatId and isDel != ${MessageDeleteStatus.IS_DEL} and type != ${MessageContentType.ContentType_Read_Receipt}  ORDER BY id desc limit 1 ")
        fun queryMaxMessageMidByChatId(chatId: String?, mids: MutableList<String>?): String?

    }
}