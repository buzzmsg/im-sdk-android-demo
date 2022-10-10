package com.im.sdk.db.event

import com.im.sdk.core.event.CommonEvent
import com.im.sdk.core.event.EventCenter

/**
 * @description
 *
 * @version
 */
class ConversationEvent(
    var mData: EventData? = null
) : CommonEvent {

    private var mCallback: ConversationListener? = null

    companion object {

        fun send(chatIds: MutableSet<String>) {
            EventCenter.post(
                ConversationEvent(
                    EventData(
                        chatIds = chatIds,
                    )
                )
            )
        }
    }

    fun observe(callback: ConversationListener? = null): ConversationEvent {
        mCallback = callback
        return this
    }

    override fun getEventType() = this.javaClass.simpleName.hashCode()

    override fun call(t: Any?) {
        mData = t as EventData?
        mCallback?.onConversationChanged(mData)
    }

    override fun getData(): Any? {
        return mData
    }


    data class EventData(
        var chatIds: MutableSet<String>? = null,
    )

    interface ConversationListener {

        fun onConversationChanged(data: EventData? = null)
    }
}