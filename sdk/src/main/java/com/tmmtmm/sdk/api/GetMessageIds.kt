package com.tmmtmm.sdk.api

import androidx.annotation.Keep
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.tmmtmm.sdk.core.net.BaseResponseEntity
import com.tmmtmm.sdk.core.net.ResponseResult
import com.tmmtmm.sdk.core.net.service.ApiBaseService
import com.tmmtmm.sdk.core.net.toJson

/**
 * @description
 *
 * @time 2021/5/18 12:34 上午
 * @version
 */

@Keep
data class SequenceListRequest(
    var sequence: Long
)

@Keep
data class MessageSequenceResponse(
    var items: List<MessageSequenceItemResponse>? = null
)

@Keep
data class MessageSequenceItemResponse(
    @SerializedName("id")
    var mid: String? = null,
    var sequence: Long? = null,
    @SerializedName("is_read")
    var isRead: Int? = 0,
    var status:Int? = 0
)

object GetMessageIds {
    private const val API = "/getMessageIds"

    fun fetch(jsonObject: SequenceListRequest): ResponseResult<MessageSequenceResponse?> {
        val type = GsonUtils.getType(
            BaseResponseEntity::class.java,
            MessageSequenceResponse::class.java,
            MessageSequenceItemResponse::class.java
        )
        val result = ApiBaseService.post<MessageSequenceResponse?>(
            jsonObject.toJson().toString(),
            API,
            type
        )
        return result
    }
}





