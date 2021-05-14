package com.bian.source.plugin.dto

import com.bian.source.plugin.SourceExt

class SourceBundle implements Serializable {
    Boolean ignoreUnknownFile = true
    Boolean ignoreEmptyDirectory = true
    String moduleName
    ArrayList<Mapping> mappingList

    SourceBundle(SourceExt ext) {
        ignoreUnknownFile = ext.ignoreUnknownFile
        ignoreEmptyDirectory = ext.ignoreEmptyDirectory
        moduleName = ext.moduleName
        mappingList = new ArrayList<>(ext.mappingList)
    }
}