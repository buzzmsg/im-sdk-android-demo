package com.tmmtmm.sdk.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * @description
 * @version
 */
@Entity(
    tableName = "tmm_user_link",
    indices = [Index("uid", unique = true)]
)
class UserLinkModel {
    @PrimaryKey
    var aUid: String = ""

    var uid: String = ""
}