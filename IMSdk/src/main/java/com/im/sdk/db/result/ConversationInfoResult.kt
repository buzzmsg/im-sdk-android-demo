package com.im.sdk.db.result

import com.im.sdk.constant.ConversationMuteConstant
import com.im.sdk.constant.ConversationQuitStatus

data class ConversationInfoResult(
    var chatId: String = "",
//    var uid: String = "",
    var name: String? = null,
    var avatar: String? = null,
    var isMute: Int? = ConversationMuteConstant.IS_NOT_MUTE,
    var isTop: Int? = 0,
    var topTime: Long? = 0,
    var hideSequence: Long? = -1,
    var isExistInGroup: Int? = ConversationQuitStatus.GROUP_USER_EXIST,
)
