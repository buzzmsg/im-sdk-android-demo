package com.tmmtmm.sdk.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tmmtmm.sdk.db.ConversationDao
import com.tmmtmm.sdk.db.MessageDao
import com.tmmtmm.sdk.db.ShareDao
import com.tmmtmm.sdk.db.UserDao
import com.tmmtmm.sdk.db.model.*

/**
 * @description
 * @version
 */
@Database(
    entities =
    [
        ShareMeModel::class,
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = [

    ]
)
abstract class ShareDataBase : RoomDatabase() {
    abstract fun shareDao(): ShareDao

}