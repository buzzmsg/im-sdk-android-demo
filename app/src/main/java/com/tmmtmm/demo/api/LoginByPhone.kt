package com.tmmtmm.demo.api

import androidx.annotation.Keep
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.annotations.SerializedName
import com.tmmtmm.demo.exception.TmException
import com.tmmtmm.demo.exception.TmmError

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit


@Keep
data class LoginByPhoneRequest(
    var phone: String? = null,
)

@Keep
data class LoginByPhoneResponse(
    var code: Int? = 0, // 0
//    var ak: String? = "", // 1626879625
    @SerializedName("auid")
    var auid: String? = "", // 2x2qlr88wdcz
    var token: String? = "",
)


object LoginByPhone {

    const val host = "https://demo-sdk-test-api.rpgqp.com"
//    const val host = "https://dev-sdkdemo.tmmtmm.com.tr:7504"

    private const val api = "/login"

    fun execute(requestLoginByPhone: LoginByPhoneRequest): ResponseResult<LoginByPhoneResponse?> {
        try {
            val requestBody: RequestBody =
                requestLoginByPhone.toJson().toString()
                    .toRequestBody("application/json".toMediaType())
            val url = host + api
            val req =
                Request.Builder().url(url)
                    .post(requestBody)
                    .build()

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

            val response = okHttpClient.newCall(req).execute()

            val type =
                GsonUtils.getType(
                    BaseResponseEntity::class.java,
                    LoginByPhoneResponse::class.java,
                )
            val responseData: BaseResponseEntity<LoginByPhoneResponse>? =
                response.body?.string()?.responseToEntity(type)

            val result = if (responseData?.isSuccess() == false) {
                //error
                ResponseResult.Failure(TmException(TmmError.ERROR_COMMON))
            } else {
                //success
                ResponseResult.Success(responseData?.data)
            }

            if (result !is ResponseResult.Success) {
                return result
            }

            val status = result.value?.code
            if (status != null && status != 0) {
                return ResponseResult.Failure(TmException(status, ""))
            }

            return result
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseResult.Failure(TmException(500, ""))
        }

    }
}

