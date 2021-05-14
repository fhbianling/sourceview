package com.bian.source.plugin.dto

class Mapping implements Serializable {
    String path
    String name
    String typeId

    Mapping(String path, String name, String typeId) {
        this.name = name
        this.path = path
        this.typeId = typeId
    }
}