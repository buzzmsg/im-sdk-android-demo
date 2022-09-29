package com.tmmtmm.sdk

import android.app.Application
import com.tmmtmm.sdk.cache.LoginCache
import com.tmmtmm.sdk.core.db.DataBaseManager
import com.tmmtmm.sdk.core.id.ChatId
import com.tmmtmm.sdk.core.net.config.Net
import com.tmmtmm.sdk.core.net.listener.impl.TmConnectionListenerImpl
import com.tmmtmm.sdk.core.net.service.ApiBaseService
import com.tmmtmm.sdk.core.utils.TmUtils
import com.tmmtmm.sdk.core.utils.TransferThreadPool
import com.tmmtmm.sdk.logic.TmGroupLogic
import com.tmmtmm.sdk.logic.TmLoginLogic
import com.tmmtmm.sdk.logic.TmMessageLogic
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.sign

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

    fun getInstance(context: Application, ak: String, env: String) {
        TmLoginLogic.getInstance().setAk(ak)
        TmLoginLogic.getInstance().setEnv(env)
        if (TmLoginLogic.getInstance().getUserId().isBlank()) {
            return
        }
        TmUtils.init(context)
        DataBaseManager.getInstance().init(context)
    }

    fun initUser(auid: String) {
        TmLoginLogic.getInstance().initUser(auid)
    }

    fun sendTextMessage(
        content: String,
        aChatId: String
    ) {
        val chatId = ChatId.create(aChatId)
        TransferThreadPool.submitTask {
            TmMessageLogic.INSTANCE.sendTextMessage(content, chatId)
        }
    }

    fun setConnectionDelegate(delegate: TmLoginLogic.TmConnectionDelegate) {
        TmLoginLogic.getInstance().addConnectionListener(this::javaClass.name,delegate)
        ApiBaseService.setDelegate(object : Net.Delegate_401 {
            override fun onTokenError(net: Net?) {
                val auid = LoginCache.getAUserId()
                delegate.onConnectLost(auid) { time, nonce, signature ->
                    TmLoginLogic.getInstance().login(auid, time, nonce, signature)
                }
            }
        })
    }


    fun createGroup(aChatId: String, auids: MutableList<String>){
        TmGroupLogic.INSTANCE.createGroup(aChatId, auids)
    }

}