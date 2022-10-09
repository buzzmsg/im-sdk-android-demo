package com.tmmtmm.sdk.core.net.websocket

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.DeviceUtils
import com.tmmtmm.sdk.BuildConfig
import com.tmmtmm.sdk.core.net.service.ApiBaseService
import com.tmmtmm.sdk.core.utils.LogUtils
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit
import kotlin.collections.set

class WebSocketManager private constructor() : LifecycleEventObserver {

    private val maxNum = Int.MAX_VALUE
    private var connectNum: Int = 0

    //    private val millis: Long = 5 * 1000 * (connectNum + 1).toLong()
    private val millis: Long = TimeUnit.SECONDS.toMillis(1)

    private var isLogout = false
    private lateinit var request: Request
    private var mWebSocket: WebSocket? = null
    private var iReceiveMessageImpMap = hashMapOf<String, IReceiveMessageImpl>()
    private var defaultReceiveMessageKey = "default"
    private var isConnect: Boolean = false
    private var sendTime: Long = 0L
    private val heartTime: Long = 40 * 1000

    private var wsURL = ""


//    private var mHandler: Handler? = null
//    private val heartRunnable = Runnable {
//        if (System.currentTimeMillis() - sendTime >= heartTime) {
//            sendTime = System.currentTimeMillis()
//            val isSend = sendMessage("App")
//            LogUtils.e("heart is send$isSend")
//        }
//    }

    private var client: OkHttpClient? = null


    companion object {
        private var instance: WebSocketManager? = null

        private const val TAG = "WebSocketManager"

        @JvmName("getInstance1")
        fun getInstance(): WebSocketManager {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = WebSocketManager()
                    }
                }
            }
            return instance!!
        }
    }

    fun initWebSocket(): WebSocketManager? {
//        isLogout = false
        if (client != null) return this
        val currentToken = ApiBaseService.getCurrentToken()
        if (currentToken.isNullOrBlank()) {
            return null
        }

        client = OkHttpClient.Builder()
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .pingInterval(30, TimeUnit.SECONDS)
            .build()
        wsURL =
            BuildConfig.WS_HOST + "?ter_type=android&over=Android${DeviceUtils.getSDKVersionName()}&version=${BuildConfig.VERSION_NAME}&token=$currentToken"
        request = Request.Builder().url(wsURL).build()
        return this
    }

    fun addListener(
        lifecycleOwner: LifecycleOwner? = null,
        iReceiveMessageImpl: IReceiveMessageImpl
    ): WebSocketManager {
        if (lifecycleOwner == null) {
            iReceiveMessageImpMap[defaultReceiveMessageKey] = iReceiveMessageImpl
        } else {
            lifecycleOwner.lifecycle.addObserver(this)
            iReceiveMessageImpMap[lifecycleOwner.hashCode().toString()] = iReceiveMessageImpl
        }
        return this
    }

    fun addListener(
        key: String,
        iReceiveMessageImpl: IReceiveMessageImpl
    ): WebSocketManager {
        iReceiveMessageImpMap[key] = iReceiveMessageImpl
        return this
    }

    fun connect() {
        iReceiveMessageImpMap.forEach { map ->
            map.value.onConnecting()
        }

        if (isConnect()) {
            LogUtils.getInstance().w(content = "WebSocket connected")
            return
        }
        client?.newWebSocket(request, createListener())
    }

    fun reconnect() {
        if (connectNum <= maxNum) {
            try {
                Thread.sleep(millis)
                connect()
                connectNum++
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        } else {
            LogUtils.getInstance()
                .w(tag = TAG, "reconnect over $maxNum,please check url or network")
        }
    }

    fun isConnect(): Boolean {
        return mWebSocket != null && isConnect
    }

    fun sendMessage(content: String): Boolean {
        if (!isConnect()) return false
        return mWebSocket?.send(content) ?: false
    }

    fun sendMessage(content: ByteString): Boolean {
        if (!isConnect()) return false
        return mWebSocket?.send(content) ?: false
    }

    fun close() {
//        if (isConnect()) {
        iReceiveMessageImpMap.clear()
//        isLogout = true
        mWebSocket?.cancel()
        mWebSocket?.close(1001, "The Client close connect")
        client = null
//        }
//        if (mHandler != null) {
//            mHandler?.removeCallbacksAndMessages(null)
//            mHandler = null
//        }
    }

    fun createListener(): WebSocketListener {
        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                LogUtils.getInstance().w(TAG, "webSocket onOpen response = $response")

                LogUtils.getInstance().w("webSocket onOpen", content = response.toString())

                mWebSocket = webSocket
                isConnect = response.code == 101
                if (!isConnect) {
                    reconnect()
                } else {
                    connectNum = 0
                    LogUtils.getInstance().w(TAG, "webSocket connect success")
                    iReceiveMessageImpMap.forEach { map ->
                        map.value.onConnectSuccess()
                    }

//                    if (sendMessage("App")) {
//                        mHandler?.postDelayed(heartRunnable, heartTime)
//                    }
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                LogUtils.getInstance().w(TAG, "webSocket onMessage text = $text")

                LogUtils.getInstance().w("webSocket onMessage", content = text)
                iReceiveMessageImpMap.forEach { map ->
                    map.value.onMessage(text)
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                LogUtils.getInstance().w(TAG, "webSocket onMessage")
                iReceiveMessageImpMap.forEach { map ->
                    map.value.onMessage(bytes.base64())
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                LogUtils.getInstance().w(TAG, "webSocket onClosing reason = $reason")

                LogUtils.getInstance()
                    .w("webSocket onClosing", content = "code: $code   reason = $reason")

                mWebSocket = null
                isConnect = false
//                if (mHandler != null) {
//                    mHandler?.removeCallbacksAndMessages(null)
//                }

                iReceiveMessageImpMap.forEach { map ->
                    map.value.onClose()
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                LogUtils.getInstance().w(TAG, "webSocket onClosed reason = $reason")

                LogUtils.getInstance()
                    .w("webSocket onClosed", content = "code: $code   reason = $reason")

                mWebSocket = null
                isConnect = false
//                if (mHandler != null) {
//                    mHandler?.removeCallbacksAndMessages(null)
//                }
                iReceiveMessageImpMap.forEach { map ->
                    map.value.onClose()
                }

            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                if (response != null) {
                    LogUtils.getInstance()
                        .w(TAG, "webSocket onFailure message response = ${response.message}")

                    LogUtils.getInstance()
                        .w("webSocket onFailure", content = "response message: ${response.message}")
                }
                LogUtils.getInstance()
                    .w(TAG, "webSocket onFailure message throwable= ${t.message}")
                LogUtils.getInstance()
                    .w("webSocket onFailure", content = "throwable message: ${t.message}")
                isConnect = false
//                if (mHandler != null) {
//                    mHandler?.removeCallbacksAndMessages(null)
//                }

                iReceiveMessageImpMap.forEach { map ->
                    map.value.onConnectFailed()
                }
//                if (!StringUtils.isEmpty(t.message) && t.message != "Socket closed") {
//                if (!isLogout) {
                reconnect()
//                }
//                }
            }
        }

        return listener
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
//            Lifecycle.Event.ON_CREATE -> mFullLifecycleObserver.onCreate(source)
//            Lifecycle.Event.ON_START -> mFullLifecycleObserver.onStart(source)
//            Lifecycle.Event.ON_RESUME -> mFullLifecycleObserver.onResume(source)
//            Lifecycle.Event.ON_PAUSE -> mFullLifecycleObserver.onPause(source)
//            Lifecycle.Event.ON_STOP -> mFullLifecycleObserver.onStop(source)
            Lifecycle.Event.ON_DESTROY -> {
                if (iReceiveMessageImpMap.keys.contains(source.hashCode().toString())) {
                    iReceiveMessageImpMap.remove(source.hashCode().toString())
                }
            }
            else -> {}
        }
    }
}
