package com.im.sdk.ui.view.vo

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class TmmQuoteMessageVo(
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("messageBody")
    var messageBody: String? = "",
    @SerializedName("type")
    var type: Int? = 0,
    @SerializedName("json")
    var json: String? = "",
    var attachment: String? = "",
    var mids: MutableList<String>? = null,
    var tmmMessageVo: TmmMessageVo? = null
) : Parcelable