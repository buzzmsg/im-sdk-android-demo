package com.tmmtmm.sdk.core.utils

import com.tencent.mmkv.MMKV
import com.tmmtmm.sdk.usercore.logic.TmLoginManager

/**
 * @description
 *
 * @time 2021/3/9 8:37 下午
 * @version
 */
object SpUtils {

    fun putStringSet(key: String, value: Set<String>) {
        MMKV.mmkvWithID(TmLoginManager.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .encode(key, value)
    }

    fun putString(key: String, value: String) {
        MMKV.mmkvWithID(TmLoginManager.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .encode(key, value)
    }

    fun putString(id : String, key: String, value: String) {
        MMKV.mmkvWithID(id, MMKV.MULTI_PROCESS_MODE)
            .encode(key, value)
    }

    fun putInt(key: String, value: Int) {
        MMKV.mmkvWithID(TmLoginManager.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .encode(key, value)
    }

    fun putLong(key: String, value: Long) {
        MMKV.mmkvWithID(TmLoginManager.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .encode(key, value)
    }

    fun putBool(key: String, value: Boolean) {
        MMKV.mmkvWithID(TmLoginManager.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .encode(key, value)
    }

    fun putDouble(key: String, value: Double) {
        MMKV.mmkvWithID(TmLoginManager.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .encode(key, value)
    }

    fun getString(id: String, key: String): String {
        return MMKV.mmkvWithID(id, MMKV.MULTI_PROCESS_MODE)
            .decodeString(key, "")
    }

    fun getString(key: String): String {
        return MMKV.mmkvWithID(TmLoginManager.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .decodeString(key, "")
    }

    fun getStringSet(key: String): Set<String> {
        return MMKV.mmkvWithID(TmLoginManager.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .decodeStringSet(key, setOf())
    }

    fun getInt(key: String): Int {
        return MMKV.mmkvWithID(TmLoginManager.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .decodeInt(key, 0)
    }

    fun getLong(key: String): Long {
        return MMKV.mmkvWithID(TmLoginManager.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .decodeLong(key, 0)
    }

    fun getBool(key: String): Boolean {
        return MMKV.mmkvWithID(TmLoginManager.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .decodeBool(key, false)
    }

    fun getDouble(key: String): Double {
        return MMKV.mmkvWithID(TmLoginManager.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .decodeDouble(key, 0.0)
    }

}