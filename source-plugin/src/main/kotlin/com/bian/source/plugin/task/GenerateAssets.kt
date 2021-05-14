//package com.bian.source.plugin.task
//
//import com.bian.source.plugin.BytesWriter.writeToFile
//import com.bian.source.plugin.FileContainer
//import com.bian.source.plugin.SourcePlugin.Companion.OUTPUT_JSON
//import com.bian.source.plugin.SourcePlugin.Companion.generateId
//import com.bian.source.plugin.SourcePlugin.Companion.writeAssetsJson
//import com.bian.source.plugin.dto.*
//import com.google.gson.Gson
//import org.gradle.api.DefaultTask
//import org.gradle.api.tasks.Input
//import org.gradle.api.tasks.OutputDirectory
//import org.gradle.api.tasks.TaskAction
//import java.io.File
//
///**
// * author fhbianling@163.com
// * date 2021/5/13 15:54
// * 类描述：
// */
//
//const val OUTPUT_ASSETS_NAME = "generate.src"
//
//open class GenerateAssets : DefaultTask() {
//
//    @Input
//    var sourceBundles: ArrayList<SourceBundle>? = null
//
//    @OutputDirectory
//    var outputDir: File? = null
//
//    init {
//        group = "source-plugin"
//        description = "Generate required assets for source-plugin"
//    }
//
//    @TaskAction
//    fun generateAssets() {
//        outputDir ?: return
//        val dst = File(outputDir!!, OUTPUT_ASSETS_NAME)
//        generateBytes()?.writeToFile(dst)
//        println("generate assets[${project.name}]:${dst.toRelativeString(project.rootDir)}")
//    }
//
//    private fun generateBytes(): Pair<ByteArray, ByteArray>? {
//        sourceBundles ?: return null
//        val map = mutableMapOf<String, Chunk>()
//        val fileContainer = FileContainer()
//        sourceBundles!!.forEach { (ignoreUnknownFile, ignoreEmptyDirectory, moduleName, dtoList) ->
//            dtoList.forEach { fileDto ->
//                fileDto.toChunk(fileContainer, ignoreUnknownFile, ignoreEmptyDirectory)?.let {
//                    map[fileDto.generateId(moduleName)] = it
//                }
//            }
//        }
//        val fileBytes = fileContainer.generateFileBytes()
//        map.values.forEach {
//            fileContainer.defineChunkFileIndex(it)
//        }
//        val json = Gson().toJson(AssetJson(fileContainer.fileIndices, map))
//        if (OUTPUT_JSON) {
//            project.writeAssetsJson(json)
//        }
//
//        return Pair(fileBytes, json.toByteArray())
//    }
//
//    private fun FileDto.toChunk(
//        fileContainer: FileContainer,
//        ignoreUnknownFile: Boolean,
//        ignoreEmptyDirectory: Boolean
//    ): Chunk? {
//        var chunk: Chunk? = null
//        val file = File(path)
//        when {
//            file.exists() -> {
//                when (typeId) {
//                    Type.Dir.id -> {
//                        if (file.isDirectory) {
//                            chunk = DirChunk(
//                                file, name,
//                                fileContainer, ignoreUnknownFile, ignoreEmptyDirectory
//                            )
//                        } else throw IllegalArgumentException("dir(..) must applied to directory:$this")
//                    }
//                    else -> {
//                        if (!file.isDirectory) {
//                            if (!(ignoreUnknownFile && typeId == Type.Unknown.id)) {
//                                chunk = FileChunk(name, -1, typeId, file)
//                                fileContainer.putFile(file)
//                            }
//                        } else throw IllegalArgumentException("file(..) must applied to file:$this")
//
//                    }
//                }
//            }
//            else -> throw IllegalArgumentException("can't find file for $name at ${path}.")
//        }
//        return chunk
//    }
//}