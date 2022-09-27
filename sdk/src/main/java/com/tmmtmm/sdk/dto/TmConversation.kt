package com.tmmtmm.sdk.dto


/**
 * @description
 *
 * @time 2021/5/26 11:52 下午
 * @version
 */
class TmConversation(
    var id: Long,
    var chatId: String,
    var uid: String,
    var lastMid: String? = null,
    var timestamp: Long = 0,
    var topTimestamp: Long = 0,
    var isMute: Int? = 0,
    var isMuteShow: Boolean? = false,
    var headIndex: Long? = -1,
    var hideSequence: Long? = -1,
    var isHide: Boolean? = false,
    var name: String? = "",
//    var avatarInfo: FileVo? = null,
//    var avatar: String? = "",
    var unReadCount: Int? = 0,
    var lastTmMessage: TmMessage? = null,
    var draftTmMessage: TmMessage? = null,
    var groupMemberCount: Int? = 0,
    var introduce: String = "",
    var introduceIsRead: Boolean = false,
    var lastBrowseIndex: Long? = 0,
    var isExistInGroup: Int? = 0,
    ) {

    fun isMute() = isMute == 1


    override fun hashCode(): Int {
        return chatId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (other !is TmConversation) {
            return false
        }

        return chatId == other.chatId
    }

}