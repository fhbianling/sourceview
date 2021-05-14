//package com.bian.source.plugin.task
//
//import com.bian.source.plugin.dto.FileDto
//import org.gradle.api.DefaultTask
//import org.gradle.api.tasks.Input
//import org.gradle.api.tasks.OutputDirectory
//import org.gradle.api.tasks.TaskAction
//import java.io.File
//
///**
// * author fhbianling@163.com
// * date 2021/5/14 9:41
// * 类描述：
// */
//const val OUTPUT_CLASS_NAME = "SourceBinding"
//
//class GenerateBinding : DefaultTask() {
//    init {
//        group = "source-plugin"
//        description = "Generate extra binding class for source-plugin"
//    }
//
//    // 标记输出目录，以避免每次执行build.gradle都进行文件生成
//    @OutputDirectory
//    var outputDir: File? = null
//
//    // 标记输入参数，当输入参数发生变化时，重新进行文件生成
//    // 输入参数必须继承Serializable，可序列化的
//    @Input
//    var packageName: String? = null
//
//    @Input
//    var dtoList: ArrayList<FileDto>? = null
//
//    @TaskAction
//    fun generateBinding() {
//
//    }
//}