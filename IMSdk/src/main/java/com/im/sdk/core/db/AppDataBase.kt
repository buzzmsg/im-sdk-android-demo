package com.im.sdk.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.im.sdk.db.model.UserModel
import com.im.sdk.db.ConversationDao
import com.im.sdk.db.MessageDao
import com.im.sdk.db.UserDao
import com.im.sdk.db.model.ConversationModel
import com.im.sdk.db.model.MessageModel

/**
 * @description
 * @version
 */
@Database(
    entities =
    [
        UserModel::class,
        MessageModel::class,
        ConversationModel::class,
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