package com.tmmtmm.sdk.logic

import android.app.Activity
import android.graphics.ImageDecoder
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import com.tmmtmm.sdk.ImSDK
import com.tmmtmm.sdk.api.GetAuth
import com.tmmtmm.sdk.api.GetAuthRequest
import com.tmmtmm.sdk.cache.LoginCache
import com.tmmtmm.sdk.core.db.DataBaseManager
import com.tmmtmm.sdk.core.net.config.NetFactory
import com.tmmtmm.sdk.core.net.manager.NetDbManager
import com.tmmtmm.sdk.core.net.service.ApiBaseService
import com.tmmtmm.sdk.core.utils.TmUtils
import com.tmmtmm.sdk.core.utils.TransferThreadPool
import com.tmmtmm.sdk.core.utils.onSuccess
import com.tmmtmm.sdk.db.UserDBManager
import com.tmmtmm.sdk.db.event.LoginSuccessEvent
import com.tmmtmm.sdk.db.model.UserModel

/**
 * @description
 * @version
 */
class TmLoginLogic private constructor() {

    companion object {

        private var instance: TmLoginLogic? = null

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
                }
            }

            override fun onBackground(activity: Activity?) {

            }

        })
    }

    fun login(auid: String,auth: String,imSDK: ImSDK) {
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
                    initUser(aKey = imSDK.ak, env = imSDK.env,uid)
                    setShareUser(auid = auid, uid = uid)
                    val userModel = UserModel()
                    userModel.uid = uid
                    userModel.aUid = auid
                    UserDBManager.getInstance().insertUser(userModel)
                    NetFactory.getInstance()
                        .getOrCreateNetByServiceName(
                            serviceName = ApiBaseService.getServiceName(),
                            host = ApiBaseService.getHost()
                        ).setToken(token)
                    LoginSuccessEvent.send(auid)
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