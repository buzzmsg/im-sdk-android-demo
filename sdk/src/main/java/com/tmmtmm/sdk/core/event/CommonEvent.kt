package com.tmmtmm.sdk.core.event

interface CommonEvent {
    fun getEventType(): Int
    fun call(t: Any?)
    fun getData(): Any?
}