package com.tmmtmm.demo.base

import android.app.Application
import com.tencent.mmkv.MMKV

/**
 * @description
 * @version
 */
class Application: Application() {
    override fun onCreate() {
        super.onCreate()

        MMKV.initialize(this)
    }
}