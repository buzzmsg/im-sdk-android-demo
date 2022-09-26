package com.tmmtmm.sdk.usercore.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @description
 * @version
 */
@Entity(tableName = "tmm_user")
class UserModel {

    @PrimaryKey
    var aUid: String = ""

    var uid: String = ""
}