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

//    private const val CURRENT_USER = "current_user"

    private const val KEY_ENV = "key_env"

    private var akey = ""

    var currentUserId: String? = ""

    fun getUserId(): String {
        return if (TextUtils.isEmpty(currentUserId)) {
            currentUserId = SpUtils.getString(akey, getAUserId())
            currentUserId ?: ""
        } else {
            currentUserId ?: ""
        }
    }

    fun getAUserId(): String {
        return SpUtils.getMap(akey).first()
    }

    fun setUser(auid: String, uid: String) {
        SpUtils.putString(akey, auid, uid)
    }

    fun getAKey() = akey

    fun setAKey(ak: String) {
        akey = ak
    }

    fun getEnv() = SpUtils.getString(KEY_ENV)

    fun setEnv(env: String) {
        SpUtils.putString(KEY_ENV, env)
    }

}