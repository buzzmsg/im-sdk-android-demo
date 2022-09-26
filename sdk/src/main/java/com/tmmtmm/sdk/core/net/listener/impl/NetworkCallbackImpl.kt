package com.tmmtmm.sdk.core.net.listener.impl

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * @description
 *
 * @time 2021/5/29 12:13 下午
 * @version
 */
class NetworkCallbackImpl : ConnectivityManager.NetworkCallback() {

    private var isNetAvailable = true

    private var mTmConnectionImpMap : ConcurrentHashMap<String, TmConnectionListenerImpl> ?= null

    private val TAG = "NetworkCallbackImpl"

    init {
        GlobalScope.launch(Dispatchers.IO) {
            isNetAvailable = NetworkUtils.isAvailable()
        }
    }

    fun setConnectionImpl(tmConnectionImpMap : ConcurrentHashMap<String, TmConnectionListenerImpl> ?= null) {
        mTmConnectionImpMap = tmConnectionImpMap
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        if (isNetAvailable) {
            return
        }
        isNetAvailable = true
//        GlobalScope.launch(Dispatchers.IO) {
//            TmManager.getInstance().retry()
//            TmManager.getInstance().sync()
            mTmConnectionImpMap?.values?.forEach { listener ->
                listener.networkOnAvailable(network)
            }
//        }
    }

    override fun onLosing(network: Network, maxMsToLive: Int) {
        super.onLosing(network, maxMsToLive)
        isNetAvailable = false
        mTmConnectionImpMap?.values?.forEach { listener ->
            listener.networkOnLosing(network,maxMsToLive)
        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        isNetAvailable = false
        mTmConnectionImpMap?.values?.forEach { listener ->
            listener.networkOnLost(network)
        }
    }

    override fun onUnavailable() {
        super.onUnavailable()
        isNetAvailable = false
        mTmConnectionImpMap?.values?.forEach { listener ->
            listener.networkOnUnavailable()
        }
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                //wifi connected
                Log.w(
                    TAG,
                    "onCapabilitiesChanged() wifi connected with: network = $network, networkCapabilities = $networkCapabilities"
                )
                mTmConnectionImpMap?.values?.forEach { listener ->
                    listener.networkChanged()
                }
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                //transport cellular connected
                Log.w(
                    TAG,
                    "onCapabilitiesChanged() transport cellular connected with: network = $network, networkCapabilities = $networkCapabilities"
                )
                mTmConnectionImpMap?.values?.forEach { listener ->
                    listener.networkChanged()
                }
            } else {
                //other net
                Log.w(
                    TAG,
                    "onCapabilitiesChanged() other net with: network = $network, networkCapabilities = $networkCapabilities"
                )
                mTmConnectionImpMap?.values?.forEach { listener ->
                    listener.networkChanged()
                }
            }
        }
    }

    override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties)
    }

    override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
        super.onBlockedStatusChanged(network, blocked)
    }
}