package com.im.sdk.api

import androidx.annotation.Keep
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.annotations.SerializedName
import com.im.sdk.constant.MessageContentType
import com.im.sdk.constant.MessageDeleteStatus.NOT_DEL
import com.im.sdk.constant.MessageReadStatus
import com.im.sdk.constant.MessageReadStatus.NOT_READ
import com.im.sdk.constant.MessageStatus
import com.im.sdk.constant.UserConstant.THIRD_USER_ID
import com.im.sdk.core.net.BaseResponseEntity
import com.im.sdk.core.net.ResponseResult
import com.im.sdk.core.net.service.ApiBaseService
import com.im.sdk.core.net.toJson
import com.im.sdk.db.model.MessageModel

/**
 * @description
 *
 * @version
 */

@Keep
data class MessageInfoRequest(
    @SerializedName("ids")
    var mids: List<String>? = null
)

@Keep
data class MessageInfoResponse(
    var items: List<MessageInfoItem>? = null
)

@Keep
data class MessageInfoActionDto(
    @SerializedName("name")
    var name: String? = null, // name
)

@Keep
data class MessageInfoItem(
    @SerializedName("chat_id")
    var chatId: String? = null, // s_2x2qlr88wdcz_3a4ytt1zez26
    @SerializedName("achat_id")
    var aChatId: String? = null,
    @SerializedName("content")
    var content: String? = null, // 你好
    @SerializedName("create_time")
    var createTime: Long? = null, // 1621257958
    @SerializedName("id")
    var mid: String? = null, // 2x2qlr88wdcz
    @SerializedName("amid")
    var amid: String? = null, // 2x2qlr88wdcz
    @SerializedName("sender_id")
    var senderId: String? = null, // 3a4ytt1zez26
    @SerializedName("status")
    var status: Int? = NOT_DEL, // 1621257958
    @SerializedName("send_time")
    var sendTime: Long? = null, // 1621257958
    @SerializedName("update_time")
    var updateTime: Long? = null, // 1621257958
    @SerializedName("action")
    var action: MessageInfoActionDto? = null, // action
    var extra: String? = "",
    var type: Int? = com.im.sdk.constant.MessageContentType.ContentType_Text,
    var isRead: Int? = NOT_READ,
) {
    fun toMessageEntity(): MessageModel {
//        val content = if (content == null) {
////            val messagePayload = MessagePayload()
////            messagePayload
//            ""
//        } else {
//            content?.toJson().toString()
//
//        }
        val isRead =
            if (senderId == THIRD_USER_ID || type == com.im.sdk.constant.MessageContentType.ContentType_Read_Receipt) MessageReadStatus.IS_READ else isRead
        val messageModel = MessageModel()
        messageModel.id = 0
        messageModel.mid = mid ?: ""
        messageModel.chatId = chatId ?: ""
        messageModel.aChatId = aChatId ?:""
        messageModel.amid = amid ?:""
        messageModel.sender = senderId ?: ""
        messageModel.content = content
        messageModel.status = com.im.sdk.constant.MessageStatus.Sent.value()
        messageModel.crateTime = createTime
        messageModel.sendTime = sendTime
        messageModel.extra = extra
        messageModel.type = type
        messageModel.displayTime = createTime
        messageModel.readStatus = isRead ?: NOT_READ
        messageModel.delStatus = status
        messageModel.action = action?.toJson()?.toString()
        return messageModel
    }
}

object GetMessageInfo {
    private const val API = "/getMessageInfo"
    fun load(mids: List<String>): ResponseResult<MessageInfoResponse?> {

        val req = MessageInfoRequest(mids)
//        return fetchServer {
//            NetClient.getInstance().networkClient
//                .createService(LoadMessageInfoApi::class.java)
//                .fetchMessageInfo(req.toJson())
//        }
        val type = GsonUtils.getType(
            BaseResponseEntity::class.java,
            MessageInfoResponse::class.java,
            MessageInfoItem::class.java
        )
        val result =
            ApiBaseService.post<MessageInfoResponse?>(
                req.toJson().toString(),
                API,
                type
            )
        return result
    }
}

