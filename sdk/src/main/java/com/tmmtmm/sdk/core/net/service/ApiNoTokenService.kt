package com.tmmtmm.sdk.core.net.service

import com.tmmtmm.sdk.core.net.NetClient
import com.tmmtmm.sdk.core.net.ResponseResult
import com.tmmtmm.sdk.core.net.config.NetFactory
import java.lang.reflect.Type

/**
 * @description
 * @time 2021/7/22 9:44 下午
 * @version
 */
object ApiNoTokenService {
    const val SERVICE_NAME = ""

    private const val DEFAULT_HOST = ""

    fun <T> post(data: String, path: String, type: Type): ResponseResult<T?> {
        val net = NetFactory.getInstance().getOrCreateNetByServiceName(SERVICE_NAME, host = DEFAULT_HOST)
        return NetClient.getInstance()
            .apiNoTokenCore(net = net, data = data, path = path, type = type)
    }


}