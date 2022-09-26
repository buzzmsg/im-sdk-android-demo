package com.tmmtmm.sdk.core.net.service

import com.tmmtmm.sdk.core.net.NetClient
import com.tmmtmm.sdk.core.net.ResponseResult
import com.tmmtmm.sdk.core.net.config.Net
import com.tmmtmm.sdk.core.net.config.NetFactory
import com.tmmtmm.sdk.core.net.manager.NetDbManager
import java.lang.reflect.Type

/**
 * @description
 * @time 2021/7/22 5:28 下午
 * @version
 */
object ApiBaseService {
    private const val SERVICE_NAME = ""

    private const val DEFAULT_HOST = ""

    fun getServiceName() = SERVICE_NAME

    fun getHost() = DEFAULT_HOST

    fun getCurrentToken() = NetDbManager.getInstance().getNetByServiceName(SERVICE_NAME)?.token

    fun <T> post(data: String, path: String, type: Type): ResponseResult<T?> {
        val net =
            NetFactory.getInstance().getOrCreateNetByServiceName(SERVICE_NAME, host = DEFAULT_HOST)
        return NetClient.getInstance().apiCore(net = net, data = data, path = path, type = type)
    }

    fun setDelegate(delegate401: Net.Delegate_401) {
        NetFactory.getInstance()
            .getOrCreateNetByServiceName(serviceName = SERVICE_NAME, host = DEFAULT_HOST).set401Delegate(delegate401)
    }

    fun removeDelegate() {
        NetFactory.getInstance().removeNetWith401Delegate()
    }
}