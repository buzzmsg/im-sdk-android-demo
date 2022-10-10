package com.im.sdk.ui.view.recyclerview.util

import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

object ChatItemHeightUtil {
    private val TAG = "ChatItemHeightUtil"

    fun itemHeightThanValue(recycleView: RecyclerView, thanHeight: Int): Boolean {
        if (thanHeight <= 0) return true
        if (recycleView.childCount < 1) return false
        var itemHeight = 0
        var endView: View? = null
        for (view in recycleView.children) {
            itemHeight += getViewHeight(view)
            endView = view
            if (itemHeight > thanHeight) return true
        }
        if (endView?.bottom ?: 0 > thanHeight) return true
        return false
    }


    private fun getViewHeight(view: View): Int {
        return view.height
    }

}