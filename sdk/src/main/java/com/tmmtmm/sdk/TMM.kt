package com.tmmtmm.sdk

import com.tmmtmm.sdk.logic.TmLoginLogic

/**
 * @description
 * @version
 */
object TMM {

    fun getInstance(ak: String, env: String){
        TmLoginLogic.getInstance().setAk(ak)
        TmLoginLogic.getInstance().setEnv(env)
    }

    fun initUser(aUid: String){
        TmLoginLogic.getInstance().login(aUid)
    }
}