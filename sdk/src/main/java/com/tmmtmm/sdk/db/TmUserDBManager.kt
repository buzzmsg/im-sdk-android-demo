package com.tmmtmm.sdk.db

import com.tmmtmm.sdk.core.db.DataBaseManager

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


    fun insertUser(userModel: UserModel) {
        DataBaseManager.getInstance().getDataBase()?.userDao()?.insertUser(userModel)
    }

    fun insertUser(userModels: MutableList<UserModel>) {
        DataBaseManager.getInstance().getDataBase()?.userDao()?.insertUserList(userModels)
    }

    fun getUserList(uids: MutableList<String>?): MutableList<UserModel>? {
        if (uids.isNullOrEmpty()) {
            return null
        }
        val list = DataBaseManager.getInstance().splitArray(uids) { value ->
            DataBaseManager.getInstance().getDataBase()?.userDao()?.queryUserList(value)
                ?: mutableListOf()
        }
        return list
    }
}