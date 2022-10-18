package com.tmmtmm.filepicker.filetype

import com.android.filepicker.filetype.FileType
import com.tmmtmm.filepicker.R

class FontFileType : FileType {

    override val fileType: String
        get() = "Font"
    override val fileIconResId: Int
        get() = R.drawable.ic_unknown_file_picker

    override fun verify(fileName: String): Boolean {
        val isHasSuffix = fileName.contains(".")
        if (!isHasSuffix){
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".")  + 1)
        return when (suffix){
            "fnt", "fon", "otf", "ttf" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}