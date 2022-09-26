package com.tmmtmm.sdk.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tmmtmm.sdk.usercore.db.UserDao
import com.tmmtmm.sdk.usercore.db.UserModel

/**
 * @description
 * @version
 */
@Database(
    entities =
    [
        UserModel::class
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = [

    ]
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
}