package com.im.sdk.db

import androidx.room.*
import com.im.sdk.core.db.DataBaseManager
import com.im.sdk.db.model.ShareMeModel

/**
 * @description
 * @version
 */
class ShareDBManager private constructor() {
    companion object {

        private var instance: ShareDBManager? = null


        @JvmName("getInstance1")
        fun getInstance(): ShareDBManager {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ShareDBManager()
                    }
                }
            }
            return instance!!
        }
    }

    fun insertMe(userModel: ShareMeModel) {
        DataBaseManager.getInstance().getShareDb()?.shareDao()?.deleteAll()
        DataBaseManager.getInstance().getShareDb()?.shareDao()?.insertMe(userModel)
    }

    fun getMe(): ShareMeModel? {
        return DataBaseManager.getInstance().getShareDb()?.shareDao()?.getMe()
    }

}

@Dao
interface ShareDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMe(userModel: ShareMeModel?)

    @Query("select * from tmm_share_me limit 1")
    fun getMe(): ShareMeModel?

    @Query("delete from tmm_share_me")
    fun deleteAll()
}