package com.im.sdk.core.net.listener

import android.net.Network

/**
 * @description
 * @time 2021/11/19 4:36 下午
 * @version
 */
interface TmConnectionListener {

    fun networkOnAvailable(network: Network)

    fun networkOnLosing(network: Network, maxMsToLive: Int)

    fun networkOnLost(network: Network)

    fun networkOnUnavailable()

    fun networkChanged()

    fun onForeground()

    fun onBackground()
}