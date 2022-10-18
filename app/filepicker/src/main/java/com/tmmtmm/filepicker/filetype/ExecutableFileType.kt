package com.tmmtmm.filepicker.filetype

import com.android.filepicker.filetype.FileType
import com.tmmtmm.filepicker.R

class ExecutableFileType : FileType {
    override val fileType: String
        get() = "Executable"
    override val fileIconResId: Int
        get() = R.drawable.ic_exec_file_picker

    override fun verify(fileName: String): Boolean {
        val isHasSuffix = fileName.contains(".")
        if (!isHasSuffix){
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".")  + 1)
        return when (suffix){
            "apk", "app", "bat", "cgi", "com", "exe", "gadget", "jar", "wsf" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}