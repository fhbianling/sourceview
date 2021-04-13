package com.bian.sourceviewcore.tool

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * author fhbianling@163.com
 * date 2021/3/16 14:53
 * 类描述：
 */
class SourceCodeManager {
    private val gson = Gson()
    fun querySourceCode(ctx: Context, key: String): String {
        val type = object : TypeToken<Map<String, Map<String, String>>>() {}.type
        ctx.assets.list("SourceCode")?.forEach {
            Log.d("SourceCodeManager", "query:$it")
            val bytes = ctx.assets.open("SourceCode/$it").readBytes()
            val json = String(bytes)
            val map: Map<String, Map<String, String>> =
                gson.fromJson(json, type) ?: return@forEach
            for (entry in map) {
                for ((t, u) in entry.value) {
                    if (t == key) {
                        return u
                    }
                }
            }
        }
        return ""
    }

    companion object {
        val instance: SourceCodeManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { SourceCodeManager() }
    }
}