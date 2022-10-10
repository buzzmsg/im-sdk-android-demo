package com.im.sdk.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @description
 * @version
 */
@Entity(tableName = "tmm_user")
class UserModel {

    @PrimaryKey
    var uid: String = ""

    var aUid: String = ""
}