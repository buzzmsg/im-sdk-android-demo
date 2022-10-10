package com.im.sdk.ui.view.recyclerview.decoration

import android.graphics.*
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.im.sdk.R
import com.im.sdk.ui.ext.dpToPx
import com.im.sdk.ui.ext.getColor
import com.im.sdk.ui.view.recyclerview.listener.LineDecorationItemListener
import com.im.sdk.ui.view.recyclerview.util.ChatItemHeightUtil


enum class BottomDecorationType {
    none,
    visible,
    gone,

    //Not full screen display  more than screen hidden
    noScreenShow
}

class MoreItemDecoration(var listener: LineDecorationItemListener) :
    RecyclerView.ItemDecoration() {
    private val mPaint: Paint = Paint()
    private var mDividerHeight = 1
    private var mItemDividerHeight = 1
    private var decorationColor = 0
    private var itemDecorationBgColor = 0
    private var itemDecorationTextColor = 0
    private var leftMargin = 0
    private var adapterItemBg: Int? = null
    private var bottomDecorationType: BottomDecorationType = BottomDecorationType.none
    private var adapter: RecyclerView.Adapter<*>? = null

    init {
        mPaint.isAntiAlias = true
        mPaint.color = Color.GRAY
        mDividerHeight = 0.5f.dpToPx()
        mItemDividerHeight = 0.5f.dpToPx()
        decorationColor = R.color.color_dadce2.getColor()
        itemDecorationBgColor = R.color.color_EFF0F3.getColor()
        itemDecorationTextColor = R.color.text_A2A8C3.getColor()
    }


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (!isShow(position, false)) return
        val headNameString = getHeaderName(position)
        if (headNameString.isNullOrEmpty()) {
            if (position != 0) {
                outRect.top = mDividerHeight
            }
        } else {
            outRect.top = mItemDividerHeight
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val childCount = parent.childCount
        val dividerLeft = parent.paddingLeft
        val dividerRight = parent.width - parent.paddingRight
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val index = parent.getChildAdapterPosition(view)
            val headNameString = getHeaderName(index)
            if (index == 0 && headNameString.isNullOrEmpty()) {
                continue
            }
            if (headNameString.isNullOrEmpty()) {
                drawDecoration(c, index, view.top - mDividerHeight, dividerLeft, dividerRight)
            } else {
                drawItemDecoration(c, index, view.top, dividerLeft, dividerRight, headNameString)
            }
        }
        if (adapter != null) {
            val view = try {
                parent.getChildAt(childCount - 1)
            } catch (e: Exception) {
                null
            } ?: return
            val index = parent.getChildAdapterPosition(view)
            if (index == ((adapter?.itemCount ?: 0) - 1)) {
                var isShow = true
                if (bottomDecorationType == BottomDecorationType.gone) isShow = false
                if (bottomDecorationType == BottomDecorationType.noScreenShow) {
                    if (isNeedChangeChatTopPlaneHeight(
                            parent,
                            ScreenUtils.getScreenHeight()
                        )
                    ) {
                        isShow = false
                    }
                }
                if (isShow) {
                    bottomDecorationType = BottomDecorationType.visible
                    val bottom = try {
                        view.bottom
                    } catch (e: Exception) {
                        view.top + view.height
                    }
                    drawDecoration(c, index, bottom, dividerLeft, dividerRight, true)
                } else {
                    bottomDecorationType = BottomDecorationType.gone
                }
            }
        }
    }


    private fun drawDecoration(
        c: Canvas,
        realPosition: Int,
        top: Int,
        left: Int,
        right: Int,
        isEndDecoration: Boolean = false
    ) {
        if (!isShow(realPosition, isEndDecoration)) return
        val decorationView = getDecorationItemView(realPosition) ?: return
        decorationView.setDecorationHeight(mDividerHeight)
        adapterItemBg?.let {
            decorationView.setBgColor(it)
        } ?: run {
            decorationView.setBgColor(getDecorationBgColor(realPosition, isEndDecoration))
        }
        decorationView.setDecorationColor(decorationColor)
        if (!isMathWidth(realPosition, isEndDecoration)) {
            decorationView.setLeftMargin(leftMargin)
        }
        measureAndLayoutView(decorationView, left, right, mDividerHeight)
        val bitmap: Bitmap? = Bitmap.createBitmap(decorationView.drawingCache)
        c.drawBitmap(bitmap!!, left.toFloat(), top.toFloat(), null)
        bitmap.recycle()
    }

    private fun drawItemDecoration(
        c: Canvas,
        realPosition: Int,
        viewTop: Int,
        left: Int,
        right: Int,
        headNameString: String
    ) {
        if (!isShow(realPosition, false)) return
        val top = viewTop - mItemDividerHeight
        val decorationView = getItemDecorationItemView(realPosition)
        if (decorationView == null) {
            drawDecoration(c, realPosition, top, left, right)
            return
        }
        decorationView.setDecorationText(headNameString)
        decorationView.setItemDecorationHeight(mItemDividerHeight)
        decorationView.setDecorationHeight(mDividerHeight)
        decorationView.setDecorationColor(decorationColor)
        decorationView.setItemBgColor(itemDecorationBgColor)
        decorationView.setItemDecorationTextColor(itemDecorationTextColor)
        if (realPosition == 0) decorationView.showTopLineView(false)
        measureAndLayoutView(decorationView, left, right, mItemDividerHeight)
        val bitmap: Bitmap? = Bitmap.createBitmap(decorationView.drawingCache)
        c.drawBitmap(bitmap!!, left.toFloat(), top.toFloat(), null)
        bitmap.recycle()
    }


    private fun getDecorationItemView(index: Int): LineDecorationView? {
        return listener.getDecorationItemView(index)
    }


    fun getHeaderName(index: Int): String? {
        val currentName = listener.getHeaderName(index)
        if (index - 1 < 0) return currentName
        val lastName = listener.getHeaderName(index - 1)
        if (currentName.isNullOrEmpty() || lastName.isNullOrEmpty()) {
            return currentName
        }
        if (currentName == lastName) {
            return null
        }
        return currentName
    }

    private fun getItemDecorationItemView(index: Int): TextItemLineDecorationView? {
        return listener.getItemDecorationItemView(index)
    }

    private fun isMathWidth(index: Int, isBottomEndDecoration: Boolean): Boolean {
        return listener.isMathWidth(index, isBottomEndDecoration) ?: true
    }

    private fun getDecorationBgColor(index: Int, isBottomEndDecoration: Boolean): Int {
        return listener.decorationBgColor(index, isBottomEndDecoration) ?: R.color.white.getColor()
    }

    private fun isNeedChangeChatTopPlaneHeight(parent: RecyclerView, height: Int): Boolean {
        return ChatItemHeightUtil.itemHeightThanValue(parent, height)
    }


    private fun isShow(index: Int, isBottomEndDecoration: Boolean): Boolean {
        return listener.isShow(index, isBottomEndDecoration) ?: true
    }

    /**
     * @param groupView groupView
     * @param left      left
     * @param right     right
     */
    private fun measureAndLayoutView(groupView: View, left: Int, right: Int, mHeight: Int) {
        groupView.isDrawingCacheEnabled = true
        val layoutParams = ViewGroup.LayoutParams(right, mHeight)
        groupView.layoutParams = layoutParams
        groupView.measure(
            View.MeasureSpec.makeMeasureSpec(right, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(mHeight, View.MeasureSpec.EXACTLY)
        )
        groupView.layout(left, 0 - mHeight, right, 0)
    }


    class Builder private constructor(listener: LineDecorationItemListener) {
        var mDecoration: MoreItemDecoration = MoreItemDecoration(listener)

        companion object {
            fun init(listener: LineDecorationItemListener): Builder {
                return Builder(listener)
            }
        }

        fun build(): MoreItemDecoration {
            initBottomShowType()
            return mDecoration
        }

        private fun initBottomShowType() {
            if (mDecoration.bottomDecorationType == BottomDecorationType.none) {
                if (mDecoration.adapter == null) {
                    mDecoration.bottomDecorationType = BottomDecorationType.gone
                } else {
                    mDecoration.bottomDecorationType = BottomDecorationType.visible
                }
            }
        }


        fun setDecorationColor(color: Int): Builder {
            mDecoration.decorationColor = color
            return this
        }

        fun setAdapterItemBg(color: Int?): Builder {
            mDecoration.adapterItemBg = color
            return this
        }

        fun setItemDecorationBgColor(color: Int): Builder {
            mDecoration.itemDecorationBgColor = color
            return this
        }

        fun setItemDecorationTextColor(textcolor: Int): Builder {
            mDecoration.itemDecorationTextColor = textcolor
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

        fun setItemDividerHeight(height: Int): Builder {
            mDecoration.mItemDividerHeight = height
            return this
        }

        fun setShowBottomDecoration(adapter: RecyclerView.Adapter<*>? = null): Builder {
            mDecoration.adapter = adapter
            return this
        }


        fun setBottomDecorationType(bottomDecorationType: BottomDecorationType): Builder {
            mDecoration.bottomDecorationType = bottomDecorationType
            return this
        }

    }

}