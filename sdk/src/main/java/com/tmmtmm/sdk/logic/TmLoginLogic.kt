package com.tmmtmm.sdk.logic

import com.tmmtmm.sdk.TMM
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
            tmConnectionMap[TMM::class.java.name]?.getAuth(auid) { auth ->
                login(auid, auth)
            }
            return
        }
        LoginSuccessEvent.send(auid)

    }

    fun login(auid: String,auth: String) {
        try {
            TransferThreadPool.submitTask {
                val request = GetAuthRequest(
                    auid = auid,
                    akey = getAk(),
                    authcode = auth,
                )
                GetAuth.execute(request).onSuccess { loginResponse ->
                    val uid = loginResponse?.userId ?: ""
                    val token = loginResponse?.token ?: ""
                    DataBaseManager.getInstance().initShare(TmUtils.sApp)
                    setUser(auid = auid, uid = uid)
                    DataBaseManager.getInstance().init(TmUtils.sApp)
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
        fun getAuth(
            auid: String,
            resolve: ((auth: String) -> Unit)
        )
    }

}