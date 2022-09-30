package com.tmmtmm.sdk.ui.view.vo

import android.os.Parcelable
import com.tmmtmm.sdk.cache.LoginCache.getUserId
import com.tmmtmm.sdk.constant.MessageStatus
import com.tmmtmm.sdk.logic.TmLoginLogic
import com.tmmtmm.sdk.message.content.TmMessageContent
import com.tmmtmm.sdk.message.core.MessageDirection
import kotlinx.parcelize.Parcelize

/**
 * @description
 * @version
 */
@Parcelize
data class TmmConversationVo(
    var conversationId: String = "",
    var conversationType: Int = 0,
    var uid: String? = null,
    var name: String,
    var dateCreated: Long = 0,
    var dateUpdated: Long = 0,
    var topTimeStamp: Long = 0,
    var membersCount: Int = 0,
    var unReadCount: Int? = 0,
//    var avatar: FileVo? = null,
    var avatarUrl: String = "",
    var outMessage: Int? = MessageDirection.Send.value(),
    var status: Int? = MessageStatus.Sending.value(),
    var lastMessageType: Int? = 0,
    var messageContent: TmMessageContent? = null,
    var isMute: Boolean = false,
    var isMuteShow: Boolean? = false,
    var isStick: Boolean = false,
    var defaultDisplayDate: String? = null,
    var mentioned: Boolean = false,
    var chatId: String? = "",

    var aChatId: String = "",
    var lastMid: String = "",
    var lastTmmMessage: TmmMessageVo? = null,
    var draftTmmMessage: TmmMessageVo? = null,
    var introduce: String = "",
    var introduceIsRead: Boolean = false,
    var isExistInGroup: Int? = 0,
    var id: Long = 0,
) : Parcelable {

    fun isSendError() = status == MessageStatus.Send_Failure.value()

    fun isSent() = status == MessageStatus.Sent.value()

    fun isOutMessage(): Boolean = lastTmmMessage?.uid == TmLoginLogic.getInstance().getUserId()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TmmConversationVo

        if (chatId != other.chatId) return false

        return true
    }

    override fun hashCode(): Int {
        return chatId.hashCode()
    }
}