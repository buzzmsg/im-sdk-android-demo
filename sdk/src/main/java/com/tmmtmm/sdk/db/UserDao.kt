package com.tmmtmm.sdk.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * @description
 * @version
 */
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
}