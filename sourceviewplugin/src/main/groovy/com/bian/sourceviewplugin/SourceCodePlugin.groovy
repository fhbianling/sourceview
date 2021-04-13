package com.bian.sourceviewplugin


import com.google.gson.Gson
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.regex.Matcher
import java.util.regex.Pattern

class SourceCodePlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        println("[$target.name]start analyze source code provide")
        Long start = System.currentTimeMillis()
        HashMap<String, HashMap<String, String>> map = new HashMap<>()
        Pattern p = Pattern.compile("ProvideSourceCode\\(.*\\)")
        Pattern packagePattern = Pattern.compile("package .*")
        target.fileTree('src/main').forEach { f ->
            if (f.absolutePath.endsWith('.kt')) {
                String s = new String(f.readBytes())
                println("[$target.name]i'm reading bytes")
                Matcher m = p.matcher(s)
                if (m.find()) {
                    String packagePath = s.find(packagePattern)
                    packagePath = packagePath.substring(packagePath.indexOf("package") + 7, packagePath.length()).trim()
                    String key = m.group(0)
                    key = key.substring(key.indexOf('(') + 2, key.length() - 2)
                    println("[$target.name]find source code provided:${f.path}")
                    if (map[packagePath] == null) {
                        map[packagePath] = new HashMap<String, String>()
                    }
                    map[packagePath].put(key, s)
                }
            }
        }
        if (map.isEmpty()) {
            Long cost = System.currentTimeMillis() - start
            println("[$target.name] found nothing for provide source code($cost ms)")
            return
        }
        String json = new Gson().toJson(map)
        String dirPath = "src/main/assets/SourceCode"
        File dir = target.file(dirPath)
        if (!dir.exists()) {
            target.mkdir(dir)
        }
        File dst = target.file("$dirPath/${target.name}_generated.json")
        if (!dst.exists()) {
            dst.createNewFile()
        } else {
            dst.delete()
        }
        dst.write(json)
        Long cost = System.currentTimeMillis() - start
        println("[$target.name]generated SourceCode at:${dst.absolutePath}($cost ms)")
    }
}