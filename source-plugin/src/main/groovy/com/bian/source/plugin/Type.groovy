package com.bian.source.plugin

enum Type {
    Text("text"),
    KotlinSource("kt"),
    JavaSource("java"),
    Xml("xml"),
    Json("json"),
    Markdown("md"),
    Html("html"),
    Dir("dir"),
    Unknown("unknown")
    private String id

    Type(String id) {
        this.id = id
    }

    String getId() {
        return id
    }

    static String getTypeIdByPath(String path) {
        def type = Unknown.id
        def find = values().find { path.endsWith(".$it.id") }
        if (find) {
            type = find.id
        }
        return type
    }
}