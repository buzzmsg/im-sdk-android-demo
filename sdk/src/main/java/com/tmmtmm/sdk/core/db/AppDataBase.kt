package com.tmmtmm.sdk.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tmmtmm.sdk.db.ConversationDao
import com.tmmtmm.sdk.db.MessageDao
import com.tmmtmm.sdk.db.UserDao
import com.tmmtmm.sdk.db.model.ConversationModel
import com.tmmtmm.sdk.db.model.MessageModel
import com.tmmtmm.sdk.db.model.UserLinkModel
import com.tmmtmm.sdk.db.model.UserModel

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
        ConversationModel::class
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