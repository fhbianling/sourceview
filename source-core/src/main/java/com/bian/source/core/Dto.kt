package com.bian.source.core

/**
 * author fhbianling@163.com
 * date 2021/5/14 14:38
 * 类描述：
 */

enum class Type(val id: String) {
    Text("text"),
    KotlinSource("kt"),
    JavaSource("java"),
    Xml("xml"),
    Json("json"),
    Markdown("md"),
    Html("html"),
    Dir("dir"),
    Unknown("unknown");

    companion object {
        fun of(id: String?): Type {
            return values().firstOrNull { it.id == id } ?: Unknown
        }
    }
}

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