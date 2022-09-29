package com.tmmtmm.sdk.ui.view.vo

import android.os.Parcelable
import com.tmmtmm.sdk.constant.MessageContentType
import com.tmmtmm.sdk.constant.MessageStatus
import com.tmmtmm.sdk.constant.UserConstant.THIRD_USER_ID
import com.tmmtmm.sdk.core.id.ChatId
import com.tmmtmm.sdk.dto.TmMessage
import com.tmmtmm.sdk.logic.TmLoginLogic
import com.tmmtmm.sdk.message.content.TmTextMessageContent
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.TimeUnit

@Parcelize
data class TmmMessageVo(
    var messageId: Long = 0,
    var messageBody: String?,
    var hasTime: Boolean = false,
    var createTime: Long = 0,
    var sendTime: Long = 0,
    var displayTime: Long = 0,
    var isOutMessage: Boolean = false,
    var avatarImageUrl: String? = null,
    var name: String? = "",
//    var tmAttachment: TmAttachment? = null,
    var tmTextMessageContent: TmTextMessageContent? = null,
//    var virtualCurrencyTransferMessageContent: TmVirtualCurrencyTransferMessageContent? = null,
//    var tmRedPacketMessageContent: TmRedPacketMessageContent? = null,
//    var tmRedPacketCenterMessageContent: TmRedPacketCenterMessageContent? = null,
//    var tmmPayMessageContent: TmPayMessageContent? = null,
//    var tmmMeetingMessageContent: TmMeetingMessageContent? = null,
//    var tmmAtMessageContent: TmAtMessageContent? = null,
//    var tmmUidTextMessageContent: TmUidTextMessageContent? = null,
//    var tmmLocationMessageContent: TmLocationMessageContent? = null,
    var isPlayVoice: Boolean = false,
//    var callRecords: TmCallMessage? = null,
    var messageType: Int = MessageContentType.ContentType_Text,
    var status: Int = MessageStatus.Sending.value(),
    var uid: String?,
//    var isTranslate: Boolean? = false,
//    var translateContent: String? = null,
//    var translateAtContent: MutableList<AtMessageContentItem>? = null,
//    var translateStatus: Int? = TRANSLATE_FAILED,
    var recallMessageId: Long? = null,
    var extras: String? = null,
    var thumbUrl: String? = null,
    var quoteMessageVo: TmmQuoteMessageVo? = null,
//    var miniAppMessage: TmAppletMessageContent? = null,
//    val momentMessage: TmMomentMessageContent? = null,
    val systemHintMessage: String? = null,
//    val callStatusMessage: CallStatusMessage? = null,
    val isDisappearSet: Boolean? = false,
    val disappearTime: Int? = 0,
    val readTime: Long? = 0L,
    var voiceProgress: Int = 0,
    var mid: String? = "",
    var originFilePath: String? = "",
    var chatId: String? = "",
    var action: TmMessage.TmMessageAction? = null,
    var isLocalSend: Boolean? = false,
    var isSameUser: Boolean? = false,
    var isSameGroup: Boolean? = false,
    var isSearchMessage: Boolean = false,
//    var atUserList: MutableList<UserInfo>? = null
) : Parcelable, BaseMessageType {

//    fun getRealVoiceUrl(): String {
//        if (FileUtils.isFileExists(tmAttachment?.filePath)) {
//            return tmAttachment?.filePath ?: ""
//        }
//        return tmAttachment?.url ?: ""
//    }

    fun isCenterSender() = uid == THIRD_USER_ID

    fun isDefault() = isTextMessage()
            || messageType == MessageContentType.ContentType_RTC


    fun isTextMessage() = messageType == MessageContentType.ContentType_Text

    fun isAtMessage() = messageType == MessageContentType.ContentType_At

    private fun isCallMessage() =
        messageType == MessageContentType.ContentType_RTC || messageType == MessageContentType.ContentType_Meeting

    fun isAttachment() =
        messageType == MessageContentType.ContentType_Image || messageType == MessageContentType.ContentType_File

    fun isVideo(): Boolean = messageType == MessageContentType.ContentType_Video
    fun isImage(): Boolean = messageType == MessageContentType.ContentType_Image

    fun canCopy() =
        isTextMessage() || isAtMessage()

    fun canDelete() = !isSending()

//    fun canDeleteAll(): Boolean {
//        return !isSending() && !isSendError() && messageType != MessageContentType.ContentType_Red_Packet
//                && messageType != MessageContentType.ContentType_Virtual_Currency_Transfer && !isCallMessage() && uid == TmLoginManager.getUserId()
//    }

    fun canSelect(): Boolean {
        return !isSending()
    }

    fun canForward() =
        (isAttachment() || isVideo() || isDefault() || isMiniApp() || isMoment() || isLocation()) &&
                if (isOutMessage) {
                    (isSent() || isRead()) && !isCallMessage()
                } else {
                    !isCallMessage()
                }


    fun canQuote() = (isAttachment() || isVideo() || isDefault() || isMiniApp() || isAtMessage()
            || isMoment() || isLocation()) &&
            if (isOutMessage) {
                (isSent() || isRead()) && !isCallMessage()
            } else {
                !isCallMessage()
            }

    fun canTranslate(): Boolean {
        return (isTextMessage() || isAtMessage()) && !isSending() && !isSendError() && !isOutMessage
    }

    fun isSending() = status == MessageStatus.Sending.value()

    fun isSendError() = status == MessageStatus.Send_Failure.value()

    fun isSent() = status == MessageStatus.Sent.value()

    fun isRead() = status == MessageStatus.Readed.value()

    fun isMiniApp() = messageType == MessageContentType.ContentType_Applet

    fun isMoment() = messageType == MessageContentType.ContentType_Moment

    fun isLocation() = messageType == MessageContentType.ContentType_Location

    fun isRedPacket() = messageType == MessageContentType.ContentType_Red_Packet

    fun isMoneyTransfer() =
        messageType == MessageContentType.ContentType_Virtual_Currency_Transfer

    override fun getItemType(): Int {
        return if (uid == TmLoginLogic.getInstance().getUserId()) {
            0 - messageType
        } else messageType
    }

//    fun isCallStatusMessage() =
//        null != callStatusMessage && messageType == CALL_STATUS_MESSAGE_CONTENT

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other !is TmmMessageVo) {
            return false
        }

        return mid == other.mid
    }

    override fun hashCode(): Int {
        return mid.hashCode()
    }

    fun isGroup(): Boolean {
        return !ChatId.createById(chatId ?: "").isSingle()
    }

    override fun toString(): String {
        return "TmmMessageVo(messageId=$messageId, messageBody=$messageBody, displayTime=$displayTime, mid=$mid, chatId=$chatId)"
    }


}

const val ContentType_Local_Timestamp = 8889
val DEL_TIME = TimeUnit.MINUTES.toMillis(2)


