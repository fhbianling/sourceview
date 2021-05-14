package com.bian.source.plugin.dto

import com.bian.source.plugin.tool.FileContainer

class AssetJson {
    Collection<FileContainer.FileIndex> file
    Map<String, Chunk> entry

    AssetJson(Collection<FileContainer.FileIndex> file, Map<String, Chunk> entry) {
        this.file = file
        this.entry = entry
    }
}