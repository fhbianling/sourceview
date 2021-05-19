package com.bian.source.core

import android.text.TextUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * author fhbianling@163.com
 * date 2021/5/19 17:33
 * 类描述：
 */
object Util {

    fun fixSourceContent(source: String, type: Type): String? {
        // 对于xml,html,highlight.js需要将<>更换为对应转义符
        var fixed = source
        if (type === Type.Xml) {
            fixed = fixXml(fixed)
        } else if (type === Type.Json) {
            fixed = fixJson(fixed)
        }
        return fixed
    }

    private fun fixXml(xml: String): String {
        var fixed = xml
        fixed = fixed.replace("<", "&lt;")
        return fixed.replace(">", "&gt;")
    }

    private fun fixJson(jsonStr: String): String {
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
}