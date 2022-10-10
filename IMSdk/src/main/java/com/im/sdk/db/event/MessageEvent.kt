package com.im.sdk.db.event

import com.im.sdk.core.event.CommonEvent
import com.im.sdk.core.event.EventCenter


/**
 * @description
 *
 * @version
 */
class MessageEvent(
    var mData: EventData? = null,
) : CommonEvent {


    companion object {
        fun send(mids: MutableSet<String>?, chatId: String?, isReadReceipt: Boolean = false) {
            val event = MessageEvent(
                EventData(
                    mids,
                    chatId,
                    isReadReceipt
                )
            )
            EventCenter.post(event)
        }
    }

    private var mCallback: MessageListener? =
        null

    fun observe(callback: MessageListener? = null): MessageEvent {
        mCallback = callback
        return this
    }

    override fun getEventType() = this.javaClass.simpleName.hashCode()

    override fun call(t: Any?) {
        mData = t as EventData?
        mCallback?.onReceiveMessage(mData)
    }

    override fun getData(): Any? {
        return mData
    }

    data class EventData(
        var mids: MutableSet<String>? = null,
        var chatId: String? = null,
        var isReadReceipt: Boolean = false,
    ) {
//        fun getTmMessage(mid: String): TmMessage? {
//            if (messages == null) {
//                return null
//            }
//            for (message in (messages?: mutableListOf())) {
//                if (message.mid == mid) {
//                    return message
//                }
//            }
//            return null
//        }
    }

    interface MessageListener {

        fun onReceiveMessage(data: EventData?)
    }
}




