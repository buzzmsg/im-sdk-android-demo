package com.im.sdk.ui.view.recyclerview.decoration

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.im.sdk.databinding.LineViewDecorationBinding

class LineDecorationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mBinging: LineViewDecorationBinding

    init {
        val inflater = LayoutInflater.from(context)
        mBinging = LineViewDecorationBinding.inflate(inflater, this, true)
    }


    fun setDecorationHeight(height: Int) {
        val layoutParams = mBinging.decoration.layoutParams
        layoutParams?.height = height
        mBinging.decoration.layoutParams = layoutParams
    }

    fun getDecorationHeight(): Int {
        return mBinging.decoration.height ?: 0
    }

    fun setDecorationColor(color: Int) {
        mBinging.decoration.setBackgroundColor(color)
    }

    fun setBgColor(color: Int) {
        mBinging.root.setBackgroundColor(color)
    }

    fun setLeftMargin(leftMargin: Int) {
        val layoutParams = mBinging.lineview.layoutParams
        layoutParams?.width = leftMargin
        mBinging.lineview.layoutParams = layoutParams
    }


}