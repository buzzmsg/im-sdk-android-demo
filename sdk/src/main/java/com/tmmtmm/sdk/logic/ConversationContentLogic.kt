package com.tmmtmm.sdk.logic

import com.tmmtmm.sdk.dto.TmConversation
import com.tmmtmm.sdk.ui.view.vo.TmmConversationVo

/**
 * @description
 * @version
 */
fun MutableList<TmConversation>.transform(): MutableList<TmmConversationVo> {
    return this.map {
        var name: String? = ""
        name = it.name
        val displayName = it.timestamp

        val lastMessage = it.lastTmMessage?.let { it1 ->
            MessageContentLogic.getInstance().transformToTmmMessage(
                it1
            )
        }
//        val draftMessage = it.draftTmMessage?.transformToTmmMessageNoUser()

        TmmConversationVo(
            id = it.id,
            name = name ?: "",
            dateUpdated = it.timestamp,
            topTimeStamp = it.topTimestamp,
//            avatar = it.avatarInfo,
//            avatarUrl = AvatarThumb.getAvatarLocalPath(
//                it.avatarInfo?.text,
//                it.avatarInfo?.fileType
//            ),
//            outMessage = it.lastMessage?.direction?.ordinal ?: MessageDirection.Send.ordinal,
            status = lastMessage?.status,
            messageContent = null,
            isMute = it.isMute == 1,
            isMuteShow = it.isMuteShow,
            isStick = false,
            uid = it.uid,
            unReadCount = it.unReadCount,
            mentioned = false,
            lastMid = it.lastMid ?: "",
            lastTmmMessage = lastMessage,
//            draftTmmMessage = draftMessage,
            chatId = it.chatId,
            aChatId = it.aChatId,
            introduce = it.introduce,
            isExistInGroup = it.isExistInGroup,
        )
    }.toMutableList()
}