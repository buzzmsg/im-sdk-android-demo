package com.tmmtmm.filepicker.filetype

import com.android.filepicker.filetype.FileType
import com.tmmtmm.filepicker.R

class RasterImageFileType : FileType {

    override val fileType: String
        get() = "Image"
    override val fileIconResId: Int
        get() = R.drawable.ic_image_file_picker

    override fun verify(fileName: String): Boolean {
        val isHasSuffix = fileName.contains(".")
        if (!isHasSuffix){
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".") + 1)
        return when (suffix){
            "jpeg", "jpg", "bmp", "dds", "gif", "png", "psd", "pspimage", "tga", "thm", "tif", "tiff", "yuv"-> {
                true
            }
            else -> {
                false
            }
        }
    }
}