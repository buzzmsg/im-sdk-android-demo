package com.im.sdk.ui.view.recyclerview.listener

import com.im.sdk.ui.view.recyclerview.decoration.LineDecorationView


/**
 * @description
 * @time 2022/1/17 5:21 下午
 */
interface LinePrivacyDecorationItemListener {
    fun isShow(position: Int): Boolean?
    fun isMathWidth(position: Int): Boolean?
    fun getDecorationItemView(position: Int): LineDecorationView?
}