package com.tmmtmm.sdk.core.utils

import android.util.Log
import com.tmmtmm.sdk.BuildConfig


/**
 * @description
 * @time 2022/5/11 17:00
 * @version
 */
class LogUtils private constructor() {

    private var logFilePath = ""

    private val LOG_FILE_LOGGER = "loginfo"


//    private var logger: ExtendedLogger? = null

//    private val logFile = LoggerFactory.getLogger(LOG_FILE_LOGGER)

//    private val logcat = LoggerFactory.getLogger(LogUtils::class.java)

    companion object {
        private var instance: LogUtils? = null
        private const val TAG = "LogUtils"
        private const val MAX_FILE_COUNT = 20
        private const val MAX_FILE_SIZE = "5MB"

        @JvmStatic
        fun getInstance(): LogUtils {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = LogUtils()
                    }
                }
            }
            return instance!!
        }
    }

    private fun isRelease() = BuildConfig.BUILD_TYPE == "release"


    fun i(tag: String = "", content: String, printFileLog: Boolean = false, printConsoleLog: Boolean = true) {
        if (!isRelease() && printConsoleLog) {
            Log.i(tag, content)
        }
        if (!printFileLog) {
            return
        }
//        logger?.info(content)
    }

    fun w(tag: String = "", content: String, printFileLog: Boolean = false, printConsoleLog: Boolean = true) {
        if (!isRelease() && printConsoleLog) {
            Log.w(tag, content)
        }
        if (!printFileLog) {
            return
        }
//        logger?.warn(content)
    }

    fun e(tag: String = "", content: String, printFileLog: Boolean = false, printConsoleLog: Boolean = true) {
        if (!isRelease() && printConsoleLog) {
            Log.w(tag, content)
        }
        if (!printFileLog) {
            return
        }
//        Log.w(tag, "${logger}")
//        logger?.error(content)
    }

    fun getLogFilePath() = logFilePath
}