package com.tmmtmm.sdk.logic

import android.util.Log
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.tmmtmm.sdk.constant.MessageContentType.ContentType_Unknown
import com.tmmtmm.sdk.constant.MessageStatus
import com.tmmtmm.sdk.core.net.exception.TmException
import com.tmmtmm.sdk.core.net.exception.TmmError
import com.tmmtmm.sdk.core.net.toEntity
import com.tmmtmm.sdk.db.model.MessageModel
import com.tmmtmm.sdk.dto.TmMessage
import com.tmmtmm.sdk.message.content.*
import com.tmmtmm.sdk.message.core.ContentTag
import com.tmmtmm.sdk.message.core.MessageDirection
import java.lang.reflect.Modifier

/**
 * @description
 *
 * @version
 */
class MessageContentLogic {

    private val contentMapperTm: HashMap<Int, Class<out TmMessageContent?>> =
        HashMap()

    private val exclusionList: MutableSet<String> = mutableSetOf()

    companion object {

        private var instance: MessageContentLogic? = null

        @JvmName("getInstance1")
        fun getInstance(): MessageContentLogic {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = MessageContentLogic()
                    }
                }
            }
            return instance!!
        }
    }

    init {
        registerMessageContent(TmTextMessageContent::class.java)
//        registerMessageContent(TmImageTmMediaMessageContent::class.java)
//        registerMessageContent(TmVideoTmMediaMessageContent::class.java)
//        registerMessageContent(TmVoiceTmMediaMessageContent::class.java)
//        registerMessageContent(TmCallTmMessageContent::class.java)
//        registerMessageContent(TmAttachmentMediaMessageContent::class.java)
//        registerMessageContent(TmUidTextMessageContent::class.java)
//        registerMessageContent(TmRedPacketCenterMessageContent::class.java)
//        registerMessageContent(TmRevokeMessageContent::class.java)
//        registerMessageContent(TmMomentMessageContent::class.java)
//        registerMessageContent(TmAppletMessageContent::class.java)
//        registerMessageContent(TmVirtualCurrencyTransferMessageContent::class.java)
//        registerMessageContent(TmRedPacketMessageContent::class.java)
//        registerMessageContent(TmPayMessageContent::class.java)
//        registerMessageContent(UnknownMessageContent::class.java)
//
//        registerMessageContent(TmMeetingMessageContent::class.java)
//        registerMessageContent(TmAtMessageContent::class.java)
//        registerMessageContent(TmLocationMessageContent::class.java)
//
//        registerMessageContent(TmDeleteMessageContent::class.java)

        initSerializeExclusionList()
    }

    fun registerMessageContent(msgContentClsTm: Class<out TmMessageContent?>) {
//        validateMessageContent(msgContentCls)

        val cls = Class.forName(msgContentClsTm.name)
        val c = cls.getConstructor()
        require(c.modifiers == Modifier.PUBLIC) { "the default constructor of your custom messageContent class should be public" }
        val tagTm: ContentTag =
            msgContentClsTm.getAnnotation(ContentTag::class.java) as ContentTag

        val curClazz = contentMapperTm[tagTm.type]
        if (curClazz != null && curClazz != cls) {
            throw TmException(TmmError.getDec(TmmError.ERROR_OTHER_EXCEPTION.code))
        }
        contentMapperTm[tagTm.type] = msgContentClsTm
    }

//    fun convertToConversationInfo(conversationEntity: ConversationEntity?): TmConversationInfo {
//        val avatar: FileVo? = conversationEntity?.avatar?.toEntity<FileVo>()
//        return TmConversationInfo(
//            id = conversationEntity?.id ?: 0,
//            chatId = conversationEntity?.chatId ?: "",
//            uid = conversationEntity?.uid ?: "",
//            lastMid = conversationEntity?.lastMid,
//            timestamp = conversationEntity?.timeStamp ?: 0,
//            isMute = conversationEntity?.isMute,
//            isMuteShow = false,
//            headIndex = conversationEntity?.lastMessageIndex,
//            hideSequence = conversationEntity?.hideSequence,
//            introduce = conversationEntity?.introduce ?: "",
//            introduceIsRead = conversationEntity?.introduceIsRead == ConversationConstant.CHAT_GROUP_INTRODUCE_READ,
//            name = conversationEntity?.name,
//            avatarInfo = avatar,
//            lastBrowseIndex = conversationEntity?.lastBrowseIndex
//        )
//    }

    fun convertToTmMessage(messageEntity: MessageModel?): TmMessage {
        val message = TmMessage.create()
        message.messageId = messageEntity?.id ?: 0
        message.sender = messageEntity?.sender
        message.chatId = messageEntity?.chatId ?: ""
        message.mid = messageEntity?.mid ?: ""
        message.status =
            MessageStatus.status(messageEntity?.status ?: MessageStatus.Sent.value())
        message.content = contentOfType(messageEntity?.type ?: ContentType_Unknown)
        message.serverTime = messageEntity?.crateTime ?: 0
        message.sendTime = messageEntity?.sendTime ?: 0
        message.displayTime = messageEntity?.displayTime ?: 0
//        message.extra = messageEntity?.extra?.toEntity()
        message.messageUid = messageEntity?.id ?: 0
//        message.isBrowse = messageEntity?.isBrowse
//        message.isLocalSend = messageEntity?.isLocalSend
//        val payload: MessagePayload? = messageEntity.content?.toEntity()
        val messageContentFromPayload = messageContentFromPayload(
            messageEntity?.type ?: ContentType_Unknown,
            messageEntity?.content,
        )
        message.content =
            messageContentFromPayload
        message.type = messageContentFromPayload?.getMessageContentType() ?: ContentType_Unknown
        message.action = messageEntity?.action?.toEntity()
        return message
    }

    fun transform(tmMessage: TmMessage): MessageModel {

        val messageModel = MessageModel()
        messageModel.mid = tmMessage.mid
        messageModel.sequence = tmMessage.sequence
        messageModel.chatId = tmMessage.chatId
        messageModel.sender = tmMessage.sender
        messageModel.status = tmMessage.status?.value()
        messageModel.type = tmMessage.content?.getMessageContentType()
        messageModel.content = exclusionContent(tmMessage.content)
        return messageModel
    }

    fun messageContentFromPayload(
        contentType: Int,
        payload: String?,
    ): TmMessageContent? {
        var contentTm: TmMessageContent? = contentOfType(contentType)
        try {
            contentTm?.decode(payload)
        } catch (e: java.lang.Exception) {
            Log.w(
                "ClientService.TAG",
                "decode message error, fallback to unknownMessageContent. $contentType"
            )
            e.printStackTrace()
//            if (contentTm?.getPersistFlag() == TmPersistFlag.Persist) {
//                contentTm = UnknownMessageContent()
//                (contentTm as UnknownMessageContent?)?.orignalPayload = payload
//            } else {
//                return null
//            }
        }
        return contentTm
    }

    private fun contentOfType(type: Int): TmMessageContent? {
        val cls: Class<out TmMessageContent?>? = contentMapperTm[type]
        if (cls != null) {
            return try {
                cls.newInstance()
            } catch (e: Exception) {
                Log.w(
                    "",
                    "create message content instance failed, fall back to UnknownMessageContent, the message content class must have a default constructor. $type"
                )
                e.printStackTrace()
                UnknownMessageContent()
            }
        }
        return UnknownMessageContent()
    }

    private fun initSerializeExclusionList() {
        exclusionList.add(EXCLUSION_TRANSLATION)
        exclusionList.add(EXCLUSION_TRANSLATE_STATUS)
        exclusionList.add(EXCLUSION_TRANSLATE_LANG)
        exclusionList.add(EXCLUSION_TRANSLATE_HIDE)
    }

    private fun exclusionContent(content: TmMessageContent?): String {
        val exclusionStrategy = object : ExclusionStrategy {
            override fun shouldSkipField(f: FieldAttributes?): Boolean {
                return exclusionList.contains(f?.name)
            }

            override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                return false
            }
        }

        val gson = GsonBuilder().addSerializationExclusionStrategy(exclusionStrategy).create()
        return gson.toJson(content).toString()
    }


}