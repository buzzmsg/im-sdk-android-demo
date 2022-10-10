package com.im.sdk.core.utils

import android.app.Application

/**
 * @description
 *
 * @time 2021/5/18 12:16 上午
 * @version
 */
object TmUtils {

//    var sApp: Application? = null

    lateinit var sApp: Application

    fun init(app: Application) {
        sApp = app
    }
}