package com.tmmtmm.sdk.core.net

import android.util.Log
import com.blankj.utilcode.util.DeviceUtils
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.tmmtmm.sdk.core.net.config.Net
import com.tmmtmm.sdk.core.net.exception.TmException
import com.tmmtmm.sdk.core.net.exception.TmmError
import com.tmmtmm.sdk.core.net.id.SnowflakeDistributeId
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


/**
 * @description
 *
 * @time 2021/5/18 12:14 上午
 * @version
 */
class NetClient {

//    var networkClient: NetworkClient

    private var okHttpClient: OkHttpClient? = null

    private val JSON: MediaType = "application/json".toMediaType()

    private val idWorker: SnowflakeDistributeId

    init {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(
                LoggingInterceptor.Builder()
                    .setLevel(Level.BASIC)
                    .log(Log.VERBOSE)
                    .build()
            )
        idWorker = SnowflakeDistributeId(0, 0)

        okHttpClient = okHttpClientBuilder.build()
//        networkClient = NetworkClient.Builder()
//            .init(url = "http://129.226.123.108:8004/", okHttpClientBuilder = okHttpClientBuilder)
//            .addEncryptCallback(RequestEncryptConfig())
//            .addDecryptCallback(ResponseDecryptConfig())
//            .init(url = "http://192.168.100.198:8004/",okHttpClientBuilder = okHttpClientBuilder)
//            .addRequestHeadersCallback(RequestHeaderConfig())
//            .addLoggingInterceptor(
//                LoggingInterceptor.Builder()
//                    .setLevel(Level.BASIC)
//                    .log(Log.VERBOSE)
//                    .build()
//            )
//            .build()
    }

    fun getOkhttpClient(): OkHttpClient? {
        return okHttpClient
    }

    fun <T> apiCore(net: Net, data: String?, path: String, type: Type): ResponseResult<T?> {
        if (net.mIs401) {
            return ResponseResult.Failure(TmException(TmmError.ERROR_TOKEN))
        }

        val token = net.getToken()
        if (token == null || token == EMPTY_TOKEN) {
            deal401State(net)
            return ResponseResult.Failure(TmException(TmmError.ERROR_TOKEN))
        }
        try {
            val requestBody: RequestBody? = data?.toRequestBody(JSON)
            val builder = Request.Builder()
            builder.url(net.getHost() + path)
            builder.addHeader("token", token)
            builder.addHeader("req-id", idWorker.nextId().toString())
//            builder.addHeader("version", BuildConfig.VERSION_NAME)
            builder.addHeader("over", "Android${DeviceUtils.getSDKVersionName()}")
//            builder.addHeader("lang", TmLanguageUtil.getCurrentLanguage())
            builder.addHeader("os", "android")
            builder.addHeader("Content-Type", "application/json")
            builder.addHeader("device-name", android.os.Build.BRAND + " " + android.os.Build.MODEL)
            if (requestBody != null) {
                builder.post(requestBody)
            }

            val response = okHttpClient?.newCall(builder.build())?.execute()
            if (response?.isSuccessful == false) {
                return ResponseResult.Failure(TmException(TmmError.ERROR_COMMON))
            }

            val responseData: BaseResponseEntity<T>? =
                response?.body?.string()?.responseToEntity(type)
            if (responseData?.isSuccess() == true) {
                //success code 200
                net.clear401State()
                return ResponseResult.Success(responseData.data)
            }

            return if (responseData?.code != TmmError.ERROR_TOKEN.code) {
                //other error code
                net.clear401State()
                ResponseResult.Failure(TmException(responseData?.code, responseData?.msg))
            } else {
                //token error code 401
                if (token == net.getToken()) {
                    deal401State(net)
                    ResponseResult.Failure(TmException(responseData.code, responseData.msg))
                } else {
                    //fake token
                    ResponseResult.Failure(TmException(TmmError.ERROR_COMMON))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return TmException.parse(e)
        }
    }

    fun <T> apiNoTokenCore(net: Net, data: String?, path: String, type: Type): ResponseResult<T?> {
        try {
            val requestBody: RequestBody? = data?.toRequestBody(JSON)
            val builder = Request.Builder()
            builder.url(net.getHost() + path)
//            builder.addHeader("version", BuildConfig.VERSION_NAME)
            builder.addHeader("req-id", idWorker.nextId().toString())
            builder.addHeader("over", "Android${DeviceUtils.getSDKVersionName()}")
//            builder.addHeader("lang", TmLanguageUtil.getCurrentLanguage())
            builder.addHeader("os", "android")
            builder.addHeader("Content-Type", "application/json")
            builder.addHeader("device-name", android.os.Build.BRAND + " " + android.os.Build.MODEL)
            if (requestBody != null) {
                builder.post(requestBody)
            }
            val response = okHttpClient?.newCall(builder.build())?.execute()
            if (response?.isSuccessful == false) {
                return ResponseResult.Failure(TmException(TmmError.ERROR_COMMON))
            }

            val responseData: BaseResponseEntity<T>? =
                response?.body?.string()?.responseToEntity(type)
            return if (responseData?.isSuccess() == false) {
                //error
                ResponseResult.Failure(TmException(TmmError.ERROR_COMMON))
            } else {
                //success
                ResponseResult.Success(responseData?.data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return TmException.parse(e)
//            return ResponseResult.Failure(TmException(TmmError.ERROR_COMMON.code, e.message))
        }
    }

    private fun deal401State(net: Net) {
        if (net.mIs401) {
            return
        }
        net.set401State(true)
//        TmLoginManager.exceptionLogout()
        net.get401Delegate()?.onTokenError(net)
        return
    }

    companion object {

        private var instance: NetClient? = null

        const val EMPTY_TOKEN = ""

        @JvmName("getInstance1")
        fun getInstance(): NetClient {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = NetClient()
                    }
                }
            }
            return instance!!
        }
    }
}