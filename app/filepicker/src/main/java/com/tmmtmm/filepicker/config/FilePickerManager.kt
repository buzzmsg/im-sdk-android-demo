package com.android.filepicker.config

import android.app.Activity
import androidx.fragment.app.Fragment
import com.tmmtmm.filepicker.config.FilePickerConfig
import java.lang.ref.WeakReference

/**
 *
 * @author rosu
 * @date 2018/11/22
 */
object FilePickerManager {
    const val REQUEST_CODE = 10401

    internal var contextRef: WeakReference<Activity>? = null
    internal var fragmentRef: WeakReference<Fragment>? = null
    internal lateinit var config: FilePickerConfig

    fun from(activity: Activity): FilePickerConfig {
        reset()
        this.contextRef = WeakReference(activity)
        config = FilePickerConfig(this)
        return config
    }

    private fun reset() {
        contextRef?.clear()
        fragmentRef?.clear()
    }


    fun from(fragment: Fragment): FilePickerConfig {
        reset()
        this.fragmentRef = WeakReference(fragment)
        this.contextRef = WeakReference(fragment.requireActivity())
        config = FilePickerConfig(this)
        return config
    }

    private var dataList: List<String> = ArrayList()


    fun saveData(list: List<String>) {
        dataList = list
    }


    fun obtainData(): List<String> {
        return dataList
    }
}