package com.tmmtmm.sdk.ui.view.message

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * @description
 * @version
 */
class ChatRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var mLastDownTime = 0L
    private var _touch: ((Boolean) -> Unit)? = null

    init {
        this.viewTreeObserver.addOnGlobalLayoutListener {

        }
    }

    var downX = 0f
    var downY = 0f


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {




        val dispatchTouchEvent = super.dispatchTouchEvent(ev)

        when(ev.action){
            MotionEvent.ACTION_DOWN -> {
                downX = ev.rawX
                downY = ev.rawY
            }

            MotionEvent.ACTION_UP -> {
//                val x = abs(downX - ev.rawX)
                val y = abs(downY - ev.rawY)
                if (y < 10){
//                    _touch?.invoke(dispatchTouchEvent)
                }
            }
        }
//        if (ev.action == MotionEvent.ACTION_UP) {
//            _touch?.invoke(dispatchTouchEvent)
//        }
//        Log.d("ConversationRecyclerView", "dispatchTouchEvent() called with: ev = $ev dispatchTouchEvent = $dispatchTouchEvent")
        return dispatchTouchEvent
    }

    fun onTouchEventConversationRecyclerView(touch: (Boolean) -> Unit) {
        _touch = touch
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        _touch?.invoke(true)
        return super.onTouchEvent(e)
    }




}