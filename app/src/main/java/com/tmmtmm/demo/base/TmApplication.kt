package com.tmmtmm.demo.base

import android.app.Application
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
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

    val ak = "68oni7jrg31qcsaijtg76qln"

    companion object {
        private var instance: TmApplication by Delegates.notNull()
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        instance = this
        TMM.INSTANCE.getInstance(this, ak, "test")
        TMM.INSTANCE.setDelegate(object : TMM.TmDelegate {

            override fun getAuth(auid: String, resolve: (auth: String) -> Unit) {
                val authcode = loginResponse?.authcode ?: ""
                resolve.invoke(
                    authcode
                )
            }
        })
    }
}