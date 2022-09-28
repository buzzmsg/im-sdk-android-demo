package com.tmmtmm.sdk.cache

import com.tmmtmm.sdk.core.utils.SpUtils

/**
 * @description
 *
 * @time 2021/5/17 8:50 下午
 * @version
 */
object LoginCache {

    private const val MMKV_ID_USER = "mmkv_user"

    private const val KEY_UID = "key_uid"

    private const val KEY_AK = "key_ak"

    private const val KEY_ENV = "key_env"


    fun getUserId() = SpUtils.getString(MMKV_ID_USER, KEY_UID)

    fun setUserId(uid: String) {
        SpUtils.putString(MMKV_ID_USER, KEY_UID, uid)
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