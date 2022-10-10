package com.im.sdk.core.net.config

import com.im.sdk.core.net.NetClient
import com.im.sdk.core.net.manager.NetDbManager

/**
 * @description
 * @time 2021/7/22 3:45 下午
 * @version
 */
class Net(private var serviceName: String, private var mHost: String) {
    var mIs401: Boolean = false
    private var mDelegate401: Delegate_401? = null

    companion object {
        fun create(serviceName: String, host: String): Net {
            return Net(serviceName,host)
        }
    }

    fun getHost() = mHost

    fun getServiceName() = serviceName

    fun setToken(token: String): Net {
        if (token != NetClient.EMPTY_TOKEN) {
            mIs401 = false
        }
        NetDbManager.getInstance().insertNet(serviceName, host = mHost, token = token)
        return this
    }

    fun clearToken() {

    }

    fun set401State(is401: Boolean): Net {
        mIs401 = is401
        return this
    }

    fun clear401State(): Net {
        mIs401 = false
        return this
    }

    fun set401Delegate(delegate401: Delegate_401): Net {
        mDelegate401 = delegate401
        return this
    }

    fun getToken(): String? {
        val netModel = NetDbManager.getInstance().getNetByServiceName(serviceName)
//        if (tokenByUrl.isNullOrBlank()) {
//            val oldImToken = NetDbManager.getInstance().getNetByServiceName(NetFactory.OLD_IM_BASE_URL)
//            if (!oldImToken.isNullOrBlank()) {
//                tokenByUrl = oldImToken
//                NetDbManager.getInstance().insertNet(baseUrl, token = tokenByUrl)
//            }
//        }
        return netModel?.token
//        if (!mToken.isNullOrBlank()) {
//            return mToken
//        }
//        return TokenApiManager.getTokenByUrl(baseUrl)
    }

    fun get401Delegate() = mDelegate401


    interface Delegate_401 {
        fun onTokenError(net: Net?)
    }

}