package com.bian.source.plugin

import com.bian.source.plugin.dto.Mapping

class SourceExt {
    Boolean enableBinding = true
    Boolean ignoreUnknownFile = true
    Boolean ignoreEmptyDirectory = true
    String moduleName
    String projectDir
    ArrayList<Mapping> mappingList = new ArrayList<>()

    void dir(Map map = [:]) {
        putFile(map["path"], map["name"], Type.Dir.id, map["absolute"], true)
    }

    void dir(String path) {
        putFile(path, null, Type.Dir.id, false, true)
    }

    void src(Map map = [:]) {
        putFile(map["path"], map["name"], map["type"], map["absolute"], false)
    }

    void src(String path) {
        putFile(path, null, null, false, false)
    }

    private void putFile(String path, String name, String type, Boolean absolute, Boolean putDir) {
        if (!putDir && type == Type.Dir.id) {
            throw new IllegalArgumentException("type dir doesn't work in file(..)")
        }
        if (isEmpty(path)) {
            throw new IllegalArgumentException("must specify path,path:$path,name:$name,type:$type,absolute:$absolute")
        }
        if (absolute == null) {
            absolute = false
        }
        def realPath = getRealPath(path, absolute)
        if (isEmpty(type)) {
            type = Type.getTypeIdByPath(path)
        }
        if (isEmpty(name)) {
            name = getDefaultName(realPath)
        }
        mappingList.add(new Mapping(realPath, name, type))
    }

    private static String getDefaultName(String path) {
        def file = new File(path)
        return file.name
    }

    private String getRealPath(String path, boolean absolute) {
        return absolute ? path : (projectDir + File.separator + path)
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty()
    }
}