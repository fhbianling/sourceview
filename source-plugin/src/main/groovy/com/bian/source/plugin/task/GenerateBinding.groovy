package com.bian.source.plugin.task

import com.bian.source.plugin.dto.Mapping
import com.bian.source.plugin.tool.AssetsGenerator
import com.squareup.javawriter.JavaWriter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import javax.lang.model.element.Modifier

class GenerateBinding extends DefaultTask {
    static def OUTPUT_CLASS_NAME = "SourceBinding"

    // 标记输出目录，以避免每次执行build.gradle都进行文件生成
    @OutputDirectory
    File outputDir

    // 标记输入参数，当输入参数发生变化时，重新进行文件生成
    // 输入参数必须继承Serializable，可序列化的
    @Input
    String packageName

    @Input
    ArrayList<Mapping> mappingList

    GenerateBinding() {
        group = "source-plugin"
        description = "Generate extra binding class for source-plugin"
    }

    @TaskAction
    void generateBindingClass() {
        def dstFile = new File(outputDir, "${packageName.replace(".", File.separator)}\\${OUTPUT_CLASS_NAME}.java")
        println("generate class[${project.name}]: ${project.rootDir.relativePath(dstFile)}")
        if (!dstFile.parentFile.exists()) {
            dstFile.parentFile.mkdirs()
        }
        if (!dstFile.exists()) {
            dstFile.createNewFile()
        }
        try {
            def writerStream = dstFile.newWriter()
            def writer = new JavaWriter(writerStream)
            writer.emitPackage(packageName)
            writer.emitImports("com.bian.source.core.SourceIndex")
            writer.beginType(OUTPUT_CLASS_NAME, "class", [Modifier.FINAL, Modifier.PUBLIC] as Set)
            def modifiers = [Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC] as Set
            mappingList.each {
                def propertyName = it.name.uncapitalize()
                def propertyValue = "new SourceIndex(\"$it.name\",\"${AssetsGenerator.getMappingId(it.name, project.name)}\")"
                writer.emitField("SourceIndex", propertyName, modifiers, propertyValue)
            }
            writer.endType().close()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}