package com.bian.source.plugin.tool

import com.bian.source.plugin.Type
import com.bian.source.plugin.dto.AssetJson
import com.bian.source.plugin.dto.Chunk
import com.bian.source.plugin.dto.DirChunk
import com.bian.source.plugin.dto.FileChunk
import com.bian.source.plugin.dto.Mapping
import com.bian.source.plugin.dto.SourceBundle
import com.google.gson.Gson
import org.gradle.api.Project

import java.security.MessageDigest

class AssetsGenerator {
    private FileContainer fileContainer = new FileContainer()
    private final static boolean OUTPUT_JSON = true

    byte[] generate(ArrayList<SourceBundle> sourceBundles, Project project) {
        def map = new LinkedHashMap<String, Chunk>()
        sourceBundles.each { SourceBundle bundle ->
            bundle.mappingList.each { mapping ->
                Chunk chunk = getChunk(mapping, fileContainer,
                        bundle.ignoreUnknownFile,
                        bundle.ignoreEmptyDirectory, project)
                if (chunk != null) {
                    map[getMappingId(mapping.name, bundle.moduleName)] = chunk
                }
            }
        }
        byte[] fileBytes = fileContainer.generateFileBytes()
        map.values().each { Chunk chunk ->
            fileContainer.defineChunkFileIndex(chunk)
        }
        def fileMap = fileContainer.fileIndexMap.values()
        def json = new Gson().toJson(new AssetJson(fileMap, map))
        byte[] jsonBytes = json.bytes
        if (OUTPUT_JSON) {
            def dstJsonFile = new File(getJsonFilePath(project))
            BytesWriter.writeBytesToFile(dstJsonFile, jsonBytes)
            println("generate json[$project.name]:${project.rootDir.relativePath(dstJsonFile)}")
        }
        return BytesWriter.assembleAssetBytes(fileBytes, jsonBytes)
    }

    static Chunk getChunk(Mapping mapping, FileContainer fileContainer,
                          boolean ignoreUnknown, boolean ignoreEmpty, Project project) {
        Chunk chunk = null
        def file = new File(mapping.path)
        if (file.exists()) {
            if (mapping.typeId == Type.Dir.id) {
                if (file.isDirectory()) {
                    chunk = new DirChunk(mapping.name, file,
                            fileContainer, ignoreUnknown, ignoreEmpty, project.rootDir)
                } else {
                    throw new IllegalArgumentException("dir(..) must applied to directory:$mapping")
                }
            } else {
                if (file.isDirectory()) {
                    throw new IllegalArgumentException("src(..) must applied to file:$mapping")
                } else {
                    if (!(ignoreUnknown && mapping.typeId == Type.Unknown.id)) {
                        chunk = new FileChunk(mapping.name, mapping.typeId, file, project.rootDir)
                        fileContainer.putFile(file)
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("can't find file for ${mapping.name} at ${mapping.path}.")
        }
        chunk
    }

    private static String getJsonFilePath(Project project) {
        return "$project.buildDir.absolutePath${File.separator}sourceView${File.separator}assets.json"
    }

    static String getMappingId(String mappingName, String moduleName) {
        return getMD5ForString("$moduleName\$$mappingName")
    }

    private static String getMD5ForString(String string) {
        def md5 = MessageDigest.getInstance("MD5")
        md5.update(string.bytes)
        def bytes = md5.digest()
        def i = 0
        def buf = new StringBuffer("")
        for (j in 0..<bytes.length) {
            i = bytes[j]
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
}