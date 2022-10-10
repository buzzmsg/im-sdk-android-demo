package com.im.sdk.db.event

import com.im.sdk.core.event.CommonEvent
import com.im.sdk.core.event.EventCenter


/**
 * @description
 *
 * @version
 */
class LoginSuccessEvent(
    var mData: EventData? = null,
) : CommonEvent {


    companion object {
        fun send(auid:String) {
            val event = LoginSuccessEvent(
                EventData(
                    auid,
                )
            )
            EventCenter.post(event)
        }
    }

    private var mCallback: LoginSuccessListener? =
        null

    fun observe(callback: LoginSuccessListener? = null): LoginSuccessEvent {
        mCallback = callback
        return this
    }

    override fun getEventType() = this.javaClass.simpleName.hashCode()

    override fun call(t: Any?) {
        mData = t as EventData?
        mCallback?.onLoginSuccess(mData?.auid)
    }

    override fun getData(): Any? {
        return mData
    }

    data class EventData(
        var auid: String? = null,
    )

    interface LoginSuccessListener {
        fun onLoginSuccess(auid: String?)
    }
}




