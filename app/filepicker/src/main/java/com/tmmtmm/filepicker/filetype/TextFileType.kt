package com.tmmtmm.filepicker.filetype

import com.android.filepicker.filetype.FileType
import com.tmmtmm.filepicker.R

class TextFileType : FileType {

    override val fileType: String
        get() = "Text"
    override val fileIconResId: Int
        get() = R.drawable.ic_unknown_file_picker

    override fun verify(fileName: String): Boolean {
        val isHasSuffix = fileName.contains(".")
        if (!isHasSuffix){
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".")  + 1)
        return when (suffix){
            "doc", "docx", "log", "txt", "msg", "odt", "pages", "rtf", "tex", "wpd", "wps" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}