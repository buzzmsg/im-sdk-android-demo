package com.im.sdk.core.event

import androidx.lifecycle.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @description
 *
 * @time 2021/5/14 10:39 上午
 * @version
 */
class EventCenter<T : CommonEvent> constructor(
    lifecycleOwner: LifecycleOwner? = null
) : LifecycleObserver, LifecycleEventObserver {

    private var mCallback: CommonEvent? = null

    init {
        if (lifecycleOwner == null) {
            EventBus.getDefault().register(this)
        } else {
            lifecycleOwner.lifecycle.addObserver(this)
        }
    }

    companion object {
        fun <T : CommonEvent> handle(lifecycleOwner: LifecycleOwner? = null) =
            EventCenter<T>(lifecycleOwner)

        fun post(event: CommonEvent) {
            EventBus.getDefault().post(event)
        }

        fun remove() {
            EventBus.getDefault().unregister(this)
        }
    }

    fun addCallback(callback: CommonEvent?): EventCenter<T> {
        mCallback = callback
        return this
    }

    fun removeCallback(): EventCenter<T> {
        mCallback = null
        onDestroy()
        return this
    }


    private fun onDestroy() {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(commonEvent: CommonEvent) {
        if (mCallback?.getEventType() == commonEvent.getEventType()) {
            mCallback?.call(commonEvent.getData())
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                EventBus.getDefault().register(this)
            }
            Lifecycle.Event.ON_DESTROY -> {
                onDestroy()
            }
            else -> {}
        }
    }
}