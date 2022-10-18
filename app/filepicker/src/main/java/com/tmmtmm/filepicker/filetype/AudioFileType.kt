package com.tmmtmm.filepicker.filetype

import com.android.filepicker.filetype.FileType
import com.tmmtmm.filepicker.R

class AudioFileType : FileType {
    override val fileType: String
        get() = "Audio"
    override val fileIconResId: Int
        get() = R.drawable.ic_music_file_picker

    override fun verify(fileName: String): Boolean {

        val isHasSuffix = fileName.contains(".")
        if (!isHasSuffix){
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf(".")  + 1)
        return when (suffix){
            "aif", "iff", "m3u", "m4a", "mid", "mp3", "mpa", "wav", "wma", "ogg", "flac", "ape", "alac" -> {
                true
            }
            else -> {
                false
            }
        }
    }
}