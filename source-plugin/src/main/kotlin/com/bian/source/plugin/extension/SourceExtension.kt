//package com.bian.source.plugin.extension
//
//import com.bian.source.plugin.dto.FileDto
//import com.bian.source.plugin.dto.Type
//import java.io.File
//
///**
// * author fhbianling@163.com
// * date 2021/5/13 15:11
// * 类描述：
// */
//@Suppress("unused")
//open class SourceExtension {
//    var enableBinding = true
//    var ignoreUnknownFile = true
//    var ignoreEmptyDirectory = true
//    internal var moduleName = ""
//    internal var projectDir = ""
//    internal val dtoList = mutableListOf<FileDto>()
//
//    fun dir(map: Map<String, Any>) {
//        putFile(
//            map["path"] as? String,
//            map["name"] as? String,
//            Type.Dir.id,
//            map["absolute"] as? Boolean,
//            true
//        )
//    }
//
//    fun dir(path: String) {
//        putFile(path, null, Type.Dir.id, absolute = false, putDir = true)
//    }
//
//    fun src(map: Map<String, Any>) {
//        putFile(
//            map["path"] as? String,
//            map["name"] as? String,
//            map["type"] as? String,
//            map["absolute"] as? Boolean,
//            false
//        )
//    }
//
//    fun src(path: String) {
//        putFile(path, null, null, absolute = false, putDir = false)
//    }
//
//    private fun putFile(
//        path: String?,
//        name: String?,
//        type: String?,
//        absolute: Boolean?,
//        putDir: Boolean
//    ) {
//        if (!putDir && type == Type.Dir.id) {
//            throw IllegalArgumentException("type dir doesn't work in file(..)")
//        }
//        if (path.isNullOrEmpty()) {
//            throw IllegalArgumentException("must specify path,path:$path,name:$name,type:$type,absolute:$absolute")
//        }
//        val realPath = getRealPath(path, absolute ?: false)
//        val displayName = name ?: getDefaultName(path)
//        val typeId = type ?: Type.getTypeIdByPath(path)
//        val fileDto = FileDto(realPath, displayName, typeId)
//        dtoList.add(fileDto)
//    }
//
//    private fun getDefaultName(path: String): String {
//        return if (path.contains(File.separator)) {
//            path.substring(path.lastIndexOf(File.separator) + 1)
//        } else {
//            path
//        }
//    }
//
//    private fun getRealPath(path: String, absolute: Boolean): String {
//        return if (absolute) path else (projectDir + File.separator + path)
//    }
//}