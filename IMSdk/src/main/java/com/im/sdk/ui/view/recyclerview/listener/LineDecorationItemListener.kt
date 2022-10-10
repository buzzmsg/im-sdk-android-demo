package com.im.sdk.ui.view.recyclerview.listener

import com.im.sdk.ui.view.recyclerview.decoration.LineDecorationView
import com.im.sdk.ui.view.recyclerview.decoration.TextItemLineDecorationView


/**
 * @description
 * @time 2022/1/17 5:21 下午
 */
interface LineDecorationItemListener {
    fun isShow(position: Int, isBottomEndDecoration: Boolean): Boolean?
    fun decorationBgColor(position: Int, isBottomEndDecoration: Boolean): Int?
    fun isMathWidth(position: Int, isBottomEndDecoration: Boolean): Boolean?
    fun getHeaderName(position: Int): String?
    fun getDecorationItemView(position: Int): LineDecorationView?
    fun getItemDecorationItemView(position: Int): TextItemLineDecorationView?
}