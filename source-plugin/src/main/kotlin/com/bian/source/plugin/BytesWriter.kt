//package com.bian.source.plugin
//
//import com.bian.source.plugin.SourcePlugin.Companion.paddedLength
//import java.io.File
//
///**
// * author fhbianling@163.com
// * date 2021/5/13 17:27
// * 类描述：
// */
//object BytesWriter {
//    fun Pair<ByteArray, ByteArray>.writeToFile(file: File) {
//        val fileBytes = first
//        val jsonBytes = second
//        val paddedLength = jsonBytes.size.paddedLength()
//        val dataBytes = ByteArray(8 + paddedLength + fileBytes.size)
//        dataBytes.writeInt(0, jsonBytes.size)
//        dataBytes.writeInt(4, paddedLength)
//        System.arraycopy(jsonBytes, 0, dataBytes, 8, jsonBytes.size)
//        System.arraycopy(fileBytes, 0, dataBytes, 8 + paddedLength, fileBytes.size)
//        file.safeWriteBytes(dataBytes)
//    }
//
//    private fun ByteArray.writeInt(start: Int, value: Int) {
//        this[start] = ((value shr 24) and 0XFF).toByte()
//        this[start + 1] = ((value shr 16) and 0XFF).toByte()
//        this[start + 2] = ((value shr 8) and 0XFF).toByte()
//        this[start + 3] = (value and 0XFF).toByte()
//    }
//
//    fun File.safeWriteText(text: String) {
//        safeWriteBytes(text.toByteArray())
//    }
//
//    fun File.safeWriteBytes(bytes: ByteArray) {
//        if (!parentFile.exists()) {
//            parentFile.mkdirs()
//        }
//        if (!exists()) {
//            createNewFile()
//        }
//        writeBytes(bytes)
//    }
//}