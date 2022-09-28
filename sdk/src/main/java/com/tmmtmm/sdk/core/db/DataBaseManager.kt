package com.tmmtmm.sdk.core.db

import android.content.Context
import android.text.TextUtils
import androidx.room.Room
import com.tmmtmm.sdk.BuildConfig
import com.tmmtmm.sdk.logic.TmLoginLogic

/**
 * @description
 *
 * @version
 */
class DataBaseManager private constructor() {

    private var appDataBase: AppDataBase? = null


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

    fun init(context: Context) {
        val dbName = "tmmtmm/" + BuildConfig.DB_NAME_PREFIX + TmLoginLogic.getInstance().getUserId()
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


    fun close() {
        appDataBase = null
    }

    fun clearDataBase() {
        appDataBase?.clearAllTables()
    }
}