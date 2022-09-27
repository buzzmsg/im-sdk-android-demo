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


    fun getUserId() = SpUtils.getString(MMKV_ID_USER, KEY_UID)

    fun setUserId(uid: String) {
        SpUtils.putString(MMKV_ID_USER, KEY_UID, uid)
    }




}