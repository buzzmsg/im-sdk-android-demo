package com.android.filepicker.bean

import com.tmmtmm.filepicker.bean.BeanSubscriber

/**
 *
 * @author rosu
 * @date 2018/11/21
 */
interface FileBean {
    var fileName: String
    var filePath: String
    var createTime: Long
    var beanSubscriber: BeanSubscriber
}