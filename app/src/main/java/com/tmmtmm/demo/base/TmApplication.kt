package com.tmmtmm.demo.base

import android.app.Application
import android.util.Log
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.ThreadUtils
import com.im.sdk.IMSdk
import com.im.sdk.dto.TmUserinfo
import com.im.sdk.extensions.globalIO
import com.tencent.mmkv.MMKV
import com.tmmtmm.demo.api.GetAuth
import com.tmmtmm.demo.api.GetAuthRequest
import com.tmmtmm.demo.api.LoginByPhoneResponse
import com.tmmtmm.demo.api.ResponseResult
import com.tmmtmm.demo.manager.LoginManager
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.properties.Delegates
import kotlin.random.Random

/**
 * @description
 * @version
 */
class TmApplication : Application() {

    var loginResponse: LoginByPhoneResponse? = null

    var imSdk: IMSdk? = null

    var cacheTmUserinfoList = mutableListOf<TmUserinfo>()

    val avatarName = "avatar_default_"

    private val ak = "68oni7jrg31qcsaijtg76qln"

    companion object {
        private const val TAG = "TmApplication"
        private var instance: TmApplication by Delegates.notNull()
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        instance = this
        imSdk = IMSdk.getInstance(context = this, ak = ak, "test", deviceId = "android")
        imSdk?.setDelegate(object : IMSdk.IMDelegate {

            override fun authCodeExpire(auid: String) {
//                val localAuthCode = LoginManager.INSTANCE.getAuthCode()
//
//                if (localAuthCode.isNotBlank()) {
//                    imSdk?.setAuthCode(localAuthCode)
////                    resolve.invoke(localAuthCode)
//                    return
//                }


                ThreadUtils.executeByCached(object : ThreadUtils.Task<String>() {
                    override fun doInBackground(): String {
                        val result =
                            GetAuth.execute(GetAuthRequest(LoginManager.INSTANCE.getToken()))

                        if (result !is ResponseResult.Success) return ""

                        val authCode = result.value?.authcode ?: ""

                        LoginManager.INSTANCE.setAuthCode(authCode)
//                        resolve.invoke(
//                            authCode
//                        )
                        imSdk?.setAuthCode(authCode)
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

            override fun onShowUserinfo(auids: List<String>) {
                setUserinfo(auids)
            }

            override fun onReceiveMessages(amids: List<String>) {

            }

            override fun onShowConversationMarker(aChatIds: List<String>) {

            }

            override fun onShowConversationSubTitle(aChatIds: List<String>) {

            }
        })
    }

    fun setUserinfo(auids: List<String>) {
        globalIO {
            val mapCacheUserinfoList = cacheTmUserinfoList.associateBy({ it.auid }, { it.item })
            for (auid in auids) {
                if (auid.isEmpty()) continue

                if (mapCacheUserinfoList.keys.contains(auid)) {
                    continue
                }
                Log.d(TAG, "setUserinfo() called auid  = $auid")
                if (auid.isBlank()) {
                    continue
                }
                //R.drawable.avatar_default_1)
                val drawableName = avatarName + Random.nextInt(1, 30)
                val drawableIdByName = ResourceUtils.getDrawableIdByName(drawableName)
                val bitmap =
                    ImageUtils.drawable2Bytes(ResourceUtils.getDrawable(drawableIdByName))
                val tmUserinfoItemDto =
                    TmUserinfo.UserinfoItemDto(bitmap, name = "alex_${auid.substring(0, 5)}")
                val tmUserinfo = TmUserinfo(auid, tmUserinfoItemDto)
                cacheTmUserinfoList.add(tmUserinfo)
            }
            imSdk?.setUserinfo(CopyOnWriteArrayList(cacheTmUserinfoList))
        }
    }
}