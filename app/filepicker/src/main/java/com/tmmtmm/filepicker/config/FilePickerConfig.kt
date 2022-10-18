package com.tmmtmm.filepicker.config

import android.content.Intent
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import com.android.filepicker.FilePickerActivity
import com.android.filepicker.config.*
import com.tmmtmm.filepicker.R


/**
 *
 * @author rosu
 * @date 2018/11/27
 */
class FilePickerConfig(private val pickerManager: FilePickerManager) {

    private val contextRes = FilePickerManager.contextRef!!.get()!!.resources

    var isShowHiddenFiles = false

    var isShowingCheckBox = true

    var isSkipDir = true

    var singleChoice = false

    var maxSelectable = Int.MAX_VALUE

    var mediaStorageName = "sd"

    @get:StorageMediaType
    @set:StorageMediaType
    var mediaStorageType: String = STORAGE_EXTERNAL_STORAGE

    var customRootPath: String = ""

    var selfFilter: AbstractFileFilter? = null

    var selfFileType: AbstractFileType? = null
    val defaultFileType: DefaultFileType by lazy { DefaultFileType() }

    var fileItemOnClickListener: FileItemOnClickListener? = null

    var themeId: Int = R.style.FilePickerThemeRail

    var selectAllText: String = "all"

    var deSelectAllText: String =
        "Cancel"

    @StringRes
    var hadSelectedText: Int = 0
    var confirmText: String = "OK"

    @StringRes
    var maxSelectCountTips: Int = 0
    var emptyListTips: String = "Nothing"

    fun showHiddenFiles(isShow: Boolean): FilePickerConfig {
        isShowHiddenFiles = isShow
        return this
    }

    fun showCheckBox(isShow: Boolean): FilePickerConfig {
        isShowingCheckBox = isShow
        return this
    }

    fun skipDirWhenSelect(isSkip: Boolean): FilePickerConfig {
        isSkipDir = isSkip
        return this
    }

    fun maxSelectable(max: Int): FilePickerConfig {
        maxSelectable = if (max < 0) Int.MAX_VALUE else max
        return this
    }

    @JvmOverloads
    fun storageType(volumeName: String = "", @StorageMediaType storageMediaType: String): FilePickerConfig {
        mediaStorageName = volumeName
        mediaStorageType = storageMediaType
        return this
    }

    fun setCustomRootPath(path: String): FilePickerConfig {
        customRootPath = path
        return this
    }

    fun filter(fileFilter: AbstractFileFilter): FilePickerConfig {
        selfFilter = fileFilter
        return this
    }

    fun fileType(fileType: AbstractFileType): FilePickerConfig {
        selfFileType = fileType
        return this
    }

    fun setItemClickListener(fileItemOnClickListener: FileItemOnClickListener): FilePickerConfig {
        this.fileItemOnClickListener = fileItemOnClickListener
        return this
    }

    fun setTheme(themeId: Int): FilePickerConfig {
        this.themeId = themeId
        return this
    }


    fun enableSingleChoice(): FilePickerConfig {
        this.singleChoice = true
        return this
    }


    fun setText(
        @NonNull selectAllString: String = "all",
        @NonNull unSelectAllString: String = "Cancel",
        @NonNull @StringRes hadSelectedStrRes: Int = 0,
        @NonNull confirmText: String = "OK",
        @NonNull @StringRes maxSelectCountTipsStrRes: Int = 0,
        @NonNull emptyListTips: String = "Nothing"
    ): FilePickerConfig {
        this.selectAllText = selectAllString
        this.deSelectAllText = unSelectAllString
        this.hadSelectedText = hadSelectedStrRes
        this.confirmText = confirmText
        this.maxSelectCountTips = maxSelectCountTipsStrRes
        this.emptyListTips = emptyListTips
        return this
    }

    fun forResult(requestCode: Int) {
        val activity = FilePickerManager.contextRef?.get()!!
        val fragment = FilePickerManager.fragmentRef?.get()

        val intent = Intent(activity, FilePickerActivity::class.java)
        if (fragment == null) {
            activity.startActivityForResult(intent, requestCode)
        } else {
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    companion object {
        @get:StorageMediaType
        const val STORAGE_EXTERNAL_STORAGE = "STORAGE_EXTERNAL_STORAGE"
        @get:StorageMediaType
        const val STORAGE_UUID_SD_CARD = "STORAGE_UUID_SD_CARD"
        @get:StorageMediaType
        const val STORAGE_UUID_USB_DRIVE = "STORAGE_UUID_USB_DRIVE"
        @get:StorageMediaType
        const val STORAGE_CUSTOM_ROOT_PATH = "STORAGE_CUSTOM_ROOT_PATH"

        @Retention(AnnotationRetention.SOURCE)
        annotation class StorageMediaType
    }
}