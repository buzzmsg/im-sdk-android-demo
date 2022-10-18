package com.tmmtmm.filepicker.filetype

import com.android.filepicker.filetype.FileType
import com.tmmtmm.filepicker.R

class CompressedFileType : FileType {

    override val fileType: String
        get() = "Compressed"
    override val fileIconResId: Int
        get() = R.drawable.ic_compressed_file_picker

    override fun verify(fileName: String): Boolean {
        val isHasSuffix = fileName.contains(".")
        if (!isHasSuffix){
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".")  + 1)
        return when (suffix){
            "zip", "rar", "arj", "tar.gz", "tgz", "gz", "iso", "tbz", "tbz2", "7z" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}