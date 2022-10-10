package com.im.sdk.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * @description
 * @version
 */
@Entity(
    tableName = "tmm_conversation_link",
    indices = [Index("chatId", unique = true)]
)
class ConversationLinkModel {
    @PrimaryKey
    var aChatId: String = ""

    var chatId: String = ""
}