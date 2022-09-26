package com.tmmtmm.sdk.usercore.logic

import com.tmmtmm.sdk.core.utils.SpUtils

/**
 * @description
 *
 * @time 2021/5/17 8:50 下午
 * @version
 */
object TmLoginManager {

    private const val MMKV_ID_USER = "mmkv_user"

    private const val KEY_UID = "key_uid"
    private const val KEY_Name = "key_name"
    private const val KEY_IS_INIT = "key_is_init"


    private var isFirstRegister = false

    private var isOpenContactPermission = false



    fun getFirstRegister() = isFirstRegister

    fun setFirstRegister(isFirstRegister: Boolean = false) {
        TmLoginManager.isFirstRegister = isFirstRegister
    }

    fun getIsOpenContactPermission() = isOpenContactPermission

    fun setIsOpenContactPermission(isOpenContactPermission: Boolean = false) {
        TmLoginManager.isOpenContactPermission = isOpenContactPermission
    }


    fun getUserId() = SpUtils.getString(MMKV_ID_USER, KEY_UID)

    fun setUserId(uid: String) {
        SpUtils.putString(MMKV_ID_USER, KEY_UID, uid)
    }

    fun getUserName() = SpUtils.getString(KEY_Name)

    fun setUserName(name: String) {
        SpUtils.putString(KEY_Name, name)
    }

    fun isSdkInit() = SpUtils.getBool(KEY_IS_INIT)

    fun finishInitSdk(isInit: Boolean) {
        SpUtils.putBool(KEY_IS_INIT, isInit)
    }




}