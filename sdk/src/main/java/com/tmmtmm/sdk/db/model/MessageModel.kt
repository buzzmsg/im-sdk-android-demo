package com.tmmtmm.sdk.db.model

import androidx.annotation.Keep
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
class MessageEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var mid: String = ""
    var sequence: Long? = 0
    var chatId: String = ""
    var sender: String? = ""
    var status: Int? = MessageStatus.Sending.value()
    var type: Int? = MessageContentType.ContentType_Text
    var content: String? = ""
    var extra: String? = ""
    var crateTime: Long? = 0
    var sendTime: Long? = 0
    var displayTime: Long? = 0
    var isDel: Int? = MessageDeleteStatus.NOT_DEL
    var action: String? = ""
    var isRead: Int = MessageReadStatus.NOT_READ
    var atType: Int? = 0
    var isBrowse: Int? = 0
    var isLocalSend: Boolean? = false

    override fun hashCode(): Int {
        return mid.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (other !is MessageEntity) {
            return false
        }

        return mid == other.mid
    }

    override fun toString(): String {
        return "MessageEntity(id=$id, mid='$mid', sequence=$sequence, chatId='$chatId', sender=$sender, status=$status, type=$type, content=$content, extra=$extra, crateTime=$crateTime, sendTime=$sendTime, displayTime=$displayTime, isDel=$isDel, action=$action, isRead=$isRead)"
    }
}




