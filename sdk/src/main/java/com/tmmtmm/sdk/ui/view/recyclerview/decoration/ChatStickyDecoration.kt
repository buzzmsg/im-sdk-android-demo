package com.tmmtmm.sdk.ui.view.recyclerview.decoration

import android.graphics.*
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tmmtmm.sdk.ui.ext.dpToPx
import com.tmmtmm.sdk.ui.ext.gone
import com.tmmtmm.sdk.ui.ext.hide
import com.tmmtmm.sdk.ui.ext.visible
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
class ChatStickyDecoration private constructor(groupListener: ChatAvatarDecorationListener) :
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
    private val mAvatarViewCache = CacheStringUtil<Int?>()
    private val mUserNameViewCache = CacheStringUtil<Int?>()
    private var mGroupListener: ChatAvatarDecorationListener? = null
    private var seeHistoryViewHeight: Int = 0

    init {
        mGroupListener = groupListener
        mPaint.isAntiAlias = true
        mPaint.color = Color.BLACK
        mmPaint.isAntiAlias = true
        mmPaint.color = Color.RED
        seeHistoryViewHeight = 70f.dpToPx()
    }


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val realPosition = getRealPosition(position)

        val seeHistoryView = getSeeHistoryView(realPosition - 1)
        val manager = parent.layoutManager
        if (manager is GridLayoutManager) {
            super.getItemOffsets(outRect, view, parent, state)
        } else {
            if (!isHeader(realPosition)) {
                if (isFirstInGroup(realPosition)) {
                    seeHistoryView?.let {
                        outRect.top = mGroupHeight + seeHistoryViewHeight
                    } ?: run {
                        outRect.top = mGroupHeight
                    }
                } else {
                    seeHistoryView?.let {
                        outRect.top = seeHistoryViewHeight
                    } ?: run {
                        outRect.top = mDivideHeight
                        if (isTransMessage(realPosition)) {
                            if (isLastTransMessage(realPosition)) {
                                outRect.top = mTransMessageHeight
                            }
                        } else if (lastIsUser(realPosition)) {
                            outRect.top = mUserDivideHeight
                        }
                    }
                }
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val childCount = parent.childCount
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        for (i in 0 until childCount) {
            val childView = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(childView)
            val realPosition = getRealPosition(position)

            val seeHistoryView = getSeeHistoryView(realPosition -1)

            val top = if (isFirstInGroup(realPosition) || isFirstInRecyclerView(realPosition, i)) {
                childView.top - seeHistoryViewHeight - mGroupHeight + 20f.dpToPx()
            } else {
                childView.top - seeHistoryViewHeight
            }

            seeHistoryView?.let {
                drawSeeHistoryView(c, it, left, right, top)
            }
        }
    }


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val itemCount = state.itemCount
        val childCount = parent.childCount
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        var childViewP: View? = null
        var realPositionP: Int? = null
        var leftP: Int? = null
        var rightP: Int? = null
        var bottomP: Int? = null
        for (i in 0 until childCount) {
            val childView = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(childView)
            val realPosition = getRealPosition(position)
//            val seeHistoryView = getSeeHistoryView(realPosition)
            var isGroupHeight = false
            if (!(isFirstInGroup(realPosition) || isFirstInRecyclerView(realPosition, i))) {
                drawDivide(c, parent, childView, realPosition, left, right)
//                seeHistoryView?.let {
//                    drawSeeHistoryView(c, it, left, right, childView.bottom)
//                }
            }
            if (isFirstInGroup(realPosition) || isFirstInRecyclerView(realPosition, i)) {
                val viewBottom = childView.bottom
                //top
                val paddingHeight = if (isFirstInRecyclerView(realPosition, i)) {
                    parent.paddingTop
                } else {
                    0
                }
//                val seeHistoryViewH = if (seeHistoryView == null) 0 else seeHistoryViewHeight
                var bottom = if (!mSticky) childView.top else Math.max(
                    mGroupHeight,
//                    childView.top + paddingHeight - seeHistoryViewH
                    childView.top + paddingHeight

                )
                if (mSticky && position + 1 < itemCount) {
                    if (isLastLineInGroup(parent, realPosition) && viewBottom < bottom) {
                        bottom = viewBottom
                    }
                }
                drawDecoration(c, childView, realPosition, left, right, bottom)
                isGroupHeight = true
            }
        }
        if (childViewP != null && realPositionP != null && leftP != null && rightP != null && bottomP != null) {
            drawDecoration(c, childViewP, realPositionP, leftP, rightP, bottomP)
        }
    }


    private fun getAvatarImageView(childView: View, position: Int = -1): List<Any>? {
//        if (childView !is ViewGroup) return null
//        if (position > 0) {
//            val view = childView.getChildAt(position)
//            if (view is TmmImageView) {
//                return listOf(position, view)
//            }
//        }
//        for ((index, view) in childView.children.withIndex()) {
//            if (view is TmmImageView) {
//                return listOf(index, view)
//            }
//        }
        return null
    }

    private fun getUserNameTextView(childView: View, position: Int = -1): List<Any>? {
        if (childView !is ViewGroup) return null
        if (position > 0) {
            val view = childView.getChildAt(position)
            if (view is AppCompatTextView) {
                return listOf(position, view)
            }
        }
        for ((index, view) in childView.children.withIndex()) {
            if (view is AppCompatTextView) {
                return listOf(index, view)
            }
        }
        return null
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
        if (isMeMessage(realPosition)) return
        if (!isGroup(realPosition)) return
        val userId = getUserId(realPosition)
        if (userId.isEmpty()) return

        val list =
            getAvatarImageView(childView, mAvatarViewCache[userId + realPosition] ?: -1) ?: return
        mAvatarViewCache.put(userId + realPosition, list[0] as Int)
        val avatarView = list[1] as View

        var isShowAvatar = true
        if (isTransMessage(realPosition)) isShowAvatar = false
        if (lastIsUser(realPosition)) {
            if (!isGroupHeight) {
                isShowAvatar = false
            }
            if (childView.top < mDivideHeight) {
                isShowAvatar = false
            }
        }
        if (isShowAvatar) {
            avatarView.visible()
        } else {
            avatarView.hide()
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
        if (isMeMessage(realPosition)) return
        if (!isGroup(realPosition)) return

        val userId = getUserId(realPosition)
        if (userId.isEmpty()) return

        val list =
            getUserNameTextView(childView, mAvatarViewCache[userId + realPosition] ?: -1) ?: return
        mUserNameViewCache.put(userId + realPosition, list[0] as Int)
        val userNameView = list[1] as View


        var isShow = true
        if (!isGroup(realPosition)) isShow = false
        if (isTransMessage(realPosition)) isShow = false

        if (lastIsUser(realPosition)) {
            if (!isGroupHeight) {
                isShow = false
            }
            if (childView.top < mDivideHeight) {
                isShow = false
            }
        }
        if (isShow) {
            userNameView.visible()
        } else {
            userNameView.gone()
        }
    }


    /**
     * @param c            Canvas
     * @param realPosition realPosition
     * @param left         left
     * @param right        right
     * @param bottom       bottom
     */
    private fun drawDecoration(
        c: Canvas,
        childView: View,
        realPosition: Int,
        left: Int,
        right: Int,
        bottom: Int
    ) {
        val top = bottom - mGroupHeight
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
//        mPaint.color = Color.RED
//        c.drawLine(0F,childView.top.toFloat(),right.toFloat(),childView.top.toFloat(),mPaint)
//        mPaint.color = Color.BLUE
//        c.drawLine(0F,childView.bottom.toFloat(),right.toFloat(),childView.bottom.toFloat(),mPaint)
//        mPaint.color = mGroupBackground
    }

    /**
     * @param c            Canvas
     * @param realPosition realPosition
     * @param left         left
     * @param right        right
     * @param bottom       bottom
     */
    private fun drawSeeHistoryView(
        c: Canvas,
        seeHistoryView: View,
        left: Int,
        right: Int,
        top: Int
    ) {
        val bottom = top + seeHistoryViewHeight
        c.drawRect(
            0F,
            top.toFloat(),
            right.toFloat(),
            bottom.toFloat(),
            mPaint
        )
        measureAndLayoutView(seeHistoryView, 0, right)
        val bitmap: Bitmap = Bitmap.createBitmap(seeHistoryView.drawingCache) ?: return
        val x = left.toFloat()
        c.drawBitmap(bitmap, x, top.toFloat(), null)
        bitmap.recycle()
    }


    override fun drawDivide(
        c: Canvas?,
        parent: RecyclerView?,
        childView: View?,
        realPosition: Int,
        left: Int,
        right: Int
    ) {
        if (mDivideHeight != 0 && !isHeader(realPosition)) {
            val manager = parent!!.layoutManager
            if (manager is GridLayoutManager) {
                super.drawDivide(c, parent, childView, realPosition, left, right)
            } else {
                val bottom = childView!!.top.toFloat()
                var height = mDivideHeight
                if (isTransMessage(realPosition)) {
                    if (isLastTransMessage(realPosition)) {
                        height = mTransMessageHeight
                    }
                } else if (lastIsUser(realPosition)) {
                    height = mUserDivideHeight
                }
                if (bottom >= mGroupHeight) {
                    c!!.drawRect(
                        left.toFloat(),
                        bottom - height,
                        right.toFloat(),
                        bottom,
                        mDividePaint
                    )


//                    mPaint.color = Color.RED
//                    c.drawLine(0F,childView.top.toFloat(),right.toFloat(),childView.top.toFloat(),mPaint)
//                    mPaint.color = Color.BLUE
//                    c.drawLine(0F,childView.bottom.toFloat(),right.toFloat(),childView.bottom.toFloat(),mPaint)
//                    mPaint.color = mGroupBackground
                }
            }
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
    private fun getSeeHistoryView(realPosition: Int): View? {
        return mGroupListener?.getSeeHistoryView(realPosition)
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
        var mDecoration: ChatStickyDecoration

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

        fun build(): ChatStickyDecoration {
            return mDecoration
        }

        companion object {
            fun init(listener: ChatAvatarDecorationListener): Builder {
                return Builder(listener)
            }
        }

        init {
            mDecoration = ChatStickyDecoration(listener)
        }
    }


}