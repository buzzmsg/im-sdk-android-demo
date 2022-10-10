package com.im.sdk

import android.app.Application
import com.blankj.utilcode.util.ThreadUtils
import com.im.sdk.cache.LoginCache
import com.im.sdk.core.db.DataBaseManager
import com.im.sdk.core.id.ChatId
import com.im.sdk.core.net.ResponseResult
import com.im.sdk.core.net.config.Net
import com.im.sdk.core.net.service.ApiBaseService
import com.im.sdk.core.utils.TmUtils
import com.im.sdk.core.utils.TransferThreadPool
import com.im.sdk.db.event.LoginSuccessEvent
import com.im.sdk.logic.TmGroupLogic
import com.im.sdk.logic.TmLoginLogic
import com.im.sdk.logic.TmMessageLogic
import java.util.concurrent.ConcurrentHashMap

/**
 * @description
 * @version
 */
class IMSdk private constructor(val context: Application, val ak: String, val env: String) {

    private var tmConnectionMap = ConcurrentHashMap<String, ImDelegate>()


    companion object {
        private var instance: IMSdk? = null

        @JvmName("getInstance")
        fun getInstance(context: Application, ak: String, env: String): IMSdk {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = IMSdk(context, ak, env)
                    }
                }
            }
            return instance!!
        }
    }

    init {
        TmUtils.init(context)
        DataBaseManager.getInstance().initShare(TmUtils.sApp, aKey = ak, env = env)
        TmLoginLogic.getInstance().setAk(ak)
    }

    fun initUser(auid: String) {
        if (TmUtils.sApp == null) {
            throw Exception()
        }
        val userId = LoginCache.getUserId()
        if (userId.isBlank()) {
            //not login,start to login
            tmConnectionMap[IMSdk::class.java.name]?.getAuth(auid) { auth ->
                TmLoginLogic.getInstance().login(auid, auth, this)
            }
            return
        }
        //user is login , init user db
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

    fun setDelegate(delegate: ImDelegate) {
        tmConnectionMap[IMSdk::class.java.name] = delegate
        ApiBaseService.setDelegate(object : Net.Delegate_401 {
            override fun onTokenError(net: Net?) {
                val auid = LoginCache.getAUserId()
                delegate.getAuth(auid) { auth ->
                    TmLoginLogic.getInstance().login(auid, auth, this@IMSdk)
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
                    delegate?.onSucc()
                }
                return@submitTask
            }

            if (result is ResponseResult.Failure) {
                ThreadUtils.runOnUiThread {
                    delegate?.onError(
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
        fun onSucc()
        fun onError(code: Int?, errorMsg: String?)
    }

    interface ImDelegate {
        fun getAuth(
            auid: String,
            resolve: ((auth: String) -> Unit)
        )
    }

}