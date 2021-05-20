package com.bian.source.core

import android.os.Parcel
import android.os.Parcelable

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

internal data class FileIndex(val byteOffset: Int, val byteLength: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(byteOffset)
        parcel.writeInt(byteLength)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FileIndex> {
        override fun createFromParcel(parcel: Parcel): FileIndex {
            return FileIndex(parcel)
        }

        override fun newArray(size: Int): Array<FileIndex?> {
            return arrayOfNulls(size)
        }
    }
}

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