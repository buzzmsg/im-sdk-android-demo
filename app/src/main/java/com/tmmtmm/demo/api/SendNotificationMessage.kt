package com.tmmtmm.demo.api

import androidx.annotation.Keep
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.annotations.SerializedName
import com.tmmtmm.demo.BuildConfig
import com.tmmtmm.demo.exception.TmException
import com.tmmtmm.demo.exception.TmmError
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit


@Keep
data class SendNotificationMessageRequest(
    @SerializedName("amid")
    var amid: String? = null,
    @SerializedName("achat_id")
    var aChatId: String? = null,
    @SerializedName("sender_id")
    var senderId: String? = null,
    @SerializedName("sender_time")
    var sendTime: Long? = null,
//    @SerializedName("receive_ids")
//    var receiveIds: MutableList<String>? = null,
//    @SerializedName("content")
//    var content: String? = null
)

@Keep
data class SendNotificationMessageResponse(
    @SerializedName("err_code")
    var errCode: Int?,
)


object SendNotificationMessage {

//    const val host = "https://demo-sdk-test-api.rpgqp.com"
//    const val host = "https://dev-sdkdemo.tmmtmm.com.tr:7504"

    private const val api = "/sendNotificationMessage"

    fun execute(sendNotificationMessageRequest: SendNotificationMessageRequest): ResponseResult<SendNotificationMessageResponse?> {
        try {
            val requestBody: RequestBody =
                sendNotificationMessageRequest.toJson().toString()
                    .toRequestBody("application/json".toMediaType())
            val url = BuildConfig.DEMO_TEST_API + api
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
                    SendNotificationMessageResponse::class.java,
                )
            val responseData: BaseResponseEntity<SendNotificationMessageResponse>? =
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

