package com.tmmtmm.sdk.cache

import android.text.TextUtils
import com.tmmtmm.sdk.core.utils.SpUtils

/**
 * @description
 *
 * @time 2021/5/17 8:50 下午
 * @version
 */
object LoginCache {

    private const val CURRENT_USER = "current_user"

    private const val KEY_AK = "key_ak"

    private const val KEY_ENV = "key_env"

    var currentUserId: String? = ""

    fun getUserId(): String {
        return if (TextUtils.isEmpty(currentUserId)) {
            currentUserId = (getUser().entries.first().value as? String) ?: ""
            currentUserId ?: ""
        } else {
            currentUserId ?: ""
        }
    }

    fun getAUserId():String {
        return (getUser().entries.first().key as? String) ?: ""
    }

    fun setUser(auid: String, uid: String) {
        SpUtils.putString(CURRENT_USER, auid, uid)
    }

    fun getUser(): Map<String, *> {
        return SpUtils.getMap(CURRENT_USER)
    }

    fun getAk() = SpUtils.getString(KEY_AK)

    fun setAk(ak: String) {
        SpUtils.putString(KEY_AK, ak)
    }

    fun getEnv() = SpUtils.getString(KEY_ENV)

    fun setEnv(env: String) {
        SpUtils.putString(KEY_ENV, env)
    }

}