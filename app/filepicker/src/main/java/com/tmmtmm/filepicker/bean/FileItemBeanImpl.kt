package com.android.filepicker.bean

import com.android.filepicker.filetype.FileType
import com.tmmtmm.filepicker.bean.BeanSubscriber

class FileItemBeanImpl(
    override var fileName: String,
    override var filePath: String,
    override var createTime: Long,
    private var isChecked: Boolean,
    var fileType: FileType?,
    var isDir: Boolean,
    var isHide: Boolean,
    override var beanSubscriber: BeanSubscriber
) : FileBean {

    fun isChecked(): Boolean {
        return isChecked
    }

    fun setCheck(check: Boolean) {
        isChecked = check
        beanSubscriber.updateItemUI(check)
    }
}