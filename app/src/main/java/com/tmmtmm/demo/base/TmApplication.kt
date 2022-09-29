package com.tmmtmm.demo.base

import android.app.Application
import com.tencent.mmkv.MMKV
import com.tmmtmm.demo.api.LoginByPhoneResponse
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
        TMM.INSTANCE.setConnectionDelegate(object : TmLoginLogic.TmConnectionDelegate {
            override fun onConnectLost(
                auid: String,
                resolve: (time: Long, nonce: String, signature: String) -> Unit
            ) {
                val timeStamp = loginResponse?.timestamp ?: 0
                val nonce = loginResponse?.nonce ?: ""
                val signature = loginResponse?.signature ?: ""
                resolve.invoke(
                    timeStamp,
                    nonce,
                    signature
                )
            }
        })
    }
}