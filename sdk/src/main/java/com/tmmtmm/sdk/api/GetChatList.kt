package com.tmmtmm.sdk.api

import androidx.annotation.Keep
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.tmmtmm.sdk.core.net.BaseResponseEntity
import com.tmmtmm.sdk.core.net.ResponseResult
import com.tmmtmm.sdk.core.net.config.BUSINESS_SUCCESS_CODE
import com.tmmtmm.sdk.core.net.exception.TmException
import com.tmmtmm.sdk.core.net.service.ApiBaseService
import com.tmmtmm.sdk.core.net.toJson

/**
 * @description
 *
 * @time 2021/5/18 12:34 上午
 * @version
 */

@Keep
data class GetChatListRequest(
    var ids: MutableSet<String>
)

@Keep
data class GetChatListResponse(
    var items: List<GetChatListItemResponse>? = null,
    @SerializedName("err_code")
    var errorCode: Int? = 0,
    @SerializedName("err_msg")
    var errorMsg: String? = ""
)

@Keep
data class GetChatListItemResponse(
    @SerializedName("id")
    var chatId: String? = null,
    var aChatId: String? = null,
    var name: String? = null,
)

object GetChatList {
    private const val API = "/getChatList"

    fun execute(jsonObject: GetChatListRequest): ResponseResult<GetChatListResponse?> {
        val type = GsonUtils.getType(
            BaseResponseEntity::class.java,
            GetChatListResponse::class.java,
            GetChatListItemResponse::class.java
        )
        val result = ApiBaseService.post<GetChatListResponse?>(
            jsonObject.toJson().toString(),
            API,
            type
        )


        if (result !is ResponseResult.Success) {
            return result
        }

        val status = result.value?.errorCode
        if (status != null && status != BUSINESS_SUCCESS_CODE) {
            return ResponseResult.Failure(TmException(status, ""))
        }

        return result
    }
}





