package com.tmmtmm.filepicker.utils

import android.content.Context
import android.os.Environment
import com.tmmtmm.filepicker.bean.BeanSubscriber
import com.android.filepicker.bean.FileItemBeanImpl
import com.tmmtmm.filepicker.config.FilePickerConfig.Companion.STORAGE_CUSTOM_ROOT_PATH
import com.tmmtmm.filepicker.config.FilePickerConfig.Companion.STORAGE_EXTERNAL_STORAGE
import com.android.filepicker.config.FilePickerManager
import com.android.filepicker.bean.FileNavBeanImpl
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

/**
 *
 * @author rosu
 * @date 2018/11/22
 */
const val MAX_FILE_SELECT_SIZE = 1024 * 1024 * 500
class FileUtils {

    companion object {

        fun getRootFile(): File {
            return when (FilePickerManager.config.mediaStorageType) {
                STORAGE_EXTERNAL_STORAGE -> {
                    File(Environment.getExternalStorageDirectory().absoluteFile.toURI())
                }
                STORAGE_CUSTOM_ROOT_PATH -> {
                    if (FilePickerManager.config.customRootPath.isEmpty()) {
                        File(Environment.getExternalStorageDirectory().absoluteFile.toURI())
                    } else {
                        File(FilePickerManager.config.customRootPath)
                    }
                }
                else -> {
                    File(Environment.getExternalStorageDirectory().absoluteFile.toURI())
                }
            }
        }

        fun produceListDataSource(
            rootFile: File,
            beanSubscriber: BeanSubscriber
        ): ArrayList<FileItemBeanImpl> {
            val listData: ArrayList<FileItemBeanImpl> = ArrayList()
            val fileList = rootFile.listFiles()

            if (fileList.isNullOrEmpty()) return listData
            for (file in fileList) {

                try {
                    val isHiddenFile = file.name.startsWith(".")

                    val readAttributes =
                        Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
                    val createTime = readAttributes.lastModifiedTime().toMillis()

                    if (file.isDirectory) {
                        listData.add(
                            FileItemBeanImpl(
                                file.name,
                                file.path,
                                createTime,
                                false,
                                null,
                                true,
                                isHiddenFile,
                                beanSubscriber
                            )
                        )
                        continue
                    }



                    if (file.length() <= MAX_FILE_SELECT_SIZE) {
                        val itemBean = FileItemBeanImpl(
                            file.name,
                            file.path,
                            createTime,
                            false,
                            null,
                            false,
                            isHiddenFile,
                            beanSubscriber
                        )
                        FilePickerManager.config.selfFileType?.fillFileType(itemBean)
                            ?: FilePickerManager.config.defaultFileType.fillFileType(itemBean)
                        listData.add(itemBean)
                    }
                } catch (e: Exception) {

                }

            }
            listData.run {
                this.hideFiles<FileItemBeanImpl>(!FilePickerManager.config.isShowHiddenFiles)
                //this.sortWith(compareBy({!it.isDir}, {it.createTime}))

                this.sortedBy {
                    it.createTime
                }
            }
            return FilePickerManager.config.selfFilter?.doFilter(listData) ?: listData
        }


        fun produceNavDataSource(
            currentDataSource: ArrayList<FileNavBeanImpl>,
            nextPath: String,
            context: Context
        ): ArrayList<FileNavBeanImpl> {

            if (currentDataSource.isEmpty()) {
                currentDataSource.add(
                    FileNavBeanImpl(
                        if (FilePickerManager.config.mediaStorageName.isNotEmpty()) {
                            FilePickerManager.config.mediaStorageName
                        } else if (FilePickerManager.config.customRootPath.isNotEmpty()) {
                            FilePickerManager.config.customRootPath
                        } else {
                            "sd"
                        },
                        nextPath
                    )
                )
                return currentDataSource
            }

            for (data in currentDataSource) {
                if (nextPath == currentDataSource.first().dirPath) {
                    return ArrayList(currentDataSource.subList(0, 1))
                }
                val isCurrent = nextPath == currentDataSource[currentDataSource.size - 1].dirPath
                if (isCurrent) {
                    return currentDataSource
                }

                val isBackToAbove = nextPath == data.dirPath
                if (isBackToAbove) {
                    return ArrayList(
                        currentDataSource.subList(
                            0,
                            currentDataSource.indexOf(data) + 1
                        )
                    )
                }
            }

            currentDataSource.add(
                FileNavBeanImpl(
                    nextPath.substring(nextPath.lastIndexOf("/") + 1),
                    nextPath
                )
            )
            return currentDataSource
        }
    }
}

private fun <E> java.util.ArrayList<FileItemBeanImpl>?.hideFiles(hide: Boolean) {
    if (hide) {
        this?.removeAll { it.isHide }
    }
}
