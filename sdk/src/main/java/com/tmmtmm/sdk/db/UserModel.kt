package com.tmmtmm.sdk.db

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
}