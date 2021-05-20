package com.bian.source.core

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.util.*

/**
 * author fhbianling@163.com
 * date 2021/5/20 10:08
 * 类描述：
 */


internal fun SourceIndex.initStack(path: String, stack: Stack<SourceIndex>) {
    stack.clear()
    val split = path.split(SourceIndex.PATH_DELIMITER)
    var temp: SourceIndex? = this
    var index = 1
    while (temp != null) {
        stack.push(temp)
        val name = split.getOrNull(index)
        temp = temp.findChildByName(name)
        index++
    }
}

internal fun Stack<SourceIndex>.generateSpannableString(onClick: (SourceIndex?) -> Unit): SpannableString {
    val string = StringBuilder()
    val indexCache = mutableListOf<Triple<Int, Int, Int>>()
    var start = 0
    for (i in 0 until size) {
        val sourceIndex = get(i)
        val suffix = if (i == size - 1) "" else ">"
        val text = " ${sourceIndex.name} $suffix"
        val end = start + text.length
        indexCache.add(Triple(start + 1, end - 1, i))
        start = end
        string.append(text)
    }
    val spannable = SpannableString(string.toString())
    indexCache.forEach { (start, end, stackIndex) ->
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onClick.invoke(get(stackIndex))
                }
            }, start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(Color.BLUE),
            start,
            end,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannable
}

internal fun SourceIndex.internalQueryChildByName(
    name: String,
    onlyFile: Boolean = true
): Array<SourceIndex> {
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

internal fun SourceIndex.internalQueryChildByPath(path: String): SourceIndex? {
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

internal val SourceIndex.isDir
    get() = !children.isNullOrEmpty()

internal fun SourceIndex.findChildByName(name: String?): SourceIndex? {
    return children?.firstOrNull { it.name == name }
}

fun SourceIndex.fixSourceContent(source: String): String {
    var fixed = source
    if (type == Type.Xml.id) {
        fixed = fixXml(fixed)
    } else if (type == Type.Json.id) {
        fixed = fixJson(fixed)
    }
    return fixed
}

// 对于xml,html,highlight.js需要将<>更换为对应转义符
private fun fixXml(xml: String): String {
    var fixed = xml
    fixed = fixed.replace("<", "&lt;")
    return fixed.replace(">", "&gt;")
}

// json格式化
private fun fixJson(jsonStr: String): String {
    Log.d("XXXX", "fixJson")
    val jsonIndent = 2
    try {
        if (TextUtils.isEmpty(jsonStr)) return jsonStr
        val jsonStrWrapper: String = jsonStr.trim()
        if (jsonStrWrapper.startsWith("{")) {
            val jsonObject = JSONObject(jsonStrWrapper)
            return jsonObject.toString(jsonIndent)
        }
        if (jsonStrWrapper.startsWith("[")) {
            val jsonArray = JSONArray(jsonStrWrapper)
            return jsonArray.toString(jsonIndent)
        }
    } catch (e: JSONException) {
        return jsonStr
    }
    return jsonStr
}

/**
 * 获得一个unique id ，对于固定的mappingName和moduleName组合，在source-plugin和source-core中都会产生相同的id
 */
internal fun getMappingId(mappingName: String, moduleName: String): String {
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

internal fun ByteArray.readFileIndex(fileIndex: FileIndex): ByteArray {
    val start = readInt(4)
    val result = ByteArray(fileIndex.byteLength)
    System.arraycopy(this, 8 + start + fileIndex.byteOffset, result, 0, result.size)
    return result
}

internal val SourceIndex.fileIndex
    get() = Source.instance.queryFileIndex(this)

internal fun ByteArray.readInt(start: Int): Int {
    var value = 0
    for (i in 0..3) {
        val shift = (3 - i) * 8
        value += (this[start + i].toInt() and 0xFF) shl shift
    }
    return value
}

internal fun Context.openSource(invoke: (ByteArray) -> Unit) {
    assets.list("")?.contains(ASSETS_NAME) ?: return
    assets.open(ASSETS_NAME).use {
        invoke(it.readBytes())
    }
}

