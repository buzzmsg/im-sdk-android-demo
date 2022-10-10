package com.tmmtmm.demo.base

import android.app.Application
import com.im.sdk.IMSdk
import com.tencent.mmkv.MMKV
import com.tmmtmm.demo.api.LoginByPhoneResponse
import kotlin.properties.Delegates

/**
 * @description
 * @version
 */
class TmApplication : Application() {

//    lateinit var instance: TmApplication

    var loginResponse: LoginByPhoneResponse? = null

    var imSdk: IMSdk? = null

    val ak = "68oni7jrg31qcsaijtg76qln"

    companion object {
        private var instance: TmApplication by Delegates.notNull()
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        instance = this
        imSdk = IMSdk.getInstance(context = this, ak = ak,"test")
        imSdk?.setDelegate(object : IMSdk.ImDelegate {
            override fun getAuth(auid: String, resolve: (auth: String) -> Unit) {
                val authcode = loginResponse?.authcode ?: ""
                resolve.invoke(
                    authcode
                )
            }
        })
    }
}