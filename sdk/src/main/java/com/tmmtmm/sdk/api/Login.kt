package com.tmmtmm.sdk.api

import androidx.annotation.Keep
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.annotations.SerializedName
import com.tmmtmm.sdk.core.net.BaseResponseEntity
import com.tmmtmm.sdk.core.net.ResponseResult
import com.tmmtmm.sdk.core.net.config.BUSINESS_SUCCESS_CODE
import com.tmmtmm.sdk.core.net.exception.TmException
import com.tmmtmm.sdk.core.net.service.ApiNoTokenService
import com.tmmtmm.sdk.core.net.toJson

@Keep
data class LoginRequest(
    var aUid: String? = null,
    var key: String? = null
)

@Keep
data class LoginResponse(
    @SerializedName("items")
    var items: ItemsDto? = ItemsDto(),

    var status: Int? = BUSINESS_SUCCESS_CODE
)

@Keep
data class ItemsDto(
    @SerializedName("code")
    var code: Int? = 0, // 0
    @SerializedName("expires_in")
    var expiresIn: Int? = 0, // 1626879625
    @SerializedName("id")
    var id: String? = "", // 2x2qlr88wdcz
    @SerializedName("language")
    var language: String? = "",
    @SerializedName("refresh_token")
    var refreshToken: String? = "", // 0B57B8B476532038339F2C82DF9E7081
    @SerializedName("token")
    var token: String? = "" // C32716F237ABFD43D298C276E1F0B30FMngycWxyODh3ZGN6
)

object Login {

    private const val ERROR_CAPTCHA_UNAVAILABLE = 400017
    private const val LOGIN_API = "/Login"

    fun execute(requestLogin: LoginRequest): ResponseResult<LoginResponse?> {

        val type =
            GsonUtils.getType(
                BaseResponseEntity::class.java,
                LoginResponse::class.java,
                ItemsDto::class.java
            )
        val result = ApiNoTokenService.post<LoginResponse>(
            requestLogin.toJson().toString(),
            LOGIN_API,
            type
        )

        if (result !is ResponseResult.Success) {
            return result
        }

        val status = result.value?.status
        if (status != null && status != BUSINESS_SUCCESS_CODE){
            return if (status == ERROR_CAPTCHA_UNAVAILABLE ){
                ResponseResult.Failure(TmException(status, ""))
            } else {
                ResponseResult.Failure(TmException(status,""))
            }
        }

        return result
    }
}

