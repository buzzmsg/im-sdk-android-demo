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
data class SendCardAndTempMessageRequest(
    @SerializedName("achat_id")
    var aChatId: String? = null,
    @SerializedName("sender_id")
    var senderId: String? = null,
    @SerializedName("sender_time")
    var sendTime: Long? = null,
)

@Keep
data class SendCardAndTempMessageResponse(
    @SerializedName("err_code")
    var errCode: Int?,
)


object SendCardAndTempMessage {

    const val host = "https://dev-sdkdemo.imtmm.com:7504"
//    const val host = "https://dev-sdkdemo.tmmtmm.com.tr:7504"

    private const val api = "/sendCardAndTempMessage"

    fun execute(sendCardAndTempMessageRequest: SendCardAndTempMessageRequest): ResponseResult<SendCardAndTempMessageResponse?> {
        try {
            val requestBody: RequestBody =
                sendCardAndTempMessageRequest.toJson().toString()
                    .toRequestBody("application/json".toMediaType())
            val url = host + api
            val req =
                Request.Builder().url(url)
                    .post(requestBody)
                    .addHeader("token", " ")
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
                    SendCardAndTempMessageResponse::class.java,
                )
            val responseData: BaseResponseEntity<SendCardAndTempMessageResponse>? =
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

            val status = result.value?.errCode
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

