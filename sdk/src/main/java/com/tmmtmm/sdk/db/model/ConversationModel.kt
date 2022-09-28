package com.tmmtmm.sdk.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

//@Entity(
//    tableName = "tmm_conversation",
//    indices = [Index("timeStamp"),Index("chatId", unique = true)]
//)
//data class ConversationEntity(
//    @PrimaryKey(autoGenerate = true)
//    var id: Long = 0,
//    var chatId: String = "",
//    var uid: String = "",
//    var name: String? = null,
//    var avatar: String? = null,
//    var qrCodeUrl: String? = null,
//    var isMute: Int? = null,
//    var isTop: Int? = null,
//    var timeStamp: Long? = 0,
//    var lastMid: String? = null,
//    var lastMessageIndex: Long? = -1,
//    var hideSequence: Long? = -1,
//    @ColumnInfo(defaultValue = "")
//    var introduce: String = "",
//    var introduceIsRead: Int? = ConversationConstant.CHAT_GROUP_INTRODUCE_DEFAULT
//) {
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as ConversationEntity
//
//        if (chatId != other.chatId) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        return chatId.hashCode()
//    }
//}
@Entity(
    tableName = "tmm_conversation",
    indices = [Index("timeStamp"), Index("topTime")]
)
data class ConversationModel(
    @PrimaryKey
    var chatId: String = "",
    var uid: String = "",
    var name: String? = null,
    var avatar: String? = null,
    var qrCodeUrl: String? = null,
    var isMute: Int? = null,
    var isTop: Int? = null,
    var topTime: Long? = 0,
    var timeStamp: Long? = 0,
    var lastMid: String? = null,
    var lastMessageIndex: Long? = -1,
    var hideSequence: Long? = -1,
    @ColumnInfo(defaultValue = "1")
    var isExistInGroup: Int = 0,
    @ColumnInfo(defaultValue = "")
    var introduce: String = "",
    var introduceIsRead: Int? = 0,
    var lastBrowseIndex: Long? = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConversationModel

        if (chatId != other.chatId) return false

        return true
    }

    override fun hashCode(): Int {
        return chatId.hashCode()
    }
}

