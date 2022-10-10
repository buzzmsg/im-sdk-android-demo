package com.im.sdk.db.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * @description
 * @version
 */
@Entity(
    tableName = "tmm_share_me",
    indices = [Index("uid", unique = true)]
)
class ShareMeModel {
    @PrimaryKey
    var aUid: String = ""
    var uid: String = ""
}