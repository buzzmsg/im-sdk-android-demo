package com.im.sdk.ui.view.recyclerview.decoration;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.im.sdk.ui.view.recyclerview.click.ClickInfo;
import com.im.sdk.ui.view.recyclerview.listener.OnGroupClickListener;
import com.im.sdk.ui.view.recyclerview.listener.PowerGroupListener;
import com.im.sdk.ui.view.recyclerview.util.CacheUtil;
import com.im.sdk.ui.view.recyclerview.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @description
 * @time 2022/1/17 5:16 下午
 */
public class PowerfulStickyDecoration extends BaseDecoration {

    private Paint mGroutPaint;

    /**
     *
     */
    private CacheUtil<Bitmap> mBitmapCache = new CacheUtil<>();

    /**
     *
     */
    private CacheUtil<View> mHeadViewCache = new CacheUtil<>();

    private PowerGroupListener mGroupListener;

    private PowerfulStickyDecoration(PowerGroupListener groupListener) {
        super();
        this.mGroupListener = groupListener;
        mGroutPaint = new Paint();
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int itemCount = state.getItemCount();
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(childView);
            int realPosition = getRealPosition(position);
            if (isFirstInGroup(realPosition) || isFirstInRecyclerView(realPosition, i)) {
                int viewBottom = childView.getBottom();
                String curGroupName = getGroupName(realPosition);
                //top

                int bottom = !mSticky ? childView.getTop() : Math.max(mGroupHeight, childView.getTop());

                if (mSticky && position + 1 < itemCount) {
                    if (isLastLineInGroup(parent, realPosition) && viewBottom < bottom) {
                        bottom = viewBottom;
                    }
                }
                drawDecoration(c, realPosition, left, right, bottom);
            } else {

                drawDivide(c, parent, childView, realPosition, left, right);
            }
        }
    }

    /**
     *
     * @param c        Canvas
     * @param realPosition realPosition
     * @param left     left
     * @param right    right
     * @param bottom   bottom
     */
    private void drawDecoration(Canvas c, int realPosition, int left, int right, int bottom) {
        c.drawRect(left, bottom - mGroupHeight, right, bottom, mGroutPaint);
        View groupView;
        int firstPositionInGroup = getFirstInGroupWithCash(realPosition);
        if (mHeadViewCache.get(firstPositionInGroup) == null) {
            groupView = getGroupView(firstPositionInGroup);
            if (groupView == null) {
                return;
            }
            measureAndLayoutView(groupView, left, right);
            mHeadViewCache.put(firstPositionInGroup, groupView);
        } else {
            groupView = mHeadViewCache.get(firstPositionInGroup);
        }
        Bitmap bitmap;
        if (mBitmapCache.get(firstPositionInGroup) != null) {
            bitmap = mBitmapCache.get(firstPositionInGroup);
        } else {
            bitmap = Bitmap.createBitmap(groupView.getDrawingCache());
            mBitmapCache.put(firstPositionInGroup, bitmap);
        }
        c.drawBitmap(bitmap, left, bottom - mGroupHeight, null);
        if (mOnGroupClickListener != null) {
            setClickInfo(groupView, left, bottom, realPosition);
        }
    }

    /**
     *
     * @param groupView groupView
     * @param left      left
     * @param right     right
     */
    private void measureAndLayoutView(View groupView, int left, int right) {
        groupView.setDrawingCacheEnabled(true);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(right, mGroupHeight);
        groupView.setLayoutParams(layoutParams);
        groupView.measure(
                View.MeasureSpec.makeMeasureSpec(right, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(mGroupHeight, View.MeasureSpec.EXACTLY));
        groupView.layout(left, 0 - mGroupHeight, right, 0);
    }

    /**
     *
     * @param groupView
     * @param parentBottom
     * @param realPosition
     */
    private void setClickInfo(View groupView, int parentLeft, int parentBottom, int realPosition) {
        int parentTop = parentBottom - mGroupHeight;
        List<ClickInfo.DetailInfo> list = new ArrayList<>();
        List<View> viewList = ViewUtil.getChildViewWithId(groupView);
        for (View view : viewList) {
            int top = view.getTop() + parentTop;
            int bottom = view.getBottom() + parentTop;
            int left = view.getLeft() + parentLeft;
            int right = view.getRight() + parentLeft;
            list.add(new ClickInfo.DetailInfo(view.getId(), left, right, top, bottom));
        }
        ClickInfo clickInfo = new ClickInfo(parentBottom, list);
        clickInfo.mGroupId = groupView.getId();
        stickyHeaderPosArray.put(realPosition, clickInfo);
    }

    /**
     * @param realPosition realPosition
     * @return
     */
    @Override
    String getGroupName(int realPosition) {
        if (mGroupListener != null) {
            return mGroupListener.getGroupName(realPosition);
        } else {
            return null;
        }
    }

    /**
     *
     * @param realPosition realPosition
     * @return
     */
    private View getGroupView(int realPosition) {
        if (mGroupListener != null) {
            return mGroupListener.getGroupView(realPosition);
        } else {
            return null;
        }
    }

    /**
     *
     * @param b b
     */
    public void setCacheEnable(boolean b) {
        mHeadViewCache.isCacheable(b);
        mBitmapCache.isCacheable(b);
    }

    /**
     *
     */
    public void clearCache() {
        mHeadViewCache.clean();
        mBitmapCache.clean();
    }

    /**
     *
     * @param recyclerView recyclerView
     * @param realPosition     realPosition
     */
    public void notifyRedraw(RecyclerView recyclerView, View viewGroup, int realPosition) {
        viewGroup.setDrawingCacheEnabled(false);
        int firstPositionInGroup = getFirstInGroupWithCash(realPosition);
        mBitmapCache.remove(firstPositionInGroup);
        mHeadViewCache.remove(firstPositionInGroup);
        int left = recyclerView.getPaddingLeft();
        int right = recyclerView.getWidth() - recyclerView.getPaddingRight();
        measureAndLayoutView(viewGroup, left, right);
        mHeadViewCache.put(firstPositionInGroup, viewGroup);
        recyclerView.invalidate();
    }

    public static class Builder {
        PowerfulStickyDecoration mDecoration;

        private Builder(PowerGroupListener listener) {
            mDecoration = new PowerfulStickyDecoration(listener);
        }

        public static Builder init(PowerGroupListener listener) {
            return new Builder(listener);
        }

        /**
         *
         * @param groutHeight
         * @return this
         */
        public Builder setGroupHeight(int groutHeight) {
            mDecoration.mGroupHeight = groutHeight;
            return this;
        }


        /**
         *
         * @param background
         */
        public Builder setGroupBackground(@ColorInt int background) {
            mDecoration.mGroupBackground = background;
            mDecoration.mGroutPaint.setColor(mDecoration.mGroupBackground);
            return this;
        }

        /**
         *
         * @param height
         * @return this
         */
        public Builder setDivideHeight(int height) {
            mDecoration.mDivideHeight = height;
            return this;
        }

        /**
         *
         * @param color color
         * @return this
         */
        public Builder setDivideColor(@ColorInt int color) {
            mDecoration.mDivideColor = color;
            mDecoration.mDividePaint.setColor(mDecoration.mDivideColor);
            return this;
        }

        /**
         *
         * @param listener
         * @return this
         */
        public Builder setOnClickListener(OnGroupClickListener listener) {
            mDecoration.setOnGroupClickListener(listener);
            return this;
        }

        /**
         *
         * @param recyclerView      recyclerView
         * @param gridLayoutManager gridLayoutManager
         * @return this
         */
        public Builder resetSpan(RecyclerView recyclerView, GridLayoutManager gridLayoutManager) {
            mDecoration.resetSpan(recyclerView, gridLayoutManager);
            return this;
        }

        /**
         *
         * @param b
         * @return
         */
        public Builder setCacheEnable(boolean b) {
            mDecoration.setCacheEnable(b);
            return this;
        }

        /**
         *
         * @param headerCount
         * @return
         */
        public Builder setHeaderCount(int headerCount) {
            if (headerCount >= 0) {
                mDecoration.mHeaderCount = headerCount;
            }
            return this;
        }

        /**
         * @param sticky
         * @return
         */
        public Builder setSticky(boolean sticky) {
            mDecoration.mSticky = sticky;
            return this;
        }

        public PowerfulStickyDecoration build() {
            return mDecoration;
        }
    }

}
