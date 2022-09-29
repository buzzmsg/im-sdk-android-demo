package com.tmmtmm.sdk.ui.ext

import android.view.ViewStub

class Stub<T>(private var viewStub: ViewStub?) {

    var isInflate = false

    init {
        viewStub?.setOnInflateListener { stub, inflated ->
            isInflate = true
        }
    }

    private var view: T? = null
    fun get(): T? {
        if (view == null) {
            view = viewStub?.inflate() as T
            viewStub = null
        }
        return view
    }


    fun resolved(): Boolean {
        return view != null
    }

}