package com.tmmtmm.sdk.ui.view.recyclerview.decoration

import android.graphics.*
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tmmtmm.sdk.R
import com.tmmtmm.sdk.ui.ext.dpToPx
import com.tmmtmm.sdk.ui.ext.getColor
import com.tmmtmm.sdk.ui.view.recyclerview.listener.LinePrivacyDecorationItemListener

class CustomItemDecorationLine(var listener: LinePrivacyDecorationItemListener) :
    RecyclerView.ItemDecoration() {
    private val mPaint: Paint = Paint()
    private var mDividerHeight = 1
    private var decorationColor = 0
    private var leftMargin = 0
    private var bgColor = 0

    init {
        mPaint.isAntiAlias = true
        mPaint.color = Color.GRAY
        mDividerHeight = 0.5f.dpToPx()
        decorationColor = R.color.color_dadce2.getColor()
        bgColor = R.color.white.getColor()
    }


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (position != 0) {
            if (!isShow(position)) return
            outRect.top = mDividerHeight
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val index = parent.getChildAdapterPosition(view)
            if (index == 0) {
                continue
            }
            val dividerTop = view.top - mDividerHeight
            val dividerLeft = parent.paddingLeft
            val dividerRight = parent.width - parent.paddingRight
            drawDecoration(c, index, dividerTop, dividerLeft, dividerRight)
        }
    }


    private fun drawDecoration(c: Canvas, realPosition: Int, top: Int, left: Int, right: Int) {
        if (!isShow(realPosition)) return
        val decorationView = getDecorationItemView(realPosition) ?: return
        decorationView.setDecorationHeight(mDividerHeight)
        decorationView.setBgColor(bgColor)
        decorationView.setDecorationColor(decorationColor)
        if (!isMathWidth(realPosition)) {
            decorationView.setLeftMargin(leftMargin)
        }
        measureAndLayoutView(decorationView, left, right)
        val bitmap: Bitmap? = Bitmap.createBitmap(decorationView.drawingCache)
        c.drawBitmap(bitmap!!, left.toFloat(), top.toFloat(), null)
        bitmap.recycle()
    }


    private fun getDecorationItemView(index: Int): LineDecorationView? {
        return listener.getDecorationItemView(index)
    }

    private fun isMathWidth(index: Int): Boolean {
        return listener.isMathWidth(index) ?: true
    }

    private fun isShow(index: Int): Boolean {
        return listener.isShow(index) ?: true
    }

    /**
     * @param groupView groupView
     * @param left      left
     * @param right     right
     */
    private fun measureAndLayoutView(groupView: View, left: Int, right: Int) {
        groupView.isDrawingCacheEnabled = true
        val layoutParams = ViewGroup.LayoutParams(right, mDividerHeight)
        groupView.layoutParams = layoutParams
        groupView.measure(
            View.MeasureSpec.makeMeasureSpec(right, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(mDividerHeight, View.MeasureSpec.EXACTLY)
        )
        groupView.layout(left, 0 - mDividerHeight, right, 0)
    }


    class Builder private constructor(listener: LinePrivacyDecorationItemListener) {
        var mDecoration: CustomItemDecorationLine = CustomItemDecorationLine(listener)

        companion object {
            fun init(listener: LinePrivacyDecorationItemListener): Builder {
                return Builder(listener)
            }
        }

        fun build(): CustomItemDecorationLine {
            return mDecoration
        }

        fun setDecorationColor(color: Int): Builder {
            mDecoration.decorationColor = color
            return this
        }

        fun setBgColor(color: Int): Builder {
            mDecoration.bgColor = color
            return this
        }

        fun setLeftMargin(leftMargin: Int): Builder {
            mDecoration.leftMargin = leftMargin
            return this
        }

        fun setDividerHeight(height: Int): Builder {
            mDecoration.mDividerHeight = height
            return this
        }

    }

}