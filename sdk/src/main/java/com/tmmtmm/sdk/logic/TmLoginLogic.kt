package com.tmmtmm.sdk.logic

import com.tmmtmm.sdk.TMM
import com.tmmtmm.sdk.api.Login
import com.tmmtmm.sdk.api.LoginRequest
import com.tmmtmm.sdk.cache.LoginCache
import com.tmmtmm.sdk.core.db.DataBaseManager
import com.tmmtmm.sdk.core.net.config.Net
import com.tmmtmm.sdk.core.net.config.NetFactory
import com.tmmtmm.sdk.core.net.manager.NetDbManager
import com.tmmtmm.sdk.core.net.service.ApiBaseService
import com.tmmtmm.sdk.core.utils.TransferThreadPool
import com.tmmtmm.sdk.core.utils.onSuccess
import com.tmmtmm.sdk.db.UserDBManager
import com.tmmtmm.sdk.db.event.LoginSuccessEvent
import com.tmmtmm.sdk.db.model.UserLinkModel
import com.tmmtmm.sdk.db.model.UserModel
import java.util.concurrent.ConcurrentHashMap

/**
 * @description
 * @version
 */
class TmLoginLogic private constructor() {

    private var tmConnectionMap = ConcurrentHashMap<String, TmConnectionDelegate>()

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

    fun setUser(auid: String, uid: String) {
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

    fun initUser(auid: String) {
        if (getUserId().isBlank()) {
            //start to login
            tmConnectionMap[TMM::class.java.name]?.onConnectLost(auid) { time, nonce, signature ->
                login(auid, time, nonce, signature)
            }
            return
        }
        LoginSuccessEvent.send(auid)

    }

    fun login(auid: String, time: Long, nonce: String, signature: String) {
        try {
            TransferThreadPool.submitTask {
                val request = LoginRequest(
                    auid = auid,
                    akey = getAk(),
                    nonce = nonce,
                    timestamp = time,
                    signature = signature
                )
                Login.execute(request).onSuccess { loginResponse ->
                    val uid = loginResponse?.userId ?: ""
                    val token = loginResponse?.token ?: ""
                    setUser(auid = auid, uid = uid)
                    val userLinkModel = UserLinkModel()
                    userLinkModel.aUid = auid
                    userLinkModel.uid = uid
                    UserDBManager.getInstance().insertUserLink(userLinkModel)

                    val userModel = UserModel()
                    userModel.uid = uid
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

//        setUser("")
        NetDbManager.getInstance().clearAll()
        DataBaseManager.getInstance().close()


    }

    fun addConnectionListener(
        key: String,
        tmConnectionListenerImpl: TmConnectionDelegate
    ) {
        tmConnectionMap[key] = tmConnectionListenerImpl
    }

    fun removeConnectionListener(key: String) {
        tmConnectionMap.remove(key)
    }

    interface TmConnectionDelegate {
        fun onConnectLost(
            auid: String,
            resolve: ((time: Long, nonce: String, signature: String) -> Unit)
        )
    }

}