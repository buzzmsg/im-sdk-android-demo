package com.android.filepicker.config

import com.android.filepicker.bean.FileItemBeanImpl

abstract class AbstractFileFilter {

    abstract fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl>
}