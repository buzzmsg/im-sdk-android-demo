package com.im.sdk.core.utils

import java.security.SecureRandom
import java.util.Random
import kotlin.math.roundToLong


/**
 * @description
 *
 * @time 2021/5/13 11:09 上午
 * @version
 */
object Random {

    fun create(length: Int): String {
        val random = Random()
        val sb = StringBuffer()
        for (i in 0 until length) {
            val number: Int = random.nextInt(3)
            var result: Long
            when (number) {
                0 -> {
                    result = (Math.random() * 25 + 65).roundToLong()
                    sb.append(result.toInt().toChar())
                }
                1 -> {
                    result = (Math.random() * 25 + 97).roundToLong()
                    sb.append(result.toInt().toChar())
                }
                2 -> sb.append(
                    Random().nextInt(10)
                )
            }
        }
        return sb.toString()
    }

    fun create(size: Int, seed: Int) {
        val secureRandom = SecureRandom()
        secureRandom.nextInt()
    }

}