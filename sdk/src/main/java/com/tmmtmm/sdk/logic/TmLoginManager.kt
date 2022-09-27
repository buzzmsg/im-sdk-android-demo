package com.tmmtmm.sdk.logic

import com.tmmtmm.sdk.core.utils.TmSpUtils

/**
 * @description
 *
 * @time 2021/5/17 8:50 下午
 * @version
 */
object TmLoginManager {

    private const val MMKV_ID_USER = "mmkv_user"

    private const val KEY_UID = "key_uid"


    fun getUserId() = TmSpUtils.getString(MMKV_ID_USER, KEY_UID)

    fun setUserId(uid: String) {
        TmSpUtils.putString(MMKV_ID_USER, KEY_UID, uid)
    }




}