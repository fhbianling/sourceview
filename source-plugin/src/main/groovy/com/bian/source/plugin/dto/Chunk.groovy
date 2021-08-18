package com.bian.source.plugin.dto

import com.bian.source.plugin.Type
import com.bian.source.plugin.tool.FileContainer

class Chunk implements Serializable {
    transient File file
    String name
    String filePath

    Chunk() {
    }
}

class FileChunk extends Chunk implements Serializable {
    int index
    String type

    FileChunk(String name, String type, File file, File root) {
        this.type = type
        this.name = name
        this.file = file
        this.filePath = root.relativePath(file)
    }
}

class DirChunk extends Chunk {
    Chunk[] children
    private transient boolean hasFile

    DirChunk(String name, File file, FileContainer fileContainer,
             boolean ignoreUnknown, boolean ignoreEmpty, File root) {
        this.name = name
        this.file = file
        this.filePath = root.relativePath(file)
        def childrenFiles = file.listFiles()
        List<Chunk> list = new ArrayList()
        if (childrenFiles) {
            childrenFiles.eachWithIndex { f, i ->
                if (f.isDirectory()) {
                    def chunk = new DirChunk(f.name, f, fileContainer, ignoreUnknown, ignoreEmpty, root)
                    if (chunk.hasFile) {
                        hasFile = true
                    }
                    if (!ignoreEmpty || chunk.hasFile) {
                        list.add(chunk)
                    }
                } else {
                    def typeId = Type.getTypeIdByPath(f.absolutePath)
                    if (!(ignoreUnknown && typeId == Type.Unknown.id)) {
                        hasFile = true
                        list.add(new FileChunk(f.name, typeId, f, root))
                        fileContainer.putFile(f)
                    }
                }
            }
        }
        children = list.toArray(new Chunk[0])
    }
}