package com.tmmtmm.sdk.core.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * @description
 * @version
 */
@Database(
    entities =
    [
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = [

    ]
)
abstract class AppDataBase : RoomDatabase() {

}