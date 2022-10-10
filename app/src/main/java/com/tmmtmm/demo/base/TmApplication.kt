package com.tmmtmm.demo.base

import android.app.Application
import com.blankj.utilcode.util.ThreadUtils
import com.im.sdk.IMSdk
import com.tencent.mmkv.MMKV
import com.tmmtmm.demo.api.GetAuth
import com.tmmtmm.demo.api.GetAuthRequest
import com.tmmtmm.demo.api.LoginByPhoneResponse
import com.tmmtmm.demo.api.ResponseResult
import com.tmmtmm.demo.manager.LoginManager
import kotlin.properties.Delegates

/**
 * @description
 * @version
 */
class TmApplication : Application() {

//    lateinit var instance: TmApplication

    var loginResponse: LoginByPhoneResponse? = null

    var imSdk: IMSdk? = null

    private val ak = "68oni7jrg31qcsaijtg76qln"

    companion object {
        private var instance: TmApplication by Delegates.notNull()
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        instance = this
        imSdk = IMSdk.getInstance(context = this, ak = ak, "test")

        imSdk?.setDelegate(object : IMSdk.ImDelegate {
            override fun getAuth(auid: String, resolve: (auth: String) -> Unit) {

                val localAuthCode = LoginManager.INSTANCE.getAuthCode()

                if (localAuthCode.isNotBlank()) {
                    resolve.invoke(localAuthCode)
                    return
                }


                ThreadUtils.executeByCached(object : ThreadUtils.Task<String>() {
                    override fun doInBackground(): String {
                        val result =
                            GetAuth.execute(GetAuthRequest(LoginManager.INSTANCE.getToken()))

                        if (result !is ResponseResult.Success) return ""

                        val authCode = result.value?.authcode ?: ""

                        LoginManager.INSTANCE.setAuthCode(authCode)
                        resolve.invoke(
                            authCode
                        )
                        return ""
                    }

                    override fun onSuccess(result: String?) {
                    }

                    override fun onFail(t: Throwable?) {
                    }

                    override fun onCancel() {
                    }

                })


            }
        })
    }
}