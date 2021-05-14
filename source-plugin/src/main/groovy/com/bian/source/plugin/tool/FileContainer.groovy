package com.bian.source.plugin.tool

import com.bian.source.plugin.dto.Chunk
import com.bian.source.plugin.dto.DirChunk
import com.bian.source.plugin.dto.FileChunk

class FileContainer {
    Map<File, FileIndex> fileIndexMap = new LinkedHashMap<>()

    void putFile(File file) {
        if (!fileIndexMap.containsKey(file)) {
            fileIndexMap[file] = new FileIndex(fileIndexMap.size(), 0, (int) file.length())
        }
    }

    void defineChunkFileIndex(Chunk chunk) {
        if (chunk instanceof DirChunk) {
            chunk.children.each {
                if (it) {
                    defineChunkFileIndex(it)
                }
            }
        } else if (chunk instanceof FileChunk) {
            FileIndex fileIndex = fileIndexMap[chunk.file]
            chunk.index = fileIndex ? fileIndex.index : -1
        }
    }

    byte[] generateFileBytes() {
        def length = 0
        fileIndexMap.keySet().each {
            length += BytesWriter.getPaddedLength(it.length())
        }
        byte[] bytes = new byte[length]
        def byteOffset = 0
        fileIndexMap.eachWithIndex { Map.Entry<File, FileIndex> entry, int i ->
            def file = entry.key
            entry.value.byteOffset = byteOffset
            System.arraycopy(file.bytes, 0, bytes, byteOffset, (int) file.length())
            byteOffset += BytesWriter.getPaddedLength(file.length())
        }
        return bytes
    }

    static class FileIndex {
        transient int index
        int byteOffset
        int byteLength

        FileIndex(int index, int byteOffset, int byteLength) {
            this.index = index
            this.byteOffset = byteOffset
            this.byteLength = byteLength
        }
    }
}
