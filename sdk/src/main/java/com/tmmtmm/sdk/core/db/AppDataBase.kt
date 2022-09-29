package com.tmmtmm.sdk.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tmmtmm.sdk.db.ConversationDao
import com.tmmtmm.sdk.db.MessageDao
import com.tmmtmm.sdk.db.UserDao
import com.tmmtmm.sdk.db.model.*

/**
 * @description
 * @version
 */
@Database(
    entities =
    [
        UserLinkModel::class,
        UserModel::class,
        MessageModel::class,
        ConversationModel::class,
        ConversationLinkModel::class
    ],
    version = 1,
    exportSchema = true,
    autoMigrations = [

    ]
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao

}