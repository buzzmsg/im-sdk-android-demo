package com.im.sdk.logic

import android.app.Activity
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.Utils
import com.im.sdk.IMSdk
import com.im.sdk.api.GetAuth
import com.im.sdk.api.GetAuthRequest
import com.im.sdk.cache.LoginCache
import com.im.sdk.core.db.DataBaseManager
import com.im.sdk.core.net.config.NetFactory
import com.im.sdk.core.net.manager.NetDbManager
import com.im.sdk.core.net.service.ApiBaseService
import com.im.sdk.core.net.websocket.IReceiveMessageImpl
import com.im.sdk.core.net.websocket.WebSocketManager
import com.im.sdk.core.utils.TmUtils
import com.im.sdk.core.utils.TransferThreadPool
import com.im.sdk.core.utils.onError
import com.im.sdk.core.utils.onSuccess
import com.im.sdk.db.UserDBManager
import com.im.sdk.db.event.LoginSuccessEvent
import com.im.sdk.db.model.UserModel

/**
 * @description
 * @version
 */
class TmLoginLogic private constructor() {

    private var loginRetryTimes = 0

    companion object {

        private var instance: TmLoginLogic? = null

        private const val RETRY_TIMES = 3

        private const val RETRY_DELAY_MILES = 500L

        @JvmName("getInstance")
        fun getInstance(): TmLoginLogic {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = TmLoginLogic()
                    }
                }
            }
            return instance!!
        }
    }

    fun getUserId(): String {
        return LoginCache.getUserId()
    }

    fun setShareUser(auid: String, uid: String) {
        LoginCache.setUser(auid, uid)
    }

    fun isLogin(): Boolean {
        return getUserId().isNotBlank()
    }

    fun getAk(): String {
        return LoginCache.getAKey()
    }

    fun setAk(ak: String) {
        LoginCache.setAKey(ak)
    }

    fun getEnv(): String {
        return LoginCache.getEnv()
    }

    fun setEnv(env: String) {
        LoginCache.setEnv(env)
    }

    fun initUser(aKey: String, env: String, userId: String) {
        DataBaseManager.getInstance().init(TmUtils.sApp, aKey = aKey, env = env, userId = userId)
        AppUtils.registerAppStatusChangedListener(object : Utils.OnAppStatusChangedListener {
            override fun onForeground(activity: Activity?) {
                TransferThreadPool.submitTask {
                    TmMessageLogic.INSTANCE.receiveMessage()
                    TmMessageLogic.INSTANCE.retrySendMessages()
                }
            }

            override fun onBackground(activity: Activity?) {

            }

        })

        WebSocketManager.getInstance().initWebSocket()
            ?.addListener(
                null,
                iReceiveMessageImpl = object : IReceiveMessageImpl() {
                    override fun onMessage(content: String) {
                        TransferThreadPool.submitTask {
                            TmMessageLogic.INSTANCE.receiveMessage()

                        }
                    }
                })?.connect()

        TmNetWorkStatusLogic.getInstance().registerNetworkStatus(TmUtils.sApp)
        TransferThreadPool.submitTask {
            TmMessageLogic.INSTANCE.receiveMessage()
            TmMessageLogic.INSTANCE.retrySendMessages()
        }
    }

    fun login(auid: String, auth: String, imSDK: IMSdk) {
        try {
            TransferThreadPool.submitTask {
                val request = GetAuthRequest(
                    auid = auid,
                    ak = imSDK.ak,
                    authcode = auth,
                )
                GetAuth.execute(request).onSuccess { loginResponse ->
                    val uid = loginResponse?.userId ?: ""
                    val token = loginResponse?.token ?: ""
                    NetFactory.getInstance()
                        .getOrCreateNetByServiceName(
                            serviceName = ApiBaseService.getServiceName(),
                            host = ApiBaseService.getHost()
                        ).setToken(token)

                    initUser(aKey = imSDK.ak, env = imSDK.env, uid)
                    setShareUser(auid = auid, uid = uid)
                    val userModel = UserModel()
                    userModel.uid = uid
                    userModel.aUid = auid
                    UserDBManager.getInstance().insertUser(userModel)
                    LoginSuccessEvent.send(auid)
                    loginRetryTimes = 0
                }.onError { code, msg ->
                    loginRetryTimes += 1
                    if (loginRetryTimes >= RETRY_TIMES) {
                        return@onError
                    }
                    val sleepDuration = RETRY_DELAY_MILES * loginRetryTimes
                    Thread.sleep(sleepDuration)
                    login(auid, auth, imSDK)
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun logout(aUid: String) {
        NetDbManager.getInstance().clearAll()
        DataBaseManager.getInstance().close()
    }

}