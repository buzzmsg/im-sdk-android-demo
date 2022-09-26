package com.tmmtmm.sdk.core.net.websocket

interface IReceiveMessage {
    fun onConnecting()
    fun onConnectSuccess()
    fun onConnectFailed()
    fun onClose()
    fun onMessage(content: String)
}