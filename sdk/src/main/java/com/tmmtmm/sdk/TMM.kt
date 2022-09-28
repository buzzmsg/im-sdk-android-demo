package com.tmmtmm.sdk

import com.tmmtmm.sdk.core.net.config.Net
import com.tmmtmm.sdk.core.net.service.ApiBaseService
import com.tmmtmm.sdk.logic.TmLoginLogic

/**
 * @description
 * @version
 */
object TMM {

    fun getInstance(ak: String, env: String) {
        TmLoginLogic.getInstance().setAk(ak)
        TmLoginLogic.getInstance().setEnv(env)
    }

    fun initUser(aUid: String) {
        TmLoginLogic.getInstance().login(aUid)
    }


    fun setConnectionDelegate(delegate: TmLoginLogic.TmConnectionDelegate) {
        ApiBaseService.setDelegate(object : Net.Delegate_401 {
            override fun onTokenError(net: Net?) {
//                delegate.onConnectLost()
            }
        })
    }
}