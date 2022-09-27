package com.tmmtmm.demo.ui.ext

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.Utils

/**
 * @description
 * @time 2021/3/3 3:38
 * @version
 */


fun Int.getDrawable(): Drawable? {
    return if (this == 0) {
        null
    } else ContextCompat.getDrawable(Utils.getApp(), this)
}

fun Int.getColor(): Int {
    return ContextCompat.getColor(Utils.getApp(), this)
}

fun Int.getString(): String {
    return Utils.getApp().getString(this)
}

fun AppCompatTextView.setDrawableLeft(drawableLeft: Int, width: Int, height: Int) {
    val drawable = drawableLeft.getDrawable()
    drawable?.setBounds(0, 0, width, height)
    this.setCompoundDrawables(
        drawable, this.compoundDrawablesRelative[1],
        this.compoundDrawablesRelative[2], this.compoundDrawablesRelative[3]
    )
}

fun AppCompatTextView.setDrawableLeft(drawableLeft: Int) {
    val drawable = drawableLeft.getDrawable()
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(
        drawable, this.compoundDrawablesRelative[1],
        this.compoundDrawablesRelative[2], this.compoundDrawablesRelative[3]
    )
}

fun AppCompatTextView.setDrawableLeft(drawableLeft: Drawable?) {
    drawableLeft?.setBounds(0, 0, drawableLeft.minimumWidth, drawableLeft.minimumHeight)
    this.setCompoundDrawables(
        drawableLeft, this.compoundDrawablesRelative[1],
        this.compoundDrawablesRelative[2], this.compoundDrawablesRelative[3]
    )
}

fun getShapeRadiusDrawable(radius: Int, color: Int) {
    val shapeDrawable = ShapeDrawable(RectShape())
    shapeDrawable.paint.color = color
    val rect = Rect()
    rect.top = SizeUtils.dp2px(radius.toFloat())
    rect.left = SizeUtils.dp2px(radius.toFloat())
    rect.bottom = SizeUtils.dp2px(radius.toFloat())
    rect.right = SizeUtils.dp2px(radius.toFloat())
    shapeDrawable.bounds = rect
}

fun AppCompatTextView.setDrawableRight(drawableLeft: Int) {
    val drawable = drawableLeft.getDrawable()
    if (drawable != null) {
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        this.setCompoundDrawables(
            this.compoundDrawablesRelative[0],
            this.compoundDrawablesRelative[1], drawable, this.compoundDrawablesRelative[3]
        )
    } else {
        this.setCompoundDrawables(
            this.compoundDrawablesRelative[0],
            this.compoundDrawablesRelative[1], null, this.compoundDrawablesRelative[3]
        )
    }
}

fun AppCompatTextView.setDrawableTop(drawableLeft: Int) {
    val drawable = drawableLeft.getDrawable()
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(
        this.compoundDrawablesRelative[0], drawable,
        this.compoundDrawablesRelative[2], this.compoundDrawablesRelative[3]
    )
}

fun AppCompatTextView.setDrawableBottom(drawableLeft: Int) {
    val drawable = drawableLeft.getDrawable()
    drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(
        this.compoundDrawablesRelative[0],
        this.compoundDrawablesRelative[1], this.compoundDrawablesRelative[2], drawable
    )
}

