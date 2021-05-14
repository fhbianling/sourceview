//package com.bian.source.plugin.dto
//
//import com.bian.source.plugin.FileContainer
//import com.bian.source.plugin.FileIndex
//import java.io.File
//import java.io.Serializable
//
///**
// * author fhbianling@163.com
// * date 2021/5/13 15:40
// * 类描述：
// */
//data class FileDto(val path: String, val name: String, val typeId: String) : Serializable
//
//data class AssetJson(val files: Collection<FileIndex>, val entries: Map<String, Chunk>)
//
//data class SourceBundle(
//    val ignoreUnknownFile: Boolean,
//    val ignoreEmptyDirectory: Boolean,
//    val moduleName: String,
//    val dtoList: ArrayList<FileDto>
//) : Serializable
//
//enum class Type(val id: String) {
//    Text("text"),
//    KotlinSource("kt"),
//    JavaSource("java"),
//    Xml("xml"),
//    Json("json"),
//    Markdown("md"),
//    Html("html"),
//    Dir("dir"),
//    Unknown("unknown");
//
//    companion object {
//        fun getTypeIdByPath(path: String): String {
//            return values().firstOrNull { path.endsWith(".${it.id}") }?.id ?: Unknown.id
//        }
//    }
//}
//
//open class Chunk(val name: String, @Transient val file: File) : Serializable
//
//class FileChunk(name: String, var fileId: Int, val type: String, file: File) : Chunk(name, file)
//
//class DirChunk(
//    file: File,
//    name: String,
//    fileContainer: FileContainer,
//    ignoreUnknownFile: Boolean,
//    ignoreEmptyDirectory: Boolean
//) : Chunk(name, file) {
//    val children: MutableList<Chunk> = mutableListOf()
//
//    @Transient
//    var hasFile = false
//
//    init {
//        file.listFiles()?.forEach { f ->
//            if (f.isDirectory) {
//                val chunk =
//                    DirChunk(f, f.name, fileContainer, ignoreUnknownFile, ignoreEmptyDirectory)
//                if (chunk.hasFile) {
//                    this.hasFile = true
//                }
//                if (!ignoreEmptyDirectory || chunk.hasFile) {
//                    children.add(chunk)
//                }
//            } else {
//                val typeId = Type.getTypeIdByPath(f.absolutePath)
//                if (!(ignoreUnknownFile && typeId == Type.Unknown.id)) {
//                    this.hasFile = true
//                    children.add(FileChunk(f.name, -1, typeId, f))
//                    fileContainer.putFile(f)
//                }
//            }
//        }
//    }
//}
