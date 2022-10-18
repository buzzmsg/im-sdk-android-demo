package com.tmmtmm.filepicker.filetype

import com.android.filepicker.filetype.FileType
import com.tmmtmm.filepicker.R

class DataBaseFileType : FileType {

    override val fileType: String
        get() = "DataBase"
    override val fileIconResId: Int
        get() = R.drawable.ic_unknown_file_picker

    override fun verify(fileName: String): Boolean {

        val isHasSuffix = fileName.contains(".")
        if (!isHasSuffix){
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".")  + 1)
        return when (suffix){
            "accdb", "db", "dbf", "mdb", "pdb", "sql" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}