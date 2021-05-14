package com.bian.source.core

import android.content.Context
import com.google.gson.annotations.SerializedName
import java.io.File

/**
 * author fhbianling@163.com
 * date 2021/5/14 15:15
 * 类描述：
 */
class SourceIndex() {
    var name: String? = null
    var type: String? = null

    @SerializedName("index")
    var fileId: Int? = null
    var children: List<SourceIndex>? = null

    private var attached = false

    internal var id: String? = null

    constructor(name: String, id: String) : this() {
        this.name = name
        this.id = id
    }

    fun queryContent(context: Context): ByteArray? {
        ensureAttach(context)
        return Source.queryBytes(context, this)
    }

    fun queryChildContent(path: String): ByteArray? {
        val split = path.split(File.separator)
        if (split.isEmpty()) {
            return null
        }
        return null
    }

    private fun ensureAttach(context: Context) {
        if (!attached) {
            Source.attach(context, this@SourceIndex)
            attached = true
        }
    }

    override fun toString(): String {
        return "SourceIndex(name=$name, type=$type, children=$children)"
    }

}