package com.android.filepicker.config

import android.view.View
import android.widget.CheckBox
import com.tmmtmm.filepicker.R
import com.tmmtmm.filepicker.adapter.FileListAdapter
import java.io.File

internal class FileItemOnClickListenerImpl : FileItemOnClickListener {

    override fun onItemClick(itemAdapter: FileListAdapter, itemView: View, position: Int) {

    }

    override fun onItemChildClick(itemAdapter: FileListAdapter, itemView: View, position: Int) {

    }

    override fun onItemLongClick(itemAdapter: FileListAdapter, itemView: View, position: Int) {
        if (itemView.id != R.id.item_list_file_picker) return
        val item = (itemAdapter as FileListAdapter).getItem(position)
        item ?: return
        val file = File(item.filePath)
        val isSkipDir = FilePickerManager.config.isSkipDir
        if (file.exists() && file.isDirectory && isSkipDir) return
        val cb = itemView.findViewById<CheckBox>(R.id.cb_list_file_picker)
        val isChecked = cb.isChecked
        cb.visibility = View.VISIBLE
        if (isChecked) {
            cb.isChecked = false
            item.setCheck(cb.isChecked)
        } else {
            cb.isChecked = true
            item.setCheck(cb.isChecked)
        }
    }
}