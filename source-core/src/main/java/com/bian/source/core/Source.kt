package com.bian.source.core

import android.content.Context
import com.google.gson.Gson
import java.security.MessageDigest

/**
 * author fhbianling@163.com
 * date 2021/5/14 14:41
 * 类描述：
 */

internal const val ASSETS_NAME = "generated.srcv"

object Source {
    private var mAssetsJson: AssetsJson? = null

    fun query(context: Context, key: String): SourceIndex? {
        getAssetsJson(context)?.let {
            return it.entry[key]
        }
        return null
    }

    fun query(context: Context, name: String, moduleName: String): SourceIndex? {
        return query(context, getMappingId(name, moduleName))
    }

    fun queryBytes(context: Context, sourceIndex: SourceIndex): ByteArray? {
        var result: ByteArray? = null
        getAssetsJson(context)?.let {
            val fileIndex = it.file.getOrNull(sourceIndex.fileId ?: -1) ?: return null
            context.openSource { bytes ->
                result = bytes.readFileIndex(fileIndex)
            }
        }
        return result
    }

    internal fun attach(context: Context, sourceIndex: SourceIndex) {
        getAssetsJson(context)?.let {
            val stored = it.entry[sourceIndex.id] ?: return
            sourceIndex.type = stored.type
            sourceIndex.children = stored.children
            sourceIndex.fileId = stored.fileId
        }
    }

    private fun getAssetsJson(context: Context): AssetsJson? {
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

    private fun Context.openSource(invoke: (ByteArray) -> Unit) {
        assets.list("")?.contains(ASSETS_NAME) ?: return
        assets.open(ASSETS_NAME).use {
            invoke(it.readBytes())
        }
    }

    private fun ByteArray.readFileIndex(fileIndex: FileIndex): ByteArray {
        val start = readInt(4)
        val result = ByteArray(fileIndex.byteLength)
        System.arraycopy(this, 8 + start + fileIndex.byteOffset, result, 0, result.size)
        return result
    }

    private fun ByteArray.readInt(start: Int): Int {
        var value = 0
        for (i in 0..3) {
            val shift = (3 - i) * 8
            value += (this[start + i].toInt() and 0xFF) shl shift
        }
        return value
    }


    private fun getMappingId(mappingName: String, moduleName: String): String {
        val string = "$moduleName\$$mappingName"
        val md5 = MessageDigest.getInstance("MD5")
        md5.update(string.toByteArray())
        val bytes = md5.digest()
        var i: Int
        val buf = StringBuffer("")
        for (j in bytes.indices) {
            i = bytes[j].toInt()
            if (i < 0) {
                i += 256
            }
            if (i < 16) {
                buf.append("0")
            }
            buf.append(Integer.toHexString(i))
        }
        return buf.toString().substring(8, 24)
    }
}