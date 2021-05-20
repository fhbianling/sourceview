package com.bian.source.core

import android.content.Context
import android.util.Log
import com.google.gson.Gson

/**
 * author fhbianling@163.com
 * date 2021/5/14 14:41
 * 类描述：
 */

internal const val ASSETS_NAME = "generated.srcv"

class Source {
    private var mAssetsJson: AssetsJson? = null
    private lateinit var context: Context
    fun init(context: Context) {
        this.context = context.applicationContext
    }

    fun query(id: String): SourceIndex? {
        getAssetsJson()?.let {
            return it.entry[id]
        }
        return null
    }

    fun query(name: String, moduleName: String): SourceIndex? {
        return query(getMappingId(name, moduleName))
    }

    fun queryBytes(sourceIndex: SourceIndex): ByteArray? {
        return sourceIndex.fileIndex?.let { queryBytes(it) }
    }

    internal fun queryFileIndex(sourceIndex: SourceIndex): FileIndex? {
        return getAssetsJson()?.file?.getOrNull(sourceIndex.fileId ?: -1)
    }

    private fun queryBytes(fileIndex: FileIndex): ByteArray? {
        var result: ByteArray? = null
        context.openSource { bytes ->
            result = bytes.readFileIndex(fileIndex)
        }
        return result
    }

    internal fun attach(sourceIndex: SourceIndex) {
        getAssetsJson()?.let {
            val stored = it.entry[sourceIndex.id] ?: return
            sourceIndex.copyStored(stored)
        }
    }

    private fun getAssetsJson(): AssetsJson? {
        if (mAssetsJson == null) {
            context.openSource {
                val jsonBytesSize = it.readInt(0)
                val jsonBytes = ByteArray(jsonBytesSize)
                System.arraycopy(it, 8, jsonBytes, 0, jsonBytesSize)
                val gson = Gson()
                mAssetsJson = gson.fromJson(String(jsonBytes), AssetsJson::class.java)
            }
        }
        return mAssetsJson
    }

    companion object {
        val instance: Source by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { Source() }
        fun init(context: Context) {
            instance.init(context)
        }
    }
}