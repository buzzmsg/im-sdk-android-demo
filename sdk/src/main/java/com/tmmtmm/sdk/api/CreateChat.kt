package com.tmmtmm.sdk.api

import androidx.annotation.Keep
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.annotations.SerializedName
import com.tmmtmm.sdk.core.net.BaseResponseEntity
import com.tmmtmm.sdk.core.net.ResponseResult
import com.tmmtmm.sdk.core.net.config.BUSINESS_SUCCESS_CODE
import com.tmmtmm.sdk.core.net.exception.TmException
import com.tmmtmm.sdk.core.net.service.ApiBaseService
import com.tmmtmm.sdk.core.net.service.ApiNoTokenService
import com.tmmtmm.sdk.core.net.toJson

/**
 * @description
 * @version
 */

@Keep
data class CreateChatRequest(
    var id: String? = null,
    @SerializedName("achat_id")
    var aChatId: String? = null,
    var name: String? = null,
    var auids: MutableList<String>? = null,
    var type: Int = CreateChat.CHAT_SINGLE_TYPE
)

@Keep
data class CreateChatResponse(
    @SerializedName("err_code")
    var errorCode: Int? = 0,
    @SerializedName("err_msg")
    var errorMsg: String? = ""

)

object CreateChat {

    const val CHAT_SINGLE_TYPE = 1
    const val CHAT_GROUP_TYPE = 2

    private const val CREATE_CHAT_API = "/createChat"

    fun excute(request: CreateChatRequest): ResponseResult<CreateChatResponse?> {
        val type =
            GsonUtils.getType(
                BaseResponseEntity::class.java,
                CreateChatResponse::class.java,
            )
        val result = ApiBaseService.post<CreateChatResponse>(
            request.toJson().toString(),
            CREATE_CHAT_API,
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