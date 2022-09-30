package com.tmmtmm.sdk.ui.view.recyclerview.decoration

import android.graphics.*
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tmmtmm.sdk.ui.ext.dpToPx
import com.tmmtmm.sdk.ui.view.message.ChatSelectTransBoxView
import com.tmmtmm.sdk.ui.view.recyclerview.click.ClickInfo
import com.tmmtmm.sdk.ui.view.recyclerview.listener.ChatAvatarDecorationListener
import com.tmmtmm.sdk.ui.view.recyclerview.listener.OnAvatarClickListener
import com.tmmtmm.sdk.ui.view.recyclerview.listener.OnGroupClickListener

import com.tmmtmm.sdk.ui.view.recyclerview.util.CacheStringUtil
import com.tmmtmm.sdk.ui.view.recyclerview.util.CacheUtil
import com.tmmtmm.sdk.ui.view.recyclerview.util.ViewUtil

/**
 * @description
 * @time 2022/1/17 5:16 下午
 */
class ChatAvatarStickyDecoration private constructor(groupListener: ChatAvatarDecorationListener) :
    BaseDecoration() {
    private val mPaint = Paint()

    private val mmPaint = Paint()

    /**
     *
     */
    private val mBitmapCache = CacheUtil<Bitmap?>()
    private val mAvatarBitmapCache = CacheStringUtil<Bitmap?>()

    /**
     *
     */
    private val mHeadViewCache = CacheUtil<View?>()
    private val mAvatarViewCache = CacheStringUtil<View?>()
    private var mGroupListener: ChatAvatarDecorationListener? = null

    init {
        mGroupListener = groupListener
        mPaint.isAntiAlias = true
        mPaint.color = Color.BLACK
        mmPaint.isAntiAlias = true
        mmPaint.color = Color.RED
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        val realPosition = getRealPosition(position)
        if (outRect.top == mDivideHeight) {
            if (isTransMessage(realPosition)) {
                if (isLastTransMessage(realPosition)) {
                    outRect.top = mTransMessageHeight
                }
            } else if (lastIsUser(realPosition)) {
                outRect.top = mUserDivideHeight
            } else if (isGroup(realPosition) && !isMeMessage(realPosition)) {
                outRect.top = outRect.top + mNameHeight
            }
        } else if (outRect.top == mGroupHeight) {
            var isChangeY = isGroup(realPosition)
            if (isChangeY) isChangeY = !isMeMessage(realPosition)
            if (isChangeY) isChangeY = !isTransMessage(realPosition)
            if (isChangeY && realPosition != 0) outRect.top = outRect.top + mNameHeight
        }
    }


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val itemCount = state.itemCount
        val childCount = parent.childCount
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        for (i in 0 until childCount) {
            val childView = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(childView)
            val realPosition = getRealPosition(position)
            var isGroupHeight = false
            if (isFirstInGroup(realPosition) || isFirstInRecyclerView(realPosition, i)) {
                val viewBottom = childView.bottom
                //top
                var bottom = if (!mSticky) childView.top else Math.max(
                    mGroupHeight,
                    childView.top + parent.paddingTop
                )
                if (mSticky && position + 1 < itemCount) {
                    if (isLastLineInGroup(parent, realPosition) && viewBottom < bottom) {
                        bottom = viewBottom
                    }
                }
                drawDecoration(c, realPosition, left, right, bottom)
                isGroupHeight = true
            } else {
                drawDivide(c, parent, childView, realPosition, left, right)
                isGroupHeight = false
            }
//            drawUserImage(c, realPosition, childView, mGroupHeight, i, parent, isGroupHeight)
//            drawUserName(c, realPosition, childView, mGroupHeight, i, parent, isGroupHeight)
        }
    }

    private fun getAvatarLeftValue(childView: View): Int {
        if (childView !is ViewGroup) return 0
        childView.children.forEach {
            Log.w("TAG", "getAvatarLeftValue: ${it is ChatSelectTransBoxView}")
            if (it is ChatSelectTransBoxView) {
                if (it.isVisible) {
                    return 40f.dpToPx()
                }
            }
        }
        return 0
    }


    private fun drawUserImage(
        c: Canvas,
        realPosition: Int,
        childView: View,
        divideHeight: Int,
        index: Int,
        parent: RecyclerView,
        isGroupHeight: Boolean,
    ) {
        val x = 16f.dpToPx() + getAvatarLeftValue(childView)
        if (!isGroup(realPosition)) return
        if (isMeMessage(realPosition)) return
        if (isTransMessage(realPosition)) return
        if (lastIsUser(realPosition)) {
            if (!isGroupHeight) {
                return
            }
            if (childView.top < mDivideHeight) {
                return
            }
        }

        val y = childView.top - mNameHeight

        val userId = getUserId(realPosition)
        if (userId.isEmpty()) {
            return
        }
        if (mAvatarBitmapCache[userId] != null) {
            try {
                val bitmap = mAvatarBitmapCache[userId]
                c.drawBitmap(bitmap!!, x.toFloat(), y.toFloat(), null)
            } catch (e: Exception) {
            }
        }

        val avatarView: View = getUserAvatarView(realPosition) ?: return
        measureAndLayoutView(avatarView, x, x + mAvatarSize)
        val bitmap: Bitmap? = Bitmap.createBitmap(avatarView.drawingCache)
        c.drawBitmap(bitmap!!, x.toFloat(), y.toFloat(), null)
        bitmap.recycle()
        mAvatarBitmapCache.put(userId, Bitmap.createBitmap(avatarView.drawingCache))

        if (mOnAvatarClickListener != null) {
            val top = y - mDivideHeight
            val left = x - 4f.dpToPx()
            val bottom = y + mDivideHeight + 32f.dpToPx()
            val right = x + 40f.dpToPx()
            setAvatarClickInfo(avatarView, left, top, right, bottom, bottom, realPosition)
        }
    }


    private fun drawUserName(
        c: Canvas,
        realPosition: Int,
        childView: View,
        divideHeight: Int,
        index: Int,
        parent: RecyclerView,
        isGroupHeight: Boolean,
    ) {
        if (!isGroup(realPosition)) return
        if (isMeMessage(realPosition)) return
        if (isTransMessage(realPosition)) return

        if (lastIsUser(realPosition)) {
            if (!isGroupHeight) {
                return
            }
            if (childView.top < mDivideHeight) {
                return
            }
        }
        val y = childView.top - mNameHeight
        val x = (16f + 8).dpToPx() + mAvatarSize + getAvatarLeftValue(childView)
        val nameView: View = getUserNameView(realPosition) ?: return
        measureAndLayoutView(nameView, x, x + (216f.dpToPx()))
        val bitmap: Bitmap? = Bitmap.createBitmap(nameView.drawingCache)
        c.drawBitmap(bitmap!!, x.toFloat(), y.toFloat(), null)
        bitmap.recycle()
    }


    /**
     * @param c            Canvas
     * @param realPosition realPosition
     * @param left         left
     * @param right        right
     * @param bottom       bottom
     */
    private fun drawDecoration(c: Canvas, realPosition: Int, left: Int, right: Int, bottom: Int) {
        var isChangeY = isGroup(realPosition)
        if (isChangeY) isChangeY = !isMeMessage(realPosition)
        if (isChangeY) isChangeY = !isTransMessage(realPosition)
        var top = bottom - mGroupHeight
        if (isChangeY && top > 0) top -= (mNameHeight + mDivideHeight)
        if (isGroup(realPosition) && isMeMessage(realPosition) && top > 0) top -= mDivideHeight / 2
        if (isGroup(realPosition) && isTransMessage(realPosition) && top > 0) top -= mDivideHeight / 2
        if (!isGroup(realPosition) && top > 0) top -= mDivideHeight / 2
        c.drawRect(
            left.toFloat(),
            top.toFloat(),
            right.toFloat(),
            bottom.toFloat(),
            mPaint
        )
        val groupView: View?
        val firstPositionInGroup = getFirstInGroupWithCash(realPosition)
        if (mHeadViewCache[firstPositionInGroup] == null) {
            groupView = getGroupView(firstPositionInGroup)
            if (groupView == null) {
                return
            }
            measureAndLayoutView(groupView, left, right)
            mHeadViewCache.put(firstPositionInGroup, groupView)
        } else {
            groupView = mHeadViewCache[firstPositionInGroup]
        }
        val bitmap: Bitmap?
        if (mBitmapCache[firstPositionInGroup] != null) {
            bitmap = mBitmapCache[firstPositionInGroup]
        } else {
            bitmap = Bitmap.createBitmap(groupView!!.drawingCache)
            mBitmapCache.put(firstPositionInGroup, bitmap)
        }

        val x = left.toFloat()

        c.drawBitmap(bitmap!!, x, top.toFloat(), null)
        if (mOnGroupClickListener != null) {
            setClickInfo(groupView, left, bottom, realPosition)
        }
    }

    /**
     * @param groupView groupView
     * @param left      left
     * @param right     right
     */
    private fun measureAndLayoutView(groupView: View, left: Int, right: Int) {
        groupView.isDrawingCacheEnabled = true
        val layoutParams = ViewGroup.LayoutParams(right, mGroupHeight)
        groupView.layoutParams = layoutParams
        groupView.measure(
            View.MeasureSpec.makeMeasureSpec(right, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(mGroupHeight, View.MeasureSpec.EXACTLY)
        )
        groupView.layout(left, 0 - mGroupHeight, right, 0)
    }

    /**
     * @param groupView
     * @param parentBottom
     * @param realPosition
     */
    private fun setClickInfo(
        groupView: View?,
        parentLeft: Int,
        parentBottom: Int,
        realPosition: Int
    ) {
        val parentTop = parentBottom - mGroupHeight
        val list: MutableList<ClickInfo.DetailInfo> = ArrayList()
        val viewList = ViewUtil.getChildViewWithId(groupView)
        for (view in viewList) {
            val top = view.top + parentTop
            val bottom = view.bottom + parentTop
            val left = view.left + parentLeft
            val right = view.right + parentLeft
            list.add(ClickInfo.DetailInfo(view.id, left, right, top, bottom))
        }
        val clickInfo = ClickInfo(parentBottom, list)
        clickInfo.mGroupId = groupView!!.id
        clickInfo.mTag = (groupView.tag ?: "").toString()
        stickyHeaderPosArray[realPosition] = clickInfo
    }

    private fun setAvatarClickInfo(
        groupView: View?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        parentBottom: Int,
        realPosition: Int
    ) {
        val parentTop = parentBottom - mGroupHeight
        val list: MutableList<ClickInfo.DetailInfo> = ArrayList()
        val viewList = ViewUtil.getChildViewWithId(groupView)
        for (view in viewList) {
            list.add(ClickInfo.DetailInfo(view.id, left, right, top, bottom))
        }
        val clickInfo = ClickInfo(parentBottom, list)
        clickInfo.mGroupId = groupView!!.id
        clickInfo.mTag = (groupView.tag ?: "").toString()
        stickyHeaderPosArray[realPosition] = clickInfo
    }

    /**
     * @param realPosition realPosition
     * @return
     */
    public override fun getGroupName(realPosition: Int): String {
        return mGroupListener?.getGroupName(realPosition) ?: ""
    }

    /**
     * @param realPosition realPosition
     * @return
     */
    private fun getGroupView(realPosition: Int): View? {
        return mGroupListener?.getGroupView(realPosition)
    }

    /**
     * @param realPosition realPosition
     * @return
     */
    private fun getUserAvatarView(realPosition: Int): View? {
        return mGroupListener?.getUserImageView(realPosition)
    }

    /**
     * @param realPosition realPosition
     * @return
     */
    private fun getUserNameView(realPosition: Int): View? {
        return mGroupListener?.getUserNameView(realPosition)
    }

    /**
     * @param realPosition realPosition
     * @return
     */
    private fun isTransMessage(realPosition: Int): Boolean {
        return mGroupListener?.isTransMessage(realPosition) ?: false
    }

    /**
     * @param realPosition realPosition
     * @return
     */
    private fun isLastTransMessage(realPosition: Int): Boolean {
        return mGroupListener?.isTransMessage(realPosition - 1) ?: false
    }

    /**
     * @param realPosition realPosition
     * @return
     */
    private fun isMeMessage(realPosition: Int): Boolean {
        return mGroupListener?.isMeMessage(realPosition) ?: false
    }

    /**
     * @param realPosition realPosition
     * @return
     */
    private fun isGroup(realPosition: Int): Boolean {
        return mGroupListener?.isGroup(realPosition) ?: false
    }

    /**
     * @param realPosition realPosition
     * @return
     */
    private fun lastIsUser(realPosition: Int): Boolean {
        return mGroupListener?.lastIsUser(realPosition) ?: false
    }

    /**
     * @param realPosition realPosition
     * @return
     */
    private fun nextIsUser(realPosition: Int): Boolean {
        return mGroupListener?.lastIsUser(realPosition + 1) ?: false
    }

    /**
     * @param realPosition realPosition
     * @return
     */
    private fun getUserId(realPosition: Int): String {
        return mGroupListener?.getUidView(realPosition) ?: ""
    }

    /**
     * @param b b
     */
    fun setCacheEnable(b: Boolean) {
        mHeadViewCache.isCacheable(b)
        mBitmapCache.isCacheable(b)
    }

    /**
     *
     */
    fun clearCache() {
        mHeadViewCache.clean()
        mBitmapCache.clean()
    }

    /**
     * @param recyclerView recyclerView
     * @param realPosition realPosition
     */
    fun notifyRedraw(recyclerView: RecyclerView, viewGroup: View, realPosition: Int) {
        viewGroup.isDrawingCacheEnabled = false
        val firstPositionInGroup = getFirstInGroupWithCash(realPosition)
        mBitmapCache.remove(firstPositionInGroup)
        mHeadViewCache.remove(firstPositionInGroup)
        val left = recyclerView.paddingLeft
        val right = recyclerView.width - recyclerView.paddingRight
        measureAndLayoutView(viewGroup, left, right)
        mHeadViewCache.put(firstPositionInGroup, viewGroup)
        recyclerView.invalidate()
    }

    class Builder private constructor(listener: ChatAvatarDecorationListener) {
        var mDecoration: ChatAvatarStickyDecoration

        /**
         * @param groutHeight
         * @return this
         */
        fun setGroupHeight(groutHeight: Int): Builder {
            mDecoration.mGroupHeight = groutHeight
            return this
        }

        /**
         * @param background
         */
        fun setGroupBackground(@ColorInt background: Int): Builder {
            mDecoration.mGroupBackground = background
            mDecoration.mPaint.color = mDecoration.mGroupBackground
            return this
        }

        /**
         * @param height
         * @return this
         */
        fun setDivideHeight(height: Int): Builder {
            mDecoration.mDivideHeight = height
            return this
        }

        /**
         * @param height
         * @return this
         */
        fun setUserDivideHeight(height: Int): Builder {
            mDecoration.mUserDivideHeight = height
            return this
        }

        /**
         * @param height
         * @return this
         */
        fun setNameHeight(height: Int): Builder {
            mDecoration.mNameHeight = height
            return this
        }

        /**
         * @param height
         * @return this
         */
        fun setAvatarSize(size: Int): Builder {
            mDecoration.mAvatarSize = size
            return this
        }

        /**
         * @param height
         * @return this
         */
        fun setTransMessageHeight(height: Int): Builder {
            mDecoration.mTransMessageHeight = height
            return this
        }

        /**
         * @param color color
         * @return this
         */
        fun setDivideColor(@ColorInt color: Int): Builder {
            mDecoration.mDivideColor = color
            mDecoration.mDividePaint.color = mDecoration.mDivideColor
            return this
        }

        /**
         * @param listener
         * @return this
         */
        fun setOnClickListener(listener: OnGroupClickListener?): Builder {
            mDecoration.setOnGroupClickListener(listener)
            return this
        }

        /**
         * @param listener
         * @return this
         */
        fun setOnAvatarClickListener(listener: OnAvatarClickListener?): Builder {
            mDecoration.setOnAvatarClickListener(listener)
            return this
        }

        /**
         * @param recyclerView      recyclerView
         * @param gridLayoutManager gridLayoutManager
         * @return this
         */
        fun resetSpan(recyclerView: RecyclerView?, gridLayoutManager: GridLayoutManager?): Builder {
            mDecoration.resetSpan(recyclerView, gridLayoutManager)
            return this
        }

        /**
         * @param b
         * @return
         */
        fun setCacheEnable(b: Boolean): Builder {
            mDecoration.setCacheEnable(b)
            return this
        }

        /**
         * @param headerCount
         * @return
         */
        fun setHeaderCount(headerCount: Int): Builder {
            if (headerCount >= 0) {
                mDecoration.mHeaderCount = headerCount
            }
            return this
        }

        /**
         * @param sticky
         * @return
         */
        fun setSticky(sticky: Boolean): Builder {
            mDecoration.mSticky = sticky
            return this
        }

        fun build(): ChatAvatarStickyDecoration {
            return mDecoration
        }

        companion object {
            fun init(listener: ChatAvatarDecorationListener): Builder {
                return Builder(listener)
            }
        }

        init {
            mDecoration = ChatAvatarStickyDecoration(listener)
        }
    }


}