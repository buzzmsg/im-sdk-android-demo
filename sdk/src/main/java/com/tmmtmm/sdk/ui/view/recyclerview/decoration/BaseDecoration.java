package com.tmmtmm.sdk.ui.view.recyclerview.decoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tmmtmm.sdk.ui.view.recyclerview.click.ClickInfo;
import com.tmmtmm.sdk.ui.view.recyclerview.listener.OnAvatarClickListener;
import com.tmmtmm.sdk.ui.view.recyclerview.listener.OnGroupClickListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description
 * @time 2022/1/17 4:50 下午
 */
public abstract class BaseDecoration extends RecyclerView.ItemDecoration {

    /**
     *
     */
    @ColorInt
    int mGroupBackground = Color.parseColor("#48BDFF");
    /**
     *
     */
    int mGroupHeight = 120;
    /**
     *
     */
    @ColorInt
    int mDivideColor = Color.parseColor("#CCCCCC");
    /**
     *
     */
    int mDivideHeight = 0;
    int mUserDivideHeight = 0;
    int mNameHeight = 0;
    int mAvatarSize = 0;
    int mTransMessageHeight = 0;

    /**
     *
     */
    int mHeaderCount;

    Paint mDividePaint;
    /**
     *
     */
    private SparseIntArray firstInGroupCash = new SparseIntArray(100);

    protected OnGroupClickListener mOnGroupClickListener;

    protected OnAvatarClickListener mOnAvatarClickListener;

    protected boolean mSticky = true;

    public BaseDecoration() {
        mDividePaint = new Paint();
        mDividePaint.setColor(mDivideColor);
    }

    /**
     * @param listener
     */
    protected void setOnGroupClickListener(OnGroupClickListener listener) {
        this.mOnGroupClickListener = listener;
    }

    protected void setOnAvatarClickListener(OnAvatarClickListener mOnAvatarClickListener) {
        this.mOnAvatarClickListener = mOnAvatarClickListener;
    }

    /**
     * @param realPosition realPosition
     * @return group
     */
    abstract String getGroupName(int realPosition);

    /**
     * @param position
     * @return
     */
    protected int getRealPosition(int position) {
        return position - mHeaderCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int realPosition = getRealPosition(position);
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            int spanCount = ((GridLayoutManager) manager).getSpanCount();
            if (!isHeader(realPosition)) {
                if (isFirstLineInGroup(realPosition, spanCount)) {
                    outRect.top = mGroupHeight;
                } else {
                    outRect.top = mDivideHeight;
                }
            }
        } else {
            if (!isHeader(realPosition)) {
                if (isFirstInGroup(realPosition)) {
                    outRect.top = mGroupHeight;
                } else {
                    outRect.top = mDivideHeight;
                }
            }
        }
    }

    /**
     *
     */
    protected boolean isFirstInGroup(int realPosition) {
        if (realPosition < 0) {
            //小于header数量，不是第一个
            return false;
        } else if (realPosition == 0) {
            //等于header数量，为第一个
            return true;
        }
        String preGroupId;
        preGroupId = getGroupName(realPosition - 1);
        String curGroupId = getGroupName(realPosition);
        if (curGroupId == null) {
            return false;
        }
        return !TextUtils.equals(preGroupId, curGroupId);
    }

    /**
     *
     * @param realPosition 总的position
     * @param index    RecyclerView中的Index
     * @return
     */
    protected boolean isFirstInRecyclerView(int realPosition, int index) {
        return realPosition >= 0 && index == 0;
    }

    /**
     *
     */
    protected boolean isHeader(int realPosition) {
        return realPosition < 0;
    }


    /**
     *
     */
    protected boolean isFirstLineInGroup(int realPosition, int spanCount) {
        if (realPosition < 0) {
            //小于header数量，不是第一个
            return false;
        } else if (realPosition == 0) {
            return true;
        } else {
            int posFirstInGroup = getFirstInGroupWithCash(realPosition);
            if (realPosition - posFirstInGroup < spanCount) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     *
     * @param recyclerView      recyclerView
     * @param gridLayoutManager gridLayoutManager
     */
    public void resetSpan(RecyclerView recyclerView, GridLayoutManager gridLayoutManager) {
        if (recyclerView == null) {
            throw new NullPointerException("recyclerView not allow null");
        }
        if (gridLayoutManager == null) {
            throw new NullPointerException("gridLayoutManager not allow null");
        }
        final int spanCount = gridLayoutManager.getSpanCount();

        GridLayoutManager.SpanSizeLookup lookup = new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int span;
                int realPosition = getRealPosition(position);
                if (realPosition < 0) {

                    span = spanCount;
                } else {
                    String curGroupId = getGroupName(realPosition);
                    String nextGroupId;
                    try {
                        //out
                        nextGroupId = getGroupName(realPosition + 1);
                    } catch (Exception e) {
                        nextGroupId = curGroupId;
                    }
                    if (!TextUtils.equals(curGroupId, nextGroupId)) {
                        //last
                        int posFirstInGroup = getFirstInGroupWithCash(realPosition);
                        span = spanCount - (realPosition - posFirstInGroup) % spanCount;
                    } else {
                        span = 1;
                    }
                }
                return span;
            }
        };
        gridLayoutManager.setSpanSizeLookup(lookup);
    }

    /**
     * down
     */
    private boolean mDownInHeader;

    /**
     * RecyclerView onInterceptEvent down
     *
     * @param event
     */
    public void onEventDown(MotionEvent event) {
        if (event == null) {
            mDownInHeader = false;
            return;
        }
        mDownInHeader = event.getY() > 0 && event.getY() < mGroupHeight;
    }

    /**
     * RecyclerView onInterceptEvent up
     *
     * @param event
     * @return
     */
    public boolean onEventUp(MotionEvent event) {
        if (mDownInHeader) {
            float y = event.getY();
            boolean isInHeader = y > 0 && y < mGroupHeight;
            if (isInHeader) {
                return onTouchEvent(event);
            }
        }
        return false;
    }

    /**
     * first group
     *
     * @param realPosition realPosition
     */
    protected int getFirstInGroupWithCash(int realPosition) {
        return getFirstInGroup(realPosition);
    }

    /**
     * first group
     *
     * @param realPosition realPosition
     */
    private int getFirstInGroup(int realPosition) {
        if (realPosition <= 0) {
            return 0;
        } else {
            if (isFirstInGroup(realPosition)) {
                return realPosition;
            } else {
                return getFirstInGroup(realPosition - 1);
            }
        }
    }


    /**
     * last group
     *
     * @param recyclerView recyclerView
     * @param realPosition     realPosition
     * @return
     */
    protected boolean isLastLineInGroup(RecyclerView recyclerView, int realPosition) {
        if (realPosition < 0) {
            return true;
        } else {
            String curGroupName = getGroupName(realPosition);
            String nextGroupName;
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            //
            int findCount = 1;
            if (manager instanceof GridLayoutManager) {
                int spanCount = ((GridLayoutManager) manager).getSpanCount();
                int firstPositionInGroup = getFirstInGroupWithCash(realPosition);
                findCount = spanCount - (realPosition - firstPositionInGroup) % spanCount;
            }
            try {
                nextGroupName = getGroupName(realPosition + findCount);
            } catch (Exception e) {
                nextGroupName = curGroupName;
            }
            if (nextGroupName == null) {
                return true;
            }
            return !TextUtils.equals(curGroupName, nextGroupName);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        //click
        if (gestureDetector == null) {
            gestureDetector = new GestureDetector(parent.getContext(), gestureListener);
            parent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }
        stickyHeaderPosArray.clear();
    }

    /**
     * click
     *
     * @param realPosition realPosition
     */
    private void onGroupClick(int realPosition, int viewId) {
        if (mOnGroupClickListener != null) {
            mOnGroupClickListener.onClick(realPosition, viewId);
        }
    }

    /**
     * click
     *
     * @param realPosition realPosition
     */
    private void onAvatarClick(int realPosition, int viewId, String tag) {
        if (mOnAvatarClickListener != null && !TextUtils.isEmpty(tag)) {
            mOnAvatarClickListener.onClick(realPosition, viewId, tag);
        }
    }

    /**
     * header position
     */
    protected HashMap<Integer, ClickInfo> stickyHeaderPosArray = new HashMap<>();
    private GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return onTouchEvent(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    /**
     * touch event
     *
     * @param e
     * @return
     */
    private boolean onTouchEvent(MotionEvent e) {
        for (Map.Entry<Integer, ClickInfo> entry : stickyHeaderPosArray.entrySet()) {

            ClickInfo value = stickyHeaderPosArray.get(entry.getKey());
            float y = e.getY();
            float x = e.getX();
            if (!TextUtils.isEmpty(value.mTag)) {
                if (value.mDetailInfoList != null && value.mDetailInfoList.size() > 0) {
                    List<ClickInfo.DetailInfo> list = value.mDetailInfoList;
                    for (ClickInfo.DetailInfo detailInfo : list) {
                        if (detailInfo.top <= y && y <= detailInfo.bottom
                                && detailInfo.left <= x && detailInfo.right >= x) {
                            onAvatarClick(entry.getKey(), value.mGroupId, value.mTag);
                            return true;
                        }
                    }
                }
            }
            if (value.mBottom - mGroupHeight <= y && y <= value.mBottom) {
                //
                if (value.mDetailInfoList == null || value.mDetailInfoList.size() == 0) {
                    //
                    onGroupClick(entry.getKey(), value.mGroupId);
                } else {
                    List<ClickInfo.DetailInfo> list = value.mDetailInfoList;
                    boolean isChildViewClicked = false;
                    for (ClickInfo.DetailInfo detailInfo : list) {
                        if (detailInfo.top <= y && y <= detailInfo.bottom
                                && detailInfo.left <= x && detailInfo.right >= x) {
                            //
                            onGroupClick(entry.getKey(), detailInfo.id);
                            isChildViewClicked = true;
                            break;
                        }
                    }
                    if (!isChildViewClicked) {
                        //
                        onGroupClick(entry.getKey(), value.mGroupId);
                    }

                }
                return true;
            }


        }
        return false;
    }

    /**
     * draw divide
     *
     * @param c
     * @param parent
     * @param childView
     * @param realPosition
     * @param left
     * @param right
     */
    protected void drawDivide(Canvas c, RecyclerView parent, View childView, int realPosition, int left, int right) {
        if (mDivideHeight != 0 && !isHeader(realPosition)) {
            RecyclerView.LayoutManager manager = parent.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                int spanCount = ((GridLayoutManager) manager).getSpanCount();
                if (!isFirstLineInGroup(realPosition, spanCount)) {
                    float bottom = childView.getTop() + parent.getPaddingTop();
                    //
                    if (bottom >= mGroupHeight) {
                        c.drawRect(left, bottom - mDivideHeight, right, bottom, mDividePaint);
                    }
                }
            } else {
                float bottom = childView.getTop();
                //
                if (bottom >= mGroupHeight) {
                    c.drawRect(left, bottom - mDivideHeight, right, bottom, mDividePaint);
                }
            }
        }
    }

    protected void log(String content) {
        if (false) {
            Log.i("StickDecoration", content);
        }
    }

}
