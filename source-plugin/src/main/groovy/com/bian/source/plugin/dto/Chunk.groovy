package com.bian.source.plugin.dto

import com.bian.source.plugin.Type
import com.bian.source.plugin.tool.FileContainer

class Chunk implements Serializable {
    transient File file
    String name

    Chunk() {
    }
}

class FileChunk extends Chunk {
    int index
    String type

    FileChunk(String name, String type, File file) {
        this.type = type
        this.name = name
        this.file = file
    }
}

class DirChunk extends Chunk {
    Chunk[] children
    private transient boolean hasFile

    DirChunk(String name, File file, FileContainer fileContainer,
             boolean ignoreUnknown, boolean ignoreEmpty) {
        this.name = name
        this.file = file
        def childrenFiles = file.listFiles()
        List<Chunk> list = new ArrayList()
        if (childrenFiles) {
            childrenFiles.eachWithIndex { f, i ->
                if (f.isDirectory()) {
                    def chunk = new DirChunk(f.name, f, fileContainer, ignoreUnknown, ignoreEmpty)
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
                        list.add(new FileChunk(f.name, typeId, f))
                        fileContainer.putFile(f)
                    }
                }
            }
        }
        children = list.toArray(new Chunk[0])
    }
}