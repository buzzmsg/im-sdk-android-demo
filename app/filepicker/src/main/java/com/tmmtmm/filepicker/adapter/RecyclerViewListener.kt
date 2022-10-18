package com.android.filepicker.adapter

import android.app.Activity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import com.tmmtmm.filepicker.R
import com.tmmtmm.filepicker.utils.ScreenUtils

class RecyclerViewListener(val activity: Activity, val recyclerView: RecyclerView, val itemClickListener: OnItemClickListener) :
    RecyclerView.OnItemTouchListener{

    interface OnItemClickListener {

        fun onItemClick(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, view: View, position: Int)

        fun onItemLongClick(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, view: View, position: Int)

        fun onItemChildClick(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, view: View, position: Int)
    }


    private var gestureDetectorCompat: GestureDetectorCompat =
        GestureDetectorCompat(recyclerView.context, ItemTouchHelperGestureListener())

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        gestureDetectorCompat.onTouchEvent(e)
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return gestureDetectorCompat.onTouchEvent(e)
    }

    override fun onRequestDisallowInterceptTouchEvent(p0: Boolean) {}

    private val screenWidth = ScreenUtils.getScreenWidthInPixel(activity)
    private val iconRight = screenWidth * 0.1370
    private val checkBoxLeft = screenWidth * (1 - 0.1370)


    inner class ItemTouchHelperGestureListener:GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            val childView = recyclerView.findChildViewUnder(e!!.x, e.y)
            childView?:return false
            when(childView.id){
                R.id.item_list_file_picker -> {
                    if (e.x <= iconRight || e.x >= checkBoxLeft){
                        itemClickListener.onItemChildClick(recyclerView.adapter!!, childView, recyclerView.getChildLayoutPosition(childView))
                        return true
                    }
                    itemClickListener.onItemClick(recyclerView.adapter!!, childView, recyclerView.getChildLayoutPosition(childView))
                }
                R.id.item_nav_file_picker -> {
                    itemClickListener.onItemClick(recyclerView.adapter!!, childView, recyclerView.getChildLayoutPosition(childView))
                }
            }
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            val childView = recyclerView.findChildViewUnder(e!!.x, e.y)
            childView?:return
            when(childView.id){
                R.id.item_list_file_picker -> {
                    itemClickListener.onItemLongClick(recyclerView.adapter!!, childView, recyclerView.getChildLayoutPosition(childView))
                }
            }
        }
    }
}