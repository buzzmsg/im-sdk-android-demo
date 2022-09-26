package com.tmmtmm.sdk.ui.ext

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun <T : ViewBinding> ViewBinding.bindView(context: Activity): T {
    context.setContentView(root)
    return this as T
}

fun Fragment.bindView(layoutId: Int, container: ViewGroup?, attachToRoot: Boolean = false): View? {
    return layoutInflater.inflate(layoutId, container, attachToRoot)
}

inline fun <T> ViewModel.runIO(crossinline block: suspend CoroutineScope.() -> T) {
    viewModelScope.launch {
        withContext(Dispatchers.IO) { block() }
    }
}
