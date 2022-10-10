package com.im.sdk.core.net.config

import java.util.concurrent.ConcurrentHashMap

/**
 * @description
 * @time 2021/7/22 3:44 下午
 * @version
 */
class NetFactory private constructor() {

    private val netsMap = ConcurrentHashMap<String, Net>()

    companion object {

        private var instance: NetFactory? = null

        const val OLD_IM_BASE_URL = "https://v2.imtmm.com:5100"

        @JvmName("getInstance1")
        fun getInstance(): NetFactory {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = NetFactory()
                    }
                }
            }
            return instance!!
        }
    }

    fun getOrCreateNetByServiceName(serviceName: String, host: String): Net {
        val net = if (netsMap.containsKey(serviceName) && netsMap[serviceName] != null) {
            val netInMap =
                netsMap[serviceName] ?: Net.create(serviceName = serviceName, host = host)
            netInMap
        } else {
            val newNet = Net.create(serviceName = serviceName, host = host)
            newNet
        }
        netsMap[serviceName] = net
        return net
    }

    fun getNetByServiceName(serviceName: String): Net? {
//        val net = if (netsMap.containsKey(serviceName) && netsMap[serviceName] != null) {
//            val netInMap = netsMap[serviceName] ?: Net.create(serviceName = serviceName,host = host)
//            netInMap
//        } else {
//            val newNet = Net.create(serviceName = serviceName, host = host)
//            newNet
//        }
//        netsMap[serviceName] = net
        return netsMap[serviceName]
    }

    fun removeNetWith401Delegate() {
        netsMap.clear()
    }

//    fun setNetWith401State(url: String, is401State: Boolean) {
//        val net = if (netsMap.containsKey(url)) {
//            val netInMap = netsMap[url]
//            netInMap?.set401State(is401State)
//            netInMap
//        } else {
//            val newNet = Net.create(url).set401State(is401State)
//            newNet
//        }
//        netsMap[url] = net
//    }

}