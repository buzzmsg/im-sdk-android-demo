package com.tmmtmm.sdk.core.db

import android.content.Context
import android.text.TextUtils
import androidx.room.Room
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.tmmtmm.sdk.BuildConfig
import com.tmmtmm.sdk.cache.LoginCache
import com.tmmtmm.sdk.logic.TmLoginLogic
import java.io.File

/**
 * @description
 *
 * @version
 */
class DataBaseManager private constructor() {

    private var appDataBase: AppDataBase? = null
    private var shareDataBase: ShareDataBase? = null

    companion object {

        private var instance: DataBaseManager? = null

        private const val QUERY_LIMIT = 900

        private const val PAGE_SIZE = 100

        @JvmName("getInstance1")
        fun getInstance(): DataBaseManager {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = DataBaseManager()
                    }
                }
            }
            return instance!!
        }
    }

    fun init(context: Context, aKey: String, env: String, userId: String) {
//        val dbName =
//            PathUtils.getInternalAppDbPath(aKey + File.separator + env + File.separator + BuildConfig.DB_NAME_PREFIX + TmLoginLogic.getInstance())
//        val dbName =
//            "database" + File.separator + aKey + File.separator + env + File.separator +  BuildConfig.DB_NAME_PREFIX + TmLoginLogic.getInstance()
//                .getUserId()
        val dbPath = File.separator + aKey + File.separator + env + File.separator
        val dbRootPath = PathUtils.getInternalAppDbsPath()
        val path = dbRootPath + dbPath
        FileUtils.createOrExistsDir(path)
        val dbName =
            path + BuildConfig.DB_NAME_PREFIX + userId + ".db"


        if (TextUtils.isEmpty(dbName)) {
            return
        }
        if (appDataBase == null) {
            try {
                appDataBase = Room
                    .databaseBuilder(context, AppDataBase::class.java, dbName)
                    .allowMainThreadQueries()
                    .build()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun initShare(context: Context,aKey: String,env: String) {
//        val dbName =
//            "${aKey}/${env}/" + BuildConfig.DB_NAME_PREFIX + "share"
//        val dbName =
//            PathUtils.getInternalAppDbPath(aKey + File.separator + env + File.separator + BuildConfig.DB_NAME_PREFIX + "share.db")
        val dbPath = File.separator + aKey + File.separator + env + File.separator
        val dbRootPath = PathUtils.getInternalAppDbsPath()
        val path = dbRootPath + dbPath
        FileUtils.createOrExistsDir(path)
        val dbName = path + BuildConfig.DB_NAME_PREFIX + "share.db"

        if (TextUtils.isEmpty(dbName)) {
            return
        }
        if (shareDataBase == null) {
            try {
                shareDataBase = Room
                    .databaseBuilder(context, ShareDataBase::class.java, dbName)
                    .allowMainThreadQueries()
                    .build()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun <T, R> splitMap(
        ids: MutableList<String>,
        block: ((ids: MutableList<String>) -> Map<T, R>)
    ): Map<T, R> {
        return if (ids.size > QUERY_LIMIT) {
            val splitSize = ids.chunked(QUERY_LIMIT)
            val result = hashMapOf<T, R>()
            for (list in splitSize) {
                result.putAll(block(list.toMutableList()))
            }
            result
        } else {
            block(ids)
        }
    }

    fun <T> splitArray(
        ids: MutableList<String>,
        block: ((ids: MutableList<String>) -> MutableList<T>)
    ): MutableList<T> {
        return if (ids.size > QUERY_LIMIT) {
            val splitSize = ids.chunked(QUERY_LIMIT)
            val result = mutableListOf<T>()
            for (list in splitSize) {
                result.addAll(block(list.toMutableList()))
            }
            result
        } else {
            block(ids)
        }
    }

    fun splitArray1(ids: MutableList<String>, block: ((ids: MutableList<String>) -> Unit)) {
        if (ids.size > QUERY_LIMIT) {
            val splitSize = ids.chunked(QUERY_LIMIT)
            for (list in splitSize) {
                block(list.toMutableList())
            }
        } else {
            block(ids)
        }
    }

    fun getDataBase(): AppDataBase? {
        if (appDataBase == null) {
            return null
        }
        return appDataBase as AppDataBase
    }

    fun getShareDb(): ShareDataBase? {
        if (shareDataBase == null) {
            return null
        }
        return shareDataBase as ShareDataBase
    }


    fun close() {
        appDataBase = null
    }
}