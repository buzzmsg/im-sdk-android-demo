package com.tmmtmm.sdk.message.content

import android.os.Parcelable
import androidx.annotation.Keep
import com.tmmtmm.sdk.constant.MessageContentType
import com.tmmtmm.sdk.core.net.toEntity
import com.tmmtmm.sdk.dto.TmMessage
import com.tmmtmm.sdk.message.core.ContentTag
import kotlinx.parcelize.Parcelize

@ContentTag(type = MessageContentType.ContentType_Text)
@Parcelize
@Keep
class TmTextMessageContent(
    var text: String? = null,
//    var translate: String? = null,
//    var quoteJson: String? = null
    var lang: String? = null,
) : TmMessageContent(), Parcelable {

    override fun decode(payload: String?) {
        val contentText: TmTextMessageContent? = payload?.toEntity()
        text = contentText?.text
        lang = contentText?.lang
    }

    override fun digest(message: TmMessage?): String {
        return text ?: ""
    }

}


const val EXCLUSION_TRANSLATION = "translation"
const val EXCLUSION_TRANSLATE_STATUS = "translateStatus"
const val EXCLUSION_TRANSLATE_LANG = "lang"
const val EXCLUSION_TRANSLATE_HIDE = "isTranslationHide"

const val TRANSLATE_SUCCESS = 1
const val TRANSLATE_FAILED = 0
const val TRANSLATE_LOADING = 2

const val TRANSLATE_HIDE = 1
const val TRANSLATE_NORMAL = 0