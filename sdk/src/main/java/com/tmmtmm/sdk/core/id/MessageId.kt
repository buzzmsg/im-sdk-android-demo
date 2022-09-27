package com.tmmtmm.im.core.utils.id

import com.tmmtmm.im.core.utils.Random
import com.tmmtmm.im.core.utils.hash.MD5

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