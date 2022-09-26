package com.tmmtmm.sdk.core.net.manager

import android.util.ArrayMap
import com.tencent.mmkv.MMKV
import com.tmmtmm.sdk.core.net.model.NetModel
import com.tmmtmm.sdk.core.net.toEntity

/**
 * @description
 * @time 2021/7/22 5:04 下午
 * @version
 */
class NetDbManager private constructor() {

    private var cacheNetMap = ArrayMap<String, NetModel?>()

    companion object {

        private var instance: NetDbManager? = null

        private var TOKEN_KEY = "NET_BASE_TOKEN"

        private const val KEY_NET = "KEY_NET"

        @JvmStatic
        fun getInstance(): NetDbManager {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = NetDbManager()
                    }
                }
            }
            return instance!!
        }
    }

    fun insertNet(serviceName: String, host: String, token: String) {
        cacheNetMap[serviceName] = NetModel(serviceName = serviceName, host = host, token = token)
        MMKV.mmkvWithID(TOKEN_KEY).encode(serviceName, token)
    }

    fun getNetByServiceName(serviceName: String): NetModel? {
        return if (cacheNetMap.containsKey(serviceName)) {
            cacheNetMap[serviceName]
        } else {
            val netModelJson = MMKV.mmkvWithID(KEY_NET).decodeString(serviceName)
            val token = MMKV.mmkvWithID(TOKEN_KEY).decodeString(serviceName)
            val netModel = if (netModelJson.isNullOrBlank()) {
                if (token.isNullOrBlank()) {
                    null
                } else {
                    NetModel(serviceName = serviceName, host = serviceName,token = token)
                }
            } else {
                netModelJson.toEntity<NetModel>()
            }
            return netModel
        }
    }

    fun clearTokenByServiceName(serviceName: String) {
        if (cacheNetMap.containsKey(serviceName)) {
            cacheNetMap[serviceName] = null
        }
        MMKV.mmkvWithID(TOKEN_KEY).encode(serviceName, "")
    }

    fun clearAll() {
        MMKV.mmkvWithID(TOKEN_KEY).clearAll()
    }
}