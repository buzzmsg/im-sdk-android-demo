package com.tmmtmm.sdk.core.utils

import com.tencent.mmkv.MMKV
import com.tmmtmm.sdk.cache.LoginCache

/**
 * @description
 *
 * @time 2021/3/9 8:37 下午
 * @version
 */
object SpUtils {

    fun putStringSet(key: String, value: Set<String>) {
        MMKV.mmkvWithID(LoginCache.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .encode(key, value)
    }

    fun putString(key: String, value: String) {
        MMKV.mmkvWithID(LoginCache.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .encode(key, value)
    }

    fun putString(id : String, key: String, value: String) {
        MMKV.mmkvWithID(id, MMKV.MULTI_PROCESS_MODE)
            .encode(key, value)
    }

    fun putInt(key: String, value: Int) {
        MMKV.mmkvWithID(LoginCache.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .encode(key, value)
    }

    fun putLong(key: String, value: Long) {
        MMKV.mmkvWithID(LoginCache.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .encode(key, value)
    }

    fun putBool(key: String, value: Boolean) {
        MMKV.mmkvWithID(LoginCache.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .encode(key, value)
    }

    fun putDouble(key: String, value: Double) {
        MMKV.mmkvWithID(LoginCache.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .encode(key, value)
    }

    fun getString(id: String, key: String): String {
        return MMKV.mmkvWithID(id, MMKV.MULTI_PROCESS_MODE)
            .decodeString(key, "")
    }

    fun getString(key: String): String {
        return MMKV.mmkvWithID(LoginCache.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .decodeString(key, "")
    }

    fun getStringSet(key: String): Set<String> {
        return MMKV.mmkvWithID(LoginCache.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .decodeStringSet(key, setOf())
    }

    fun getInt(key: String): Int {
        return MMKV.mmkvWithID(LoginCache.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .decodeInt(key, 0)
    }

    fun getLong(key: String): Long {
        return MMKV.mmkvWithID(LoginCache.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .decodeLong(key, 0)
    }

    fun getBool(key: String): Boolean {
        return MMKV.mmkvWithID(LoginCache.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .decodeBool(key, false)
    }

    fun getDouble(key: String): Double {
        return MMKV.mmkvWithID(LoginCache.getUserId(), MMKV.SINGLE_PROCESS_MODE)
            .decodeDouble(key, 0.0)
    }

}