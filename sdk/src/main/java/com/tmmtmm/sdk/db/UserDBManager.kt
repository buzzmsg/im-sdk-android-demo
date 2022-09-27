package com.tmmtmm.sdk.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tmmtmm.sdk.core.db.DataBaseManager
import com.tmmtmm.sdk.db.model.UserLinkModel
import com.tmmtmm.sdk.db.model.UserModel

/**
 * @description
 * @version
 */
class UserDBManager private constructor() {
    companion object {

        private var instance: UserDBManager? = null


        @JvmName("getInstance1")
        fun getInstance(): UserDBManager {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = UserDBManager()
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



    fun insertUserLink(userModel: UserLinkModel) {
        DataBaseManager.getInstance().getDataBase()?.userDao()?.insertUserLink(userModel)
    }

    fun insertUserLink(userModels: MutableList<UserLinkModel>) {
        DataBaseManager.getInstance().getDataBase()?.userDao()?.insertUserLinkList(userModels)
    }

    fun getUserLinkList(uids: MutableList<String>?): MutableList<UserLinkModel>? {
        if (uids.isNullOrEmpty()) {
            return null
        }
        val list = DataBaseManager.getInstance().splitArray(uids) { value ->
            DataBaseManager.getInstance().getDataBase()?.userDao()?.queryUserLinkList(value)
                ?: mutableListOf()
        }
        return list
    }
}

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(userModel: UserModel?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserList(userModels: List<UserModel>?)

    @Query("select * from tmm_user where uid = :uid")
    fun queryUser(uid: String?): UserModel?

    @Query("select * from tmm_user where uid in (:uids)")
    fun queryUserList(uids: MutableList<String>?): MutableList<UserModel>?




    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserLink(userModel: UserLinkModel?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserLinkList(userModels: List<UserLinkModel>?)

    @Query("select * from tmm_user_link where uid = :uid")
    fun queryUserLink(uid: String?): UserLinkModel?

    @Query("select * from tmm_user_link where uid in (:uids)")
    fun queryUserLinkList(uids: MutableList<String>?): MutableList<UserLinkModel>?
}