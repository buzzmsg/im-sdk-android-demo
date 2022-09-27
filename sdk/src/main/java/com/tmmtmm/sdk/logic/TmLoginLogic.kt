package com.tmmtmm.sdk.logic

import androidx.annotation.WorkerThread
import com.tmmtmm.sdk.db.TmUserDBManager
import com.tmmtmm.sdk.db.UserModel

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

//        val userModel =  UserModel()
//        userModel.aUid = aUid
//        userModel.uid = uid
//        TmUserDBManager.getInstance().insertUser(userModel)

        callBack.success()

    }

    interface LoginCallBack {
        fun success()
        fun fail(code: Int, errorMsg: String)
    }
}