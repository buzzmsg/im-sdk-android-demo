package com.tmmtmm.sdk.logic

import androidx.annotation.WorkerThread
import com.tmmtmm.sdk.db.UserDBManager
import com.tmmtmm.sdk.db.model.UserLinkModel

/**
 * @description
 * @version
 */
class TmLoginLogic private constructor() {
    companion object {

        private var instance: TmLoginLogic? = null


        @JvmName("getInstance")
        fun getInstance(): TmLoginLogic {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = TmLoginLogic()
                    }
                }
            }
            return instance!!
        }
    }


    fun isLogin(): Boolean {
        return TmLoginManager.getUserId().isNotBlank()
    }

    @WorkerThread
    fun login(aUid: String, callBack: LoginCallBack) {

        val uid = ""
        TmLoginManager.setUserId(uid)

        val userModel =  UserLinkModel()
        userModel.aUid = aUid
        userModel.uid = uid
        UserDBManager.getInstance().insertUserLink(userModel)

        callBack.success()

    }

    interface LoginCallBack {
        fun success()
        fun fail(code: Int, errorMsg: String)
    }
}