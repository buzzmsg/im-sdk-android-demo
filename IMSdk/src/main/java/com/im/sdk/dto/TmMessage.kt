/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */
package com.im.sdk.dto

import android.os.Parcelable
import com.im.sdk.constant.MessageStatus
import com.im.sdk.message.content.TmMessageContent
import kotlinx.parcelize.Parcelize


@Parcelize
class TmMessage(
    var messageId: Long = 0,
    var chatId: String,
    var sender: String? = "",
    var mid: String,

    var content: TmMessageContent? = null,
    var status: com.im.sdk.constant.MessageStatus? = com.im.sdk.constant.MessageStatus.Sending,

    //    public Integer fileStatus = FileMessageDownloadStatus.Not_Require.value();
//    var extra: TmMessageExtra? = null,
    var messageUid: Long = 0,
    var serverTime: Long = 0,
    var sendTime: Long = 0,
    var displayTime: Long = 0,
    var sequence: Long = 0,
    var type: Int = 0,
    var action: TmMessageAction? = null,
    var isBrowse: Int? = 0,
    var isNeedSendEvent: Boolean = true,
    var isLocalSend: Boolean? = false,

    ) : Parcelable {

    companion object {
        const val MESSAGE_ACT_FORWARD = 1

        const val MESSAGE_ACT_REFERENCE = 2

        fun create(isNeedSendEvent: Boolean = true): TmMessage {
            return TmMessage(chatId = "", mid = "", isNeedSendEvent = isNeedSendEvent)
        }
    }

    fun digest(): String {
        return content?.digest(this) ?: ""
    }


    override fun equals(o: Any?): Boolean {
        if (o == null) {
            return false
        }

        if (o !is TmMessage) {
            return false
        }

        return o.mid == mid
    }

    override fun hashCode(): Int {
        return mid.hashCode()
    }

    override fun toString(): String {
        return "Message{" +
                "messageId=" + messageId +
                ", sender='" + sender + '\'' +
//                ", toUsers=" + Arrays.toString(toUsers) +
                ", content=" + content?.digest(this) +
                ", contentType=" + content!!.getMessageContentType() +
                ", status=" + status +
                ", messageUid=" + messageUid +
                ", serverTime=" + serverTime +
                '}'
    }

    @Parcelize
    data class TmMessageAction(
        var name: String? = ""
    ) : Parcelable
}