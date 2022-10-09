package com.tmmtmm.sdk

import android.app.Activity
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.Utils
import com.tmmtmm.sdk.cache.LoginCache
import com.tmmtmm.sdk.core.db.DataBaseManager
import com.tmmtmm.sdk.core.id.ChatId
import com.tmmtmm.sdk.core.net.ResponseResult
import com.tmmtmm.sdk.core.net.config.Net
import com.tmmtmm.sdk.core.net.service.ApiBaseService
import com.tmmtmm.sdk.core.utils.TmUtils
import com.tmmtmm.sdk.core.utils.TransferThreadPool
import com.tmmtmm.sdk.db.event.LoginSuccessEvent
import com.tmmtmm.sdk.logic.TmGroupLogic
import com.tmmtmm.sdk.logic.TmLoginLogic
import com.tmmtmm.sdk.logic.TmMessageLogic
import java.util.concurrent.ConcurrentHashMap

/**
 * @description
 * @version
 */
class ImSDK private constructor(val ak: String, val env: String) {

    private var tmConnectionMap = ConcurrentHashMap<String, TmDelegate>()


    companion object {
        private var instance: ImSDK? = null

        @JvmName("getInstance")
        fun getInstance(ak: String, env: String): ImSDK {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ImSDK(ak, env)
                    }
                }
            }
            return instance!!
        }
    }

    init {
        DataBaseManager.getInstance().initShare(TmUtils.sApp, aKey = ak, env = env)
        TmLoginLogic.getInstance().setAk(ak)
    }

    fun initUser(auid: String) {
        if (TmUtils.sApp == null) {
            throw Exception()
        }
        //not login
        val userId = LoginCache.getUserId()
        if (userId.isBlank()) {
            //start to login
            tmConnectionMap[ImSDK::class.java.name]?.getAuth(auid) { auth ->
                TmLoginLogic.getInstance().login(auid, auth, this)
            }
            return
        }
        TmLoginLogic.getInstance().initUser(aKey = ak, env = env, userId = userId)
        LoginSuccessEvent.send(auid)
    }

    fun sendTextMessage(
        content: String,
        aChatId: String,
        amid: String
    ) {
        val chatId = ChatId.create(aChatId)
        TransferThreadPool.submitTask {
            TmMessageLogic.INSTANCE.sendTextMessage(content, chatId, aChatId, amid)
        }
    }

    fun setDelegate(delegate: TmDelegate) {
        tmConnectionMap[ImSDK::class.java.name] = delegate
        ApiBaseService.setDelegate(object : Net.Delegate_401 {
            override fun onTokenError(net: Net?) {
                val auid = LoginCache.getAUserId()
                delegate.getAuth(auid) { auth ->
                    TmLoginLogic.getInstance().login(auid, auth, this@ImSDK)
                }
            }
        })
    }


    fun createChat(
        aChatId: String,
        chatName: String,
        auids: MutableList<String>,
        delegate: CreateChatDelegate?
    ) {

        TransferThreadPool.submitTask {
            val result = TmGroupLogic.INSTANCE.createChat(aChatId, chatName, auids)

            if (result is ResponseResult.Success) {
                ThreadUtils.runOnUiThread {
                    delegate?.onCreateSuccess()
                }
                return@submitTask
            }

            if (result is ResponseResult.Failure) {
                ThreadUtils.runOnUiThread {
                    delegate?.onCreateFailed(
                        code = result.throwable?.code,
                        errorMsg = result.throwable?.message
                    )
                }
            }
        }
    }

    fun removeConnectionListener(key: String) {
        tmConnectionMap.remove(key)
    }

    interface CreateChatDelegate {
        fun onCreateSuccess()
        fun onCreateFailed(code: Int?, errorMsg: String?)
    }

    interface TmDelegate {
        fun getAuth(
            auid: String,
            resolve: ((auth: String) -> Unit)
        )
    }

}