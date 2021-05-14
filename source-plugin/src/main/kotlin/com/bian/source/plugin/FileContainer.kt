//package com.bian.source.plugin
//
//import com.bian.source.plugin.SourcePlugin.Companion.paddedLength
//import com.bian.source.plugin.dto.Chunk
//import com.bian.source.plugin.dto.DirChunk
//import com.bian.source.plugin.dto.FileChunk
//import java.io.File
//
///**
// * author fhbianling@163.com
// * date 2021/5/13 16:43
// * 类描述：
// */
//data class FileIndex(@Transient val index: Int, var offset: Int, val length: Int)
//
//class FileContainer {
//    private val map = linkedMapOf<File, FileIndex>()
//    val fileIndices get() = map.values
//
//    fun putFile(file: File) {
//        if (!map.containsKey(file)) {
//            map[file] = FileIndex(map.size, -1, file.length().toInt())
//        }
//    }
//
//    fun defineChunkFileIndex(chunk: Chunk) {
//        if (chunk is DirChunk) {
//            chunk.children.forEach {
//                defineChunkFileIndex(it)
//            }
//        } else if (chunk is FileChunk) {
//            chunk.fileId = map[chunk.file]?.index ?: -1
//        }
//    }
//
//    fun generateFileBytes(): ByteArray {
//        val length = map.keys.fold(0) { acc, file -> file.length().paddedLength() + acc }
//        val bytes = ByteArray(length)
//        var offset = 0
//        map.forEach { (f, fileIndex) ->
//            fileIndex.offset = offset
//            System.arraycopy(f.readBytes(), 0, bytes, offset, f.length().toInt())
//            offset += f.length().paddedLength()
//        }
//        return bytes
//    }
//}