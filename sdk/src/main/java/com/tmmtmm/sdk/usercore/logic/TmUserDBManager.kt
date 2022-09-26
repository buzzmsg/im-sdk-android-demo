package com.tmmtmm.sdk.usercore.logic

import com.tmmtmm.sdk.core.db.DataBaseManager
import com.tmmtmm.sdk.usercore.db.UserModel

/**
 * @description
 * @version
 */
class TmUserDBManager private constructor() {
    companion object {

        private var instance: TmUserDBManager? = null


        @JvmName("getInstance1")
        fun getInstance(): TmUserDBManager {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = TmUserDBManager()
                    }
                }
            }
            return instance!!
        }
    }


    fun insertUser(userModel: UserModel){
        DataBaseManager.getInstance().getDataBase()?.userDao()?.insertUser(userModel)
    }
}