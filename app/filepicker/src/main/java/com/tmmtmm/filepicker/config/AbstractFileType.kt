package com.android.filepicker.config

import com.android.filepicker.bean.FileItemBeanImpl

abstract class AbstractFileType {

    abstract fun fillFileType(itemBeanImpl: FileItemBeanImpl): FileItemBeanImpl
}