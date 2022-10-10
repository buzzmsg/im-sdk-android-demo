package com.im.sdk.logic

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.im.sdk.core.net.listener.impl.NetworkCallbackImpl
import com.im.sdk.core.net.listener.impl.TmConnectionListenerImpl
import com.im.sdk.core.net.websocket.WebSocketManager
import com.im.sdk.core.utils.TransferThreadPool
import java.util.concurrent.ConcurrentHashMap

/**
 * @description
 * @version
 */
class TmNetWorkStatusLogic private constructor()  : LifecycleEventObserver {

    private val networkCallbackImpl = NetworkCallbackImpl()
    private var tmConnectionImpMap = ConcurrentHashMap<String, TmConnectionListenerImpl>()
    private var defaultConnectionKey = this.javaClass.name

    companion object {
        private var instance: TmNetWorkStatusLogic? = null

        private const val TAG = "TmNetWorkStatusLogic"

        @JvmName("getInstance1")
        fun getInstance(): TmNetWorkStatusLogic {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = TmNetWorkStatusLogic()
                    }
                }
            }
            return instance!!
        }
    }


    fun registerNetworkStatus(mContext: Context) {
        val builder = NetworkRequest.Builder()
        val request = builder.build()
        val connMgr: ConnectivityManager? =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        addConnectionListener(null, object : TmConnectionListenerImpl() {

            override fun networkOnAvailable(network: Network) {
                super.networkOnAvailable(network)

//                if (!TmLoginManager.isSdkInit()) return

                WebSocketManager.getInstance().connect()
//                globalIO {
//                    syncWithMessageReceiveLoading()
//                    retry()
//                }

                TransferThreadPool.submitTask {
                    TmMessageLogic.INSTANCE.receiveMessage()
                    TmMessageLogic.INSTANCE.retrySendMessages()
                }
            }

            override fun networkChanged() {
                super.networkChanged()
//                STSManager.getInstance().init()
            }

        })
        connMgr?.registerNetworkCallback(request, networkCallbackImpl)
    }

    fun addConnectionListener(
        lifecycleOwner: LifecycleOwner? = null,
        tmConnectionListenerImpl: TmConnectionListenerImpl
    ) {
        if (lifecycleOwner == null) {
            tmConnectionImpMap[defaultConnectionKey] = tmConnectionListenerImpl
        } else {
            lifecycleOwner.lifecycle.addObserver(this)
            tmConnectionImpMap[lifecycleOwner.hashCode().toString()] = tmConnectionListenerImpl
        }
        networkCallbackImpl.setConnectionImpl(tmConnectionImpMap)
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {
                if (tmConnectionImpMap.keys.contains(source.hashCode().toString())) {
                    val iterator = tmConnectionImpMap.iterator()
                    while (iterator.hasNext()) {
                        val next = iterator.next()
                        if (next.key == source.hashCode().toString()) {
                            iterator.remove()
                        }
                    }
                }
            }

            else -> {}
        }
    }
}