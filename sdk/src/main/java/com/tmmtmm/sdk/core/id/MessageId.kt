package com.tmmtmm.sdk.core.id

import com.tmmtmm.sdk.core.hash.MD5
import com.tmmtmm.sdk.core.utils.Random

/**
 * @description
 *
 * @time 2021/5/13 11:11 上午
 * @version
 */
object MessageId {

    private const val create = "createMessage"

    fun create(uid:String):String {
        val mid = "$uid${System.currentTimeMillis()}${Random.create(6)}$create"
        return MD5.create(mid)
    }
}