package com.im.sdk.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.im.sdk.db.ShareDao
import com.im.sdk.db.model.ShareMeModel

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