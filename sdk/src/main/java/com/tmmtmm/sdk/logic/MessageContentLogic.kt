package com.tmmtmm.sdk.logic

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.tmmtmm.sdk.constant.MessageContentType
import com.tmmtmm.sdk.constant.MessageContentType.ContentType_Unknown
import com.tmmtmm.sdk.constant.MessageStatus
import com.tmmtmm.sdk.core.id.ChatId
import com.tmmtmm.sdk.core.net.exception.TmException
import com.tmmtmm.sdk.core.net.exception.TmmError
import com.tmmtmm.sdk.core.net.toEntity
import com.tmmtmm.sdk.db.model.MessageModel
import com.tmmtmm.sdk.dto.TmMessage
import com.tmmtmm.sdk.message.content.*
import com.tmmtmm.sdk.message.core.ContentTag
import com.tmmtmm.sdk.message.core.MessageDirection
import com.tmmtmm.sdk.ui.view.vo.TmmMessageVo
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


    fun transformToTmmMessage(tmMessage: TmMessage): TmmMessageVo {
        val isOutBox = tmMessage.sender == TmLoginLogic.getInstance().getUserId()



        var contentType = tmMessage.type

//        val userName = contact?.userName

        val textMessageContent =
            tmMessage.content.takeIf { contentType == MessageContentType.ContentType_Text && tmMessage.content is TmTextMessageContent }
                ?.let { (tmMessage.content as TmTextMessageContent) }

        val messageText = tmMessage.digest()


//        val virtualCurrencyTransferMessageContent =
//            content.takeIf { contentType == TmMessageContentType.ContentType_Virtual_Currency_Transfer && content is TmVirtualCurrencyTransferMessageContent }
//                ?.let { (content as TmVirtualCurrencyTransferMessageContent) }
//
//        val redPacketMessage =
//            content.takeIf { contentType == TmMessageContentType.ContentType_Red_Packet && content is TmRedPacketMessageContent }
//                ?.let { (content as TmRedPacketMessageContent) }
//
//        val tmmPayMessageContent =
//            content.takeIf { contentType == TmMessageContentType.ContentType_Virtual_Currency_Pay && content is TmPayMessageContent }
//                ?.let { (content as TmPayMessageContent) }
//
//        val redPacketCenterMessage =
//            content.takeIf { contentType == TmMessageContentType.ContentType_Red_Packet_Center && content is TmRedPacketCenterMessageContent }
//                ?.let { (content as TmRedPacketCenterMessageContent) }
//
//
//        val callRecordsMessage =
//            content.takeIf { contentType == TmMessageContentType.ContentType_RTC && content is TmCallTmMessageContent }
//                ?.let {
//                    val callMessage = (content as TmCallTmMessageContent).callMessage
//                    TmCallMessage(
//                        callMessage?.type ?: TmCallMessage.Type.CANCEL.value,
//                        callMessage?.duration,
//                        callMessage?.callType ?: TmCallMessage.CallType.VOICE.value,
//                        callMessage?.operatorId
//                    )
//                }
//
//        val mediaMessageContent = content.takeIf { content is TmMediaMessageContent }
//            ?.let { (content as TmMediaMessageContent) }
//
//
//        val attachment =
//            GsonUtils.fromJson(mediaMessageContent.toJson(), TmAttachment::class.java)
//
//        attachment?.apply {
//            when (contentType) {
//                TmMessageContentType.ContentType_Image -> {
//                    type = TmAttachmentType.IMAGE.ordinal
//
//                    val imageMessageContent = content as TmImageTmMediaMessageContent
//                    bucketId = imageMessageContent.bucketId
//                    width = imageMessageContent.width?.toFloat()
//                    height = imageMessageContent.height?.toFloat()
//                    isOriginal = imageMessageContent.isOrigin == 1
//                    size = imageMessageContent.size
//                    objectId = imageMessageContent.objectId
//                    transferType = imageMessageContent.transferType
//                    filePath = imageMessageContent.filePath
//
//                    val fileName = FileName.createFileName(imageMessageContent.objectId ?: "")
//                    val originPath =
//                        FileCacheServerImpl.INSTANCE.getFileCacheDirectory() + fileName.tmFileName + "." + this.fileType
//                    if (this.fileType?.lowercase(Locale.getDefault()) == "gif") {
//                        bigThumbFilePath =
//                            originPath
//                        originFilePath =
//                            originPath
//
//                        smallThumbFilePath = originPath
//
//                    } else {
//                        val thumbSize = TmThumbUtils.getImgThumb(
//                            imageMessageContent.width ?: 0,
//                            imageMessageContent.height ?: 0
//                        )
//                        if (isOriginal == true) {
//                            val bigThumbSize = TmThumbUtils.getBigImgThumb(
//                                imageMessageContent.width,
//                                imageMessageContent.height
//                            )
//                            bigThumbFilePath = FileCacheServerImpl.INSTANCE.getFileCacheDirectory() +
//                                    fileName
//                                        .createThumbFileName(
//                                            bigThumbSize.first,
//                                            bigThumbSize.second
//                                        ) + "." + this.fileType
//
//                            originFilePath =
//                                originPath
//                        } else {
//                            originFilePath =
//                                originPath
//                            bigThumbFilePath = originFilePath
//                        }
//                        smallThumbFilePath = FileCacheServerImpl.INSTANCE.getFileCacheDirectory() +
//                                fileName
//                                    .createThumbFileName(
//                                        thumbSize.first,
//                                        thumbSize.second
//                                    ) + "." + this.fileType
//                    }
//
//                }
//                TmMessageContentType.ContentType_Voice -> {
//                    type = TmAttachmentType.AUDIO.ordinal
//                    val voiceMessageContent = content as TmVoiceTmMediaMessageContent
//                    val fileName = FileName.createFileName(voiceMessageContent.objectId ?: "")
//                    filePath =
//                        FileCacheServerImpl.INSTANCE.getFileCacheDirectory() + fileName.tmFileName + "." + this.fileType
//                    objectId = voiceMessageContent.objectId
//                    bucketId = voiceMessageContent.bucketId
//                    transferType = voiceMessageContent.transferType
//                }
//                TmMessageContentType.ContentType_File -> {
//                    type = TmAttachmentType.FILE.ordinal
//                    val attachmentMessageContent = content as TmAttachmentMediaMessageContent
//                    val fileName = FileName.createFileName(attachmentMessageContent.objectId ?: "")
//                    filePath =
//                        FileCacheServerImpl.INSTANCE.getFileCacheDirectory() + fileName.tmFileName + "." + this.fileType
//                    name = attachmentMessageContent.name
//                    size = attachmentMessageContent.size
//                    fileSize = ConvertUtils.byte2FitMemorySize(size ?: 0, 1)
//                    objectId = attachmentMessageContent.objectId
//                    bucketId = attachmentMessageContent.bucketId
//                    transferType = attachmentMessageContent.transferType
//                }
//
//                TmMessageContentType.ContentType_Video -> {
//                    type = TmAttachmentType.VIDEO.ordinal
//                    val videoMessageContent = content as TmVideoTmMediaMessageContent
//                    videoCoverFormat = videoMessageContent.poster?.fileType
//                    val thumbSize = TmThumbUtils.getImgThumb(
//                        videoMessageContent.poster?.width?.toInt() ?: 0,
//                        videoMessageContent.poster?.height?.toInt() ?: 0
//                    )
//                    val posterFileName =
//                        FileName.createFileName(videoMessageContent.poster?.objectId ?: "")
//                    videoCover = FileCacheServerImpl.INSTANCE.getFileCacheDirectory() +
//                            posterFileName
//                                .createThumbFileName(
//                                    thumbSize.first,
//                                    thumbSize.second
//                                ) + "." + videoCoverFormat
//                    val fileName = FileName.createFileName(videoMessageContent.objectId ?: "")
//                    filePath =
//                        FileCacheServerImpl.INSTANCE.getFileCacheDirectory() + fileName.tmFileName + "." + this.fileType
//                    size = videoMessageContent.duration
//                    postObjectId = videoMessageContent.poster?.objectId
//                    objectId = videoMessageContent.objectId
//                    bucketId = videoMessageContent.bucketId
//                    width = videoMessageContent.poster?.width
//                    height = videoMessageContent.poster?.height
//                    transferType = videoMessageContent.transferType
//                }
//            }
//            fileFormat = this.fileType
//        }
//
//        val miniAppMessage =
//            content.takeIf { contentType == TmMessageContentType.ContentType_Applet && content is TmAppletMessageContent }
//                ?.let { content as TmAppletMessageContent }
//
//        val meetingMessage =
//            content.takeIf { contentType == TmMessageContentType.ContentType_Meeting && content is TmMeetingMessageContent }
//                ?.let { content as TmMeetingMessageContent }
//
//        val atMessage =
//            content.takeIf { contentType == TmMessageContentType.ContentType_At && content is TmAtMessageContent }
//                ?.let { content as TmAtMessageContent }
//
//        val uidTextMessage =
//            content.takeIf { contentType == TmMessageContentType.ContentType_Uid_Text && content is TmUidTextMessageContent }
//                ?.let { content as TmUidTextMessageContent }
//
//
//        val momentMessage =
//            content.takeIf { contentType == TmMessageContentType.ContentType_Moment && content is TmMomentMessageContent }
//                ?.let { content as TmMomentMessageContent }
//
//        val locationMessage =
//            content.takeIf { contentType == TmMessageContentType.ContentType_Location && content is TmLocationMessageContent }
//                ?.let { content as TmLocationMessageContent }
//
//
//        val isTranslate = if (content is TmTextMessageContent) {
//            !TextUtils.isEmpty((content as TmTextMessageContent).translation) && (content as TmTextMessageContent).isTranslationHide == TRANSLATE_NORMAL
//        } else if (content is TmAtMessageContent) {
//            (content as TmAtMessageContent).translation != null && (content as TmAtMessageContent).isTranslationHide == TRANSLATE_NORMAL
//        } else {
//            false
//        }
//
//        val translateContent = if (content is TmTextMessageContent) {
//            (content as TmTextMessageContent).translation
//        } else {
//            null
//        }
//
//        val translateAtContent = if (content is TmAtMessageContent) {
//            (content as TmAtMessageContent).translation
//        } else {
//            null
//        }
//
//        val translateStatus = if (content is TmTextMessageContent) {
//            (content as TmTextMessageContent).translateStatus
//        } else if (content is TmAtMessageContent) {
//            (content as TmAtMessageContent).translateStatus
//        } else {
//            TRANSLATE_FAILED
//        }
//
//        var quoteMessageVo: TmmQuoteMessageVo? = null
//        if (this.extra != null) {
//            quoteMessageVo = TmmQuoteMessageVo()
//            quoteMessageVo.mids = this.extra?.mids
//        }

        val disappearTime = 0

        return TmmMessageVo(
            tmMessage.messageId,
            messageBody = messageText,
            false,
            createTime = tmMessage.serverTime,
            sendTime = tmMessage.sendTime,
            displayTime = tmMessage.displayTime,
            isOutBox,
//            contact?.avatar,
//            userName,
            tmTextMessageContent = textMessageContent,
//            virtualCurrencyTransferMessageContent = virtualCurrencyTransferMessageContent,
            status = tmMessage.status?.value() ?: MessageStatus.Sending.value(),
//            tmAttachment = attachment,
//            tmRedPacketMessageContent = redPacketMessage,
//            tmRedPacketCenterMessageContent = redPacketCenterMessage,
//            tmmPayMessageContent = tmmPayMessageContent,
//            tmmAtMessageContent = atMessage,
//            tmmUidTextMessageContent = uidTextMessage,
//            tmmLocationMessageContent = locationMessage,
//            callRecords = callRecordsMessage,
            messageType = contentType,
            uid = tmMessage.sender,
//            isTranslate = isTranslate,
//            translateContent = translateContent,
//            translateAtContent = translateAtContent,
//            translateStatus = translateStatus,
            extras = "",
//            quoteMessageVo = quoteMessageVo,
//            miniAppMessage = miniAppMessage,
//            momentMessage = momentMessage,
//            tmmMeetingMessageContent = meetingMessage,
            disappearTime = disappearTime,
            mid = tmMessage.mid,
            chatId = tmMessage.chatId,
            action = tmMessage.action,
            isLocalSend = tmMessage.isLocalSend
        )
    }


}