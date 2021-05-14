package com.bian.source.core

/**
 * author fhbianling@163.com
 * date 2021/5/14 14:38
 * 类描述：
 */


internal data class FileIndex(val byteOffset: Int, val byteLength: Int)

internal data class AssetsJson(
    val file: Array<FileIndex>,
    val entry: Map<String, SourceIndex>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AssetsJson

        if (!file.contentEquals(other.file)) return false
        if (entry != other.entry) return false

        return true
    }

    override fun hashCode(): Int {
        var result = file.contentHashCode()
        result = 31 * result + entry.hashCode()
        return result
    }
}