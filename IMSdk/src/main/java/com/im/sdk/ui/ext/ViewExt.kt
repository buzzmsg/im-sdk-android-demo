package com.im.sdk.ui.ext

import android.graphics.Outline
import android.os.SystemClock
import android.view.View
import android.view.ViewOutlineProvider

/**
 * @description
 * @version
 */
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.isShow() = visibility == View.VISIBLE


fun View.setOutlineProvider(corner : Float) {
    val outLine = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setRoundRect(
                0,
                0,
                view?.width ?: 0,
                view?.height ?: 0,
                corner
            )
        }
    }

    outlineProvider = outLine
    clipToOutline = true
}

inline fun View.click(debounceTime: Long = 400, crossinline block: (View) -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {

            if (debounceTime == 0.toLong()) {
                block(v)
                return
            }

            if (SystemClock.elapsedRealtime() - lastClickTime > debounceTime) {
                block(v)
            }
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}
