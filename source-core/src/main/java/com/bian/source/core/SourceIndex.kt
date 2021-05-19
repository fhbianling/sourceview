package com.bian.source.core

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.bian.source.core.view.SourceActivity
import com.google.gson.annotations.SerializedName
import java.io.File
import java.util.*

/**
 * author fhbianling@163.com
 * date 2021/5/14 15:15
 * 类描述：
 */
class SourceIndex() : Parcelable {
    var name: String? = null
    var type: String? = null

    @SerializedName("index")
    var fileId: Int? = null
    private var children: List<SourceIndex>? = null
    private var attached = false

    internal var id: String? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        type = parcel.readString()
        fileId = parcel.readValue(Int::class.java.classLoader) as? Int
        children = parcel.createTypedArrayList(CREATOR)
        attached = parcel.readByte() != 0.toByte()
        id = parcel.readString()
    }

    constructor(name: String, id: String) : this() {
        this.name = name
        this.id = id
    }

    fun queryContent(): ByteArray? {
        ensureAttach()
        return Source.instance.queryBytes(this)
    }

    fun contentToString(): String {
        return queryContent()?.let { Util.fixSourceContent(String(it), Type.of(type)) } ?: ""
    }

    internal fun copyStored(stored: SourceIndex) {
        type = stored.type
        children = stored.children
        fileId = stored.fileId
    }

    fun queryChildByPath(path: String): SourceIndex? {
        ensureAttach()
        if (children == null) return null
        val delimiter = if (path.contains("\\")) "\\" else "/"
        val split = path.split(delimiter)
        if (split.size < 2) return null
        var child = findChildByName(split[1]) ?: return null
        var index = 2
        while (index != split.size) {
            child = child.findChildByName(split[index]) ?: return null
            index++
        }
        return child
    }

    fun view(context: Context) {
        SourceActivity.start(context, this)
    }

    fun queryChildByName(
        name: String,
        onlyFile: Boolean = true
    ): Array<SourceIndex> {
        ensureAttach()
        if (children == null) return emptyArray()
        val list = mutableListOf<SourceIndex>()
        children?.forEach {
            val isFile = it.type != Type.Dir.id
            if (it.name == name) {
                if (isFile || !onlyFile) {
                    list.add(it)
                }
            } else {
                it.queryChildByName(name, onlyFile).let { collection ->
                    list.addAll(collection)
                }
            }
        }
        return list.toTypedArray()
    }

    operator fun get(key: String): SourceIndex? {
        if (key.contains("\\") || key.contains("/"))
            return queryChildByPath(key)
        return queryChildByName(key).firstOrNull()
    }

    private fun findChildByName(name: String?): SourceIndex? {
        return children?.firstOrNull { it.name == name }
    }

    private fun ensureAttach() {
        if (!attached) {
            Source.instance.attach(this@SourceIndex)
            attached = true
        }
    }

    override fun toString(): String {
        return "SourceIndex(name=$name, type=$type, children=$children)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeValue(fileId)
        parcel.writeTypedList(children)
        parcel.writeByte(if (attached) 1 else 0)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SourceIndex> {
        override fun createFromParcel(parcel: Parcel): SourceIndex {
            return SourceIndex(parcel)
        }

        override fun newArray(size: Int): Array<SourceIndex?> {
            return arrayOfNulls(size)
        }
    }
}