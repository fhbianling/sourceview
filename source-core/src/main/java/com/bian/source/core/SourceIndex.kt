package com.bian.source.core

import android.content.Context
import com.bian.source.core.view.DirSourceActivity
import com.bian.source.core.view.FileSourceActivity
import com.google.gson.annotations.SerializedName
import java.util.*

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

    internal var children: List<SourceIndex>? = null
    internal var id: String? = null

    private var _rootId: String? = null
    private var _path: String? = null

    internal var rootId: String?
        get() {
            ensureAttach()
            return _rootId
        }
        set(value) {
            _rootId = value
        }
    var path: String?
        get() {
            ensureAttach()
            return _path
        }
        internal set(value) {
            _path = value
        }

    private var attached = false

    constructor(name: String, id: String) : this() {
        this.name = name
        this.id = id
    }

    fun content(): ByteArray? {
        ensureAttach()
        return Source.instance.queryBytes(this)
    }

    fun contentToString(): String {
        return content()?.let { fixSourceContent(String(it)) } ?: ""
    }

    fun queryChildByPath(path: String): SourceIndex? {
        ensureAttach()
        return internalQueryChildByPath(path)
    }

    fun open(context: Context) {
        ensureAttach()
        if (isDir) {
            if (_path != null && id != null)
                DirSourceActivity.start(context, this)
        } else {
            FileSourceActivity.start(context, this)
        }
    }

    fun queryChildByName(
        name: String,
        onlyFile: Boolean = true
    ): Array<SourceIndex> {
        ensureAttach()
        return internalQueryChildByName(name, onlyFile)
    }

    private fun fixChildPath() {
        children?.forEach {
            if (it._path == null) {
                it._path = "$_path$PATH_DELIMITER${it.name}"
                it._rootId = _rootId
            }
            if (it.isDir && it.children != null) {
                it.fixChildPath()
            }
        }
    }

    internal fun copyStored(stored: SourceIndex) {
        type = stored.type
        children = stored.children
        fileId = stored.fileId
        _path = name
        _rootId = id
        fixChildPath()
    }

    operator fun get(key: String): SourceIndex? {
        if (key.contains("\\") || key.contains("/"))
            return queryChildByPath(key)
        return queryChildByName(key).firstOrNull()
    }

    private fun ensureAttach() {
        if (!attached) {
            Source.instance.attach(this@SourceIndex)
            attached = true
        }
    }

    override fun toString(): String {
        return "SourceIndex(name=$name, type=$type, fileId=$fileId, children=$children, id=$id, rootId=$_rootId, path=$_path, attached=$attached)"
    }


    companion object {
        const val PATH_DELIMITER = "\\"
    }
}