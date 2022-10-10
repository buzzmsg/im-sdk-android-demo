package com.im.sdk.api

import androidx.annotation.Keep
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import com.im.sdk.constant.MessageContentType
import com.im.sdk.core.db.DataBaseManager
import com.im.sdk.core.net.BaseResponseEntity
import com.im.sdk.core.net.ResponseResult
import com.im.sdk.core.net.exception.TmException
import com.im.sdk.core.net.service.ApiBaseService
import com.im.sdk.core.net.toJson
import com.im.sdk.db.model.MessageModel
import java.util.concurrent.ConcurrentHashMap

/**
 * @description
 *
 * @time 2021/5/18 11:15 上午
 * @version
 */

@Keep
data class SendMessageRequest(
    var mid: String? = "",
    var amid: String? = "",
    @SerializedName("chat_id")
    var chatId: String? = "",
    var content: String? = "",
    var type: Int? = com.im.sdk.constant.MessageContentType.ContentType_Text,
    var extra: String? = null,
    @SerializedName("send_time")
    var sendTime: Long
)

@Keep
data class SendMessageResponse(
    @SerializedName("err_code")
    var errorCode: Int? = 0,
    var sequence: Long? = 0,
    @SerializedName("err_msg")
    var errorMsg: String? = ""

)

object SendMessage {

    private const val API = "/sendMessage"

    private const val RETRY_TIMES = 3

    private val retryMessageMap = ConcurrentHashMap<String, Int>()

    fun send(messageEntity: MessageModel): ResponseResult<SendMessageResponse?> {
        val toJson = JsonParser.parseString(messageEntity.content).asJsonObject
        if (toJson?.has("filePath") == true) {
            toJson.remove("filePath")
        }

        if (toJson?.has("posterPath") == true) {
            toJson.remove("posterPath")
        }
        messageEntity.content = toJson.toString()
        val req = SendMessageRequest(
            mid = messageEntity.mid,
            amid = messageEntity.amid,
            chatId = messageEntity.chatId,
            content = messageEntity.content,
            type = messageEntity.type,
            sendTime = messageEntity.sendTime ?: 0
        )
        if (!messageEntity.extra.isNullOrBlank()) {
            req.extra = messageEntity.extra
        }

        val type = GsonUtils.getType(
            BaseResponseEntity::class.java,
            SendMessageResponse::class.java
        )
//        TmLogUtils.getInstance().w(content = "send Message request ${req.toJson()}")
        val result = ApiBaseService.post<SendMessageResponse?>(req.toJson().toString(), API, type)

        if (result !is ResponseResult.Success) {
            return result
        }

        val status = result.value?.errorCode

        if (status != null && status != 0) {
            //business failed
//            handleError(messageEntity, status)
//            TmLogUtils.getInstance().w(content = "send Message response ${result.toJson()}")
            retryMessage(messageEntity)
            return ResponseResult.Failure(TmException(status, ""))
        } else {
            //update sequence
            if (retryMessageMap.contains(messageEntity.mid)) {
                removeRetryMessages(messageEntity.mid)
            }
//            TmLogUtils.getInstance().w(content = "send Message response ${result.toJson()}")
            val sequence = result.value?.sequence
            DataBaseManager.getInstance().getDataBase()
                ?.messageDao()
                ?.updateSequence(messageEntity.mid, sequence)
        }

        return result
    }

//    private fun handleError(messageEntity: MessageEntity, code: Int) {
//        val mChatId = ChatId.createById(messageEntity.chatId)
//        if (mChatId.isSingle()) {
//            handleSingleMessage(messageEntity, code)
//        } else {
//            handleGroupMessage(messageEntity, code)
//        }
//    }

//    private fun handleGroupMessage(messageEntity: MessageEntity, code: Int) {
//        when (code) {
//            ERROR_NOT_IN_GROUP -> {
//                MessageManager.getInstance()
//                    .updateStatus(messageEntity, TmMessageStatus.Send_Failure.value())
//                GroupMemberManager.getInstance().refreshGroupMember(messageEntity.chatId)
//                DataBaseManager.getInstance()
//                    .getDataBase()
//                    ?.conversationDao()
//                    ?.updateExistInConversation(mutableListOf(messageEntity.chatId),
//                        ConversationQuitStatus.GROUP_USER_NOT_EXIST)
//                GroupManager.getInstance().createSnapShot(messageEntity.chatId)
//            }
//
//            else -> {
//                retryMessage(messageEntity)
//            }
//        }
//    }

//    private fun handleSingleMessage(messageEntity: MessageEntity, code: Int) {
//        val targetUid = ChatId.createById(messageEntity.chatId).getTargetId() ?: ""
//        TmUserApiManager.getInstance()
//            .syncSenderRelationShipByMessages(mutableListOf(messageEntity))
//        when {
//            UserRelationChangeLogic.getInstance().isMatchRelation(code) -> {
//                UserRelationChangeLogic.getInstance()
//                    .handleUserRelationChange(code, uids = mutableListOf(targetUid), true)
//                MessageManager.getInstance()
//                    .updateStatus(messageEntity, TmMessageStatus.Send_Failure.value())
//            }
//            UserRelationChangeLogic.getInstance().isMatchBlock(code) -> {
//                MessageManager.getInstance()
//                    .updateStatus(messageEntity, TmMessageStatus.Send_Failure.value())
//                handleBlockMessage(chatId = messageEntity.chatId, uid = targetUid, code = code)
//            }
//
//            else -> {
//                retryMessage(messageEntity)
//            }
//        }
//    }

//    private fun handleBlockMessage(chatId: String, uid: String?, code: Int?) {
//        val tmMessage = TmMessage.create()
//        //create message
//        tmMessage.content =
//            TmUidTextMessageContent(
//                target = mutableListOf(TmLoginManager.getUserId()),
//                temId = "block-user"
//            )
//
//        val messageEntity = TmHelper.getInstance().transform(tmMessage)
//        val mUid = TmLoginManager.getUserId()
//        val messageId: String = MessageId.create(mUid)
//
//        //create message
//        messageEntity.sender = THIRD_USER_ID
//        messageEntity.type = TmMessageContentType.ContentType_Uid_Text
//        messageEntity.status = TmMessageStatus.Sent.value()
//        messageEntity.chatId = chatId
//        messageEntity.mid = messageId
//
//        messageEntity.crateTime = System.currentTimeMillis()
//        messageEntity.sendTime = System.currentTimeMillis()
//        messageEntity.isRead = IS_READ
//
//
//        //save message,status=sending
//        val id = MessageManager.getInstance().insert(messageEntity)
//        messageEntity.id = id ?: 0
//
//
//        TmConversationApiManager.getInstance().insertOrUpdateConversation(messageEntity)
//        //send event
//        MessageEvent.send(mutableSetOf(messageEntity.mid), messageEntity.chatId)
//        ConversationEvent.send(mutableSetOf(messageEntity.chatId))
//
//        UserRelationChangeLogic.getInstance()
//            .handleUserRelationChange(code = code, uids = mutableListOf(uid ?: ""), false)
//    }


    private fun retryMessage(messageEntity: MessageModel) {
        var retryTimes = retryMessageMap[messageEntity.mid] ?: 0
        if (retryTimes >= RETRY_TIMES) {
            removeRetryMessages(messageEntity.mid)
            return
        }
        retryTimes += 1
        retryMessageMap[messageEntity.mid] = retryTimes
        send(messageEntity)
    }

    private fun removeRetryMessages(mid: String?) {
        val iterator = retryMessageMap.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.key == mid) {
                iterator.remove()
            }
        }
    }
}


