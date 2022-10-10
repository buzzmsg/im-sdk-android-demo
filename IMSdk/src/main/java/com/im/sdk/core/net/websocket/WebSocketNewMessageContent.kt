package com.im.sdk.core.net.websocket

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class WebSocketContent<T>(
    @SerializedName("cmd")
    var cmd: String? = "", // new_msg
    @SerializedName("items")
    var items: T?
)

@Keep
data class NewMessagePushDto(
    @SerializedName("chat_id")
    var chatId: String? = "", // xxx
    @SerializedName("create_time")
    var createTime: Long? = 0L, // 1231313
    @SerializedName("id")
    var id: String? = "", // xxxx
    @SerializedName("content")
    var content: String? = "",
    @SerializedName("sender_id")
    var senderId: String? = "",
    @SerializedName("sequence")
    var sequence: Long? = 0L, // 51
    @SerializedName("type")
    var type: Int? = 0 // 1
)

@Keep
data class NewMessageTextContentDto(
    @SerializedName("text")
    var text: String? = ""
)

data class ApplyFriendPushDtoResponse(
    @SerializedName("content")
    var content: String? = "", // {"operator":"33wvqbgrl9ls","target":["2ryyah8e90d3"],"temId":"apply-add-friend"}
    @SerializedName("create_time")
    var createTime: Long? = null, // 1626351745698
    @SerializedName("id")
    var id: String? = null, // 98c421deb225c6ea
    @SerializedName("type")
    var type: Int? = null // 18
)

data class ApplyFriendPushContentDto(
    @SerializedName("operator")
    var operator: String? = "",
    @SerializedName("target")
    var target: MutableList<String>? = null,
    @SerializedName("temId")
    var temId: String? = ""
)

@Keep
data class AgreeFriendPushDtoResponse(
    @SerializedName("content")
    var content: String?, // {"operator":"2x2qlr88wdcz","target":"3ei5da9ve2h8","temId":"agree-add-friend"}
    @SerializedName("id")
    var id: String? = "" // 19b42081ee2a3ad8
)

@Keep
data class AgreeFriendPushContentDto(
    @SerializedName("operator")
    var `operator`: String? = "", // 2x2qlr88wdcz
    @SerializedName("target")
    var target: MutableList<String>? = null, // 3ei5da9ve2h8
    @SerializedName("temId")
    var temId: String? = "" // agree-add-friend
)

@Keep
data class SettingTopDtoResponse(
    @SerializedName("content")
    var content: String?, // {"operator":"2x2qlr88wdcz","target":"3ei5da9ve2h8","temId":"agree-add-friend"}
    @SerializedName("id")
    var id: String? = "" // 19b42081ee2a3ad
)

@Keep
data class SettingTopContentDto(
    @SerializedName("chat_id")
    var chatId: String?, // {"operator":"2x2qlr88wdcz","target":"3ei5da9ve2h8","temId":"agree-add-friend"}
)

@Keep
data class SettingMuteDtoResponse(
    @SerializedName("content")
    var content: String?, // {"operator":"2x2qlr88wdcz","target":"3ei5da9ve2h8","temId":"agree-add-friend"}
    @SerializedName("id")
    var id: String? = "" // 19b42081ee2a3ad
)

@Keep
data class SettingMuteContentDto(
    @SerializedName("chat_ids")
    var chatIds: MutableList<String>?, // {"operator":"2x2qlr88wdcz","target":"3ei5da9ve2h8","temId":"agree-add-friend"}
)

@Keep
data class SettingHideDtoResponse(
    @SerializedName("content")
    var content: String?, // {"operator":"2x2qlr88wdcz","target":"3ei5da9ve2h8","temId":"agree-add-friend"}
    @SerializedName("id")
    var id: String? = "" // 19b42081ee2a3ad
)

@Keep
data class SettingHideContentDto(
    @SerializedName("chat_id")
    var chatId: String?,
    var sequence: Long?// {"operator":"2x2qlr88wdcz","target":"3ei5da9ve2h8","temId":"agree-add-friend"}
)

@Keep
data class BlockPushDtoResponse(
    @SerializedName("content")
    var content: String?, // {"operator":"2x2qlr88wdcz","target":"3ei5da9ve2h8","temId":"agree-add-friend"}
    @SerializedName("id")
    var id: String? = "" // 19b42081ee2a3ad8
)

@Keep
data class BlockPushContentDto(
    @SerializedName("operator")
    var `operator`: String? = "", // 2x2qlr88wdcz
    @SerializedName("target")
    var target: String? = null, // 3ei5da9ve2h8
)

data class SqueezedOfflineDtoResponse(
    @SerializedName("content")
    var content: String?, // {"operator":"2x2qlr88wdcz","target":"3ei5da9ve2h8","temId":"agree-add-friend"}
    @SerializedName("id")
    var id: String? = "", // 19b42081ee2a3ad8
    @SerializedName("tag")
    var tag: Int? = 1 // 19b42081ee2a3ad8
)

data class SqueezedOfflineDto(
    @SerializedName("device_name")
    var deviceName: String? = ""
)


const val NEW_MESSAGE = "new_msg"
const val APPLY_FRIEND_MESSAGE = "apply_friend_msg"
const val AGREE_ADD_FRIEND = "agree_add_friend"

const val SYNC_SETTING_MUTE = "sync_setting_mute"
const val SYNC_SETTING_TOP = "sync_setting_top"
const val SYNC_UNREAD_NUM = "sync_unread_num"
const val SYNC_OFFLINE = "sync_offline"
const val SYNC_SETTING_HIDE = "sync_setting_hide"
const val ADD_BLOCK_USER = "add_block_user"
const val CANCEL_BLOCK_USER = "cancel_block_user"
