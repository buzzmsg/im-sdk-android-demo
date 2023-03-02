package com.tmmtmm.demo.ui.ext


import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.tmmtmm.demo.R

fun Toolbar.initToolbar(
    title: String,
    menu: Int = 0,
    icon: Int = R.drawable.ic_back_black,
    isTitleCenter: Boolean = true,
    backgroundColor: Int = 0,
    theme: Int = 0,
    iconColor: Int = R.color.black
): Toolbar {

    if (theme != 0) {
        context.setTheme(theme)
    }

    val titleView = findViewById<TextView>(R.id.titleView)

    if (icon != 0) {
        setNavigationIcon(icon)
        if (iconColor != 0) {
            navigationIcon?.setTint(ContextCompat.getColor(context, iconColor))
        }
        if (icon == R.drawable.ic_back_black || icon == R.drawable.ic_back_white) {
            setPadding(0, 0, 0, 0)
        }
        if (icon == R.drawable.ic_back_white) {
            if (iconColor != 0) {
                titleView?.setTextColor(ContextCompat.getColor(context, iconColor))
            } else {
                titleView?.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }

    if (menu != 0) {
        getMenu().clear()
        inflateMenu(menu)
    }

    if (isTitleCenter) {
        setTitle("")
        titleView?.text = title
        titleView?.visibility = View.VISIBLE
    } else {
        setTitle(title)
        titleView?.visibility = View.GONE
    }

    if (backgroundColor != 0) {
        background = ContextCompat.getDrawable(context, backgroundColor)
    }
    return this
}