package com.im.sdk.message.content

import android.os.Parcelable
import androidx.annotation.Keep
import com.im.sdk.dto.TmMessage
import com.im.sdk.message.core.ContentTag
import kotlinx.parcelize.Parcelize


@Parcelize
@Keep
open class TmMessageContent : Parcelable {
    open fun decode(payload: String?) {

    }

    open fun digest(message: TmMessage?): String {
        return ""
    }

    fun getMessageContentType(): Int {
        val tag = javaClass.getAnnotation(ContentTag::class.java)
        return tag?.type ?: -1
    }
}