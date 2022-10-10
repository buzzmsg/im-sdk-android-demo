package com.im.sdk.ui.view.recyclerview.listener

import android.view.View

/**
 * @description
 * @time 2022/1/17 5:21 下午
 */
interface ChatAvatarDecorationListener : GroupListener {
    fun getGroupView(position: Int): View?
    fun getUserImageView(position: Int): View?
    fun getSeeHistoryView(position: Int): View?
    fun getUserNameView(position: Int): View?
    fun lastIsUser(position: Int): Boolean?
    fun isTransMessage(position: Int): Boolean?
    fun isMeMessage(position: Int): Boolean?
    fun isGroup(position: Int): Boolean?
    fun getUidView(position: Int): String?
}