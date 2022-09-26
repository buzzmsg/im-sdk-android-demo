package com.tmmtmm.sdk.core.net.listener.impl

import android.net.Network
import com.tmmtmm.sdk.core.net.listener.TmConnectionListener

/**
 * @description
 * @time 2021/11/19 4:39 下午
 * @version
 */
open class TmConnectionListenerImpl : TmConnectionListener {

    override fun networkOnAvailable(network: Network) {
    }

    override fun networkOnLosing(network: Network, maxMsToLive: Int) {
    }

    override fun networkOnLost(network: Network) {
    }

    override fun networkOnUnavailable() {
    }

    override fun networkChanged() {

    }


    override fun onForeground() {
    }

    override fun onBackground() {

    }
}