package com.android.filepicker.config

import android.view.View
import com.tmmtmm.filepicker.adapter.FileListAdapter

/**
 *
 * @author rosu
 * @date 2018/11/26
 */
interface FileItemOnClickListener {

    fun onItemClick(
        itemAdapter: FileListAdapter,
        itemView: View,
        position: Int)

    fun onItemChildClick(
        itemAdapter: FileListAdapter,
        itemView: View,
        position: Int)

    fun onItemLongClick(
        itemAdapter: FileListAdapter,
        itemView: View,
        position: Int)
}