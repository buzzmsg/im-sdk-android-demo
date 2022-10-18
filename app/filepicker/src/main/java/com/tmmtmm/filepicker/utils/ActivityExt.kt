package com.tmmtmm.filepicker.utils

import android.app.Activity
import androidx.viewbinding.ViewBinding


fun <T : ViewBinding> ViewBinding.bindView(context: Activity): T {
    context.setContentView(root)
    return this as T
}

