package com.tmmtmm.sdk.ui.view.recyclerview.decoration

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.tmmtmm.sdk.databinding.TextItemViewDecorationBinding
import com.tmmtmm.sdk.ui.ext.gone
import com.tmmtmm.sdk.ui.ext.visible


class TextItemLineDecorationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mBinging: TextItemViewDecorationBinding

    init {
        val inflater = LayoutInflater.from(context)
        mBinging = TextItemViewDecorationBinding.inflate(inflater, this, true)
    }

    fun setItemDecorationHeight(height: Int) {
        val layoutParams = mBinging.root.layoutParams
        layoutParams?.height = height
        mBinging.root.layoutParams = layoutParams
    }

    fun setDecorationHeight(height: Int) {
        val layoutTopParams = mBinging.topLineView.layoutParams
        layoutTopParams?.height = height
        mBinging.topLineView.layoutParams = layoutTopParams
        val layoutBottomParams = mBinging.bottomLineView.layoutParams
        layoutBottomParams?.height = height
        mBinging.bottomLineView.layoutParams = layoutBottomParams
    }

    fun getItemDecorationHeight(): Int {
        return mBinging.root.height ?: 0
    }


    fun setItemBgColor(color: Int) {
        mBinging.root.setBackgroundColor(color)
    }

    fun setDecorationColor(color: Int) {
        mBinging.topLineView.setBackgroundColor(color)
        mBinging.bottomLineView.setBackgroundColor(color)
    }

    fun setDecorationText(decorationText: String) {
        mBinging.decorationText.text = decorationText
    }

    fun setItemDecorationTextColor(textColor: Int) {
        mBinging.decorationText.setTextColor(textColor)
    }

    fun showTopLineView(isShow: Boolean) {
        if (isShow) {
            mBinging.topLineView.visible()
        } else {
            mBinging.topLineView.gone()
        }
    }

    fun showBottomLineView(isShow: Boolean) {
        if (isShow) {
            mBinging.topLineView.visible()
        } else {
            mBinging.topLineView.gone()
        }
    }

}