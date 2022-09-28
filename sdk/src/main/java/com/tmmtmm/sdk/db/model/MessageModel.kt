package com.tmmtmm.sdk.db.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tmmtmm.sdk.constant.MessageContentType
import com.tmmtmm.sdk.constant.MessageDeleteStatus
import com.tmmtmm.sdk.constant.MessageReadStatus
import com.tmmtmm.sdk.constant.MessageStatus

/**
 * @description
 *
 * @version
 */
@Entity(
    tableName = "tmm_message",
    indices = [Index(
        "mid",
        unique = true
    ), Index("chatId", "type"), Index("sequence", orders = [Index.Order.DESC])]
)
@Keep
class MessageModel{

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var mid: String = ""
    @ColumnInfo(defaultValue = "0")
    var sequence: Long? = 0L
    var chatId: String = ""
    var sender: String? = ""
    var status: Int? = MessageStatus.Sending.value()
    var type: Int? = MessageContentType.ContentType_Unknown
    var content: String? = ""
    var extra: String? = ""
    var crateTime: Long? = 0L
    var sendTime: Long? = 0L
    var displayTime: Long? = 0L
    var delStatus: Int? = MessageDeleteStatus.NOT_DEL
    var action: String? = ""
    var readStatus: Int = MessageReadStatus.IS_READ

    override fun hashCode(): Int {
        return mid.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (other !is MessageModel) {
            return false
        }

        return mid == other.mid
    }

    override fun toString(): String {
        return "MessageEntity(id=$id, mid='$mid', sequence=$sequence, chatId='$chatId', sender=$sender, status=$status, type=$type, content=$content, extra=$extra, crateTime=$crateTime, sendTime=$sendTime, displayTime=$displayTime, isDel=$delStatus, action=$action, isRead=$readStatus)"
    }
}




