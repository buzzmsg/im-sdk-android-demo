package com.tmmtmm.demo.base

import android.app.Application
import com.tencent.mmkv.MMKV
import com.tmmtmm.demo.api.LoginByPhoneResponse
import com.tmmtmm.demo.manager.LoginManager
import com.tmmtmm.sdk.TMM
import com.tmmtmm.sdk.logic.TmLoginLogic
import kotlin.properties.Delegates

/**
 * @description
 * @version
 */
class TmApplication : Application() {

//    lateinit var instance: TmApplication

    var loginResponse: LoginByPhoneResponse? = null

    companion object {
        private var instance: TmApplication by Delegates.notNull()
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        instance = this
        TMM.INSTANCE.getInstance(this, LoginManager.INSTANCE.getAKey(), "test")
        TMM.INSTANCE.setConnectionDelegate(object : TmLoginLogic.TmConnectionDelegate {

            override fun getAuth(auid: String, resolve: (auth: String) -> Unit) {
                val authcode = loginResponse?.authcode ?: ""
                resolve.invoke(
                    authcode
                )
            }
        })
    }
}