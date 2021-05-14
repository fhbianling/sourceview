package com.bian.source.plugin.task


import com.bian.source.plugin.dto.SourceBundle
import com.bian.source.plugin.tool.AssetsGenerator
import com.bian.source.plugin.tool.BytesWriter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class GenerateAssets extends DefaultTask {
    static def OUTPUT_ASSETS_NAME = "generated.srcv"

    @OutputDirectory
    File outputDir

    @Input
    ArrayList<SourceBundle> sourceBundles

    GenerateAssets() {
        group = "source-plugin"
        description = "Generate required assets for source-plugin"
    }

    @TaskAction
    void generateAssets() {
        try {
            def assetsGenerator = new AssetsGenerator()
            byte[] dataBytes = assetsGenerator.generate(sourceBundles, project)
            def outputAssets = new File(outputDir, OUTPUT_ASSETS_NAME)
            BytesWriter.writeBytesToFile(outputAssets, dataBytes)
            println("generate assets[${project.name}]:${project.rootDir.relativePath(outputAssets)}")
        } catch (Exception e) {
            println(e.message)
        }
    }
}