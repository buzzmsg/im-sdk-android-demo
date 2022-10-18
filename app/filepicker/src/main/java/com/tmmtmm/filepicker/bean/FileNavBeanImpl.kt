package com.android.filepicker.bean

import com.tmmtmm.filepicker.bean.BeanSubscriber

/**
 *
 * @author rosu
 * @date 2018/11/21
 */
class FileNavBeanImpl(val dirName: String, val dirPath: String, var time: Long = 0) : FileBean {
    override var fileName: String
        get() = dirName
        set(value) {}
    override var createTime: Long
        get() = time
        set(value) {}
    override var filePath: String
        get() = dirPath
        set(value) {}

    override var beanSubscriber: BeanSubscriber
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
}