package com.tmmtmm.sdk.logic

import kotlinx.coroutines.sync.Mutex

/**
 * @description
 * @version
 */
class TmMessageLogic private constructor(){

    private val mutex = Mutex()

    companion object {
        val INSTANCE: TmMessageLogic by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            TmMessageLogic()
        }
    }

    fun receiveMessage() {

    }
}