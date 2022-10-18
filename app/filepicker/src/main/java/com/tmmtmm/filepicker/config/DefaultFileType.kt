package com.android.filepicker.config

import com.android.filepicker.filetype.*
import com.android.filepicker.bean.FileItemBeanImpl
import com.tmmtmm.filepicker.filetype.*

/**
 *
 * @author rosu
 * @date 2018/11/27
 */
class DefaultFileType: AbstractFileType(){

    private val allDefaultFileType:ArrayList<FileType> by lazy {
        val fileTypes = ArrayList<FileType>()
        fileTypes.add(AudioFileType())
        fileTypes.add(RasterImageFileType())
        fileTypes.add(CompressedFileType())
        fileTypes.add(DataBaseFileType())
        fileTypes.add(ExecutableFileType())
        fileTypes.add(FontFileType())
        fileTypes.add(PageLayoutFileType())
        fileTypes.add(TextFileType())
        fileTypes.add(VideoFileType())
        fileTypes.add(WebFileType())
        fileTypes
    }

    override fun fillFileType(itemBeanImpl: FileItemBeanImpl): FileItemBeanImpl {
        for (type in allDefaultFileType){
            if (type.verify(itemBeanImpl.fileName)){
                itemBeanImpl.fileType = type
                break
            }
        }
        return itemBeanImpl
    }
}