package com.tmmtmm.demo.api

import com.blankj.utilcode.util.GsonUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.lang.reflect.Type


/**
 * @description
 *
 * @time 2021/5/18 11:40 上午
 * @version
 */
fun JsonObject.putString(key: String, value: String): JsonObject {
    addProperty(key, value)
    return this
}

fun JsonObject.putNumber(key: String, value: Number): JsonObject {
    addProperty(key, value)
    return this
}

fun JsonObject.putJson(key: String, value: JsonObject): JsonObject {
    add(key, value)
    return this
}

fun Any.toJson(): JsonObject {
    return try {
        GsonBuilder()
            .serializeNulls()
            .create().toJsonTree(this).asJsonObject
    } catch (e: Exception) {
        e.printStackTrace()
        return JsonObject()
    }
}

fun Any.toJsonArray(): JsonArray {
    return GsonBuilder()
        .serializeNulls()
        .create().toJsonTree(this).asJsonArray
}

fun fromStringListJson(json: String): List<String> {
    return try {
        GsonUtils.fromJson(json, GsonUtils.getListType(String::class.java))
    } catch (e: Exception) {
        emptyList()
    }
}

fun stringListToJson(list: List<String>?): String {
    return GsonUtils.toJson(list ?: emptyList<String>())
}

inline fun <reified T> String.responseToEntity(type: Type): T? {
    var result: T? = null
    try {
        val gson = GsonBuilder()
            .serializeNulls()
            .create()
        result = GsonUtils.fromJson<T>(gson, this, type)
    } catch (e: Exception) {
        result = GsonUtils.fromJson<T>( this, T::class.java)
        e.printStackTrace()
    } finally {
        return result
    }
}

inline fun <reified T> String.toEntity(): T? {
    val gson = Gson()
    var result: T? = null
    try {
        result = gson.fromJson(this, T::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        return result
    }
}

inline fun <reified T> String.toEntityList(): List<T>? {
    val gson = Gson()
    var result: List<T>? = null
    try {
        result = GsonUtils.fromJson(this, GsonUtils.getListType(T::class.java))
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        return result
    }
}


