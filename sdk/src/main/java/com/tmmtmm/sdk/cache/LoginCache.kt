package com.tmmtmm.sdk.cache

import com.tmmtmm.sdk.core.db.DataBaseManager
import com.tmmtmm.sdk.core.utils.SpUtils
import com.tmmtmm.sdk.db.model.ShareMeModel

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

    var me: ShareMeModel? = null


    fun getUserId(): String {
        return if (me == null) {
            me = DataBaseManager.getInstance().getShareDb()?.shareDao()?.getMe()
            me?.uid ?: ""
        } else {
            me?.uid ?: ""
        }
    }

    fun getAUserId(): String {
        return if (me == null) {
            me = DataBaseManager.getInstance().getShareDb()?.shareDao()?.getMe()
            me?.aUid ?: ""
        } else {
            me?.aUid ?: ""
        }
    }

    fun setUser(auid: String, uid: String) {
        val shareMeModel = ShareMeModel()
        shareMeModel.uid = uid
        shareMeModel.aUid = auid
        DataBaseManager.getInstance().getShareDb()?.shareDao()?.insertMe(shareMeModel)
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