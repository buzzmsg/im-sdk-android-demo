package com.tmmtmm.filepicker.filetype

import com.android.filepicker.filetype.FileType
import com.tmmtmm.filepicker.R

class WebFileType : FileType {
    override val fileType: String
        get() = "Web"
    override val fileIconResId: Int
        get() = R.drawable.ic_html_file_picker

    override fun verify(fileName: String): Boolean {

        val isHasSuffix = fileName.contains(".")
        if (!isHasSuffix){
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".")  + 1)
        return when (suffix){
            "asp", "aspx", "cer", "cfm", "csr", "css",
            "dcr", "html", "htm", "js", "jsp" , "php",
            "rss", "xhtml" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}