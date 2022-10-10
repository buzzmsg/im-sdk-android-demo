package com.im.sdk.message.content

import android.os.Parcelable
import androidx.annotation.Keep
import com.im.sdk.constant.MessageContentType
import com.im.sdk.dto.TmMessage
import com.im.sdk.message.core.ContentTag
import kotlinx.parcelize.Parcelize

@Parcelize
@ContentTag(type = MessageContentType.ContentType_Unknown)
@Keep
class UnknownMessageContent : TmMessageContent(), Parcelable {
    var originalPayload: String? = null

    override fun decode(payload: String?) {
        originalPayload = payload
    }

    override fun digest(message: TmMessage?): String {
//        return "Unknown type message(" + (if (orignalPayload != null) orignalPayload else "") + ")"
//        return "[${StringUtils.getString(R.string.string_message_not_supported)}]"
//        return StringUtils.getString(R.string.unknown_message)
        return "unknown_message"
    }
}