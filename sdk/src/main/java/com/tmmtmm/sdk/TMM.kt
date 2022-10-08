package com.tmmtmm.sdk

import android.app.Application
import com.blankj.utilcode.util.ThreadUtils
import com.tmmtmm.sdk.cache.LoginCache
import com.tmmtmm.sdk.core.db.DataBaseManager
import com.tmmtmm.sdk.core.id.ChatId
import com.tmmtmm.sdk.core.net.ResponseResult
import com.tmmtmm.sdk.core.net.config.Net
import com.tmmtmm.sdk.core.net.service.ApiBaseService
import com.tmmtmm.sdk.core.utils.TmUtils
import com.tmmtmm.sdk.core.utils.TransferThreadPool
import com.tmmtmm.sdk.logic.TmGroupLogic
import com.tmmtmm.sdk.logic.TmLoginLogic
import com.tmmtmm.sdk.logic.TmMessageLogic

/**
 * @description
 * @version
 */
class TMM private constructor() {

    companion object {
        val INSTANCE: TMM by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            TMM()
        }
    }

    fun getInstance(context: Application, ak: String, env: String): TMM {
        TmLoginLogic.getInstance().setAk(ak)
        TmLoginLogic.getInstance().setEnv(env)
        TmUtils.init(context)
        if (ak.isBlank() || env.isBlank()) {
            return this
        }
        DataBaseManager.getInstance().initShare(context)
        if (TmLoginLogic.getInstance().getUserId().isBlank()) {
            return this
        }
        DataBaseManager.getInstance().init(context)
        return this
    }

    fun initUser(auid: String) {
        TmLoginLogic.getInstance().initUser(auid)
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
        TmLoginLogic.getInstance().addConnectionListener(TMM::class.java.name, delegate)
        ApiBaseService.setDelegate(object : Net.Delegate_401 {
            override fun onTokenError(net: Net?) {
                val auid = LoginCache.getAUserId()
                delegate.getAuth(auid) { auth ->
                    TmLoginLogic.getInstance().login(auid, auth)
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