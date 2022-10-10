package com.im.sdk.core.event

interface CommonEvent {
    fun getEventType(): Int
    fun call(t: Any?)
    fun getData(): Any?
}