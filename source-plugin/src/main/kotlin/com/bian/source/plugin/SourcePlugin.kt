//package com.bian.source.plugin
//
//import com.android.build.gradle.AppExtension
//import com.android.build.gradle.BaseExtension
//import com.android.build.gradle.LibraryExtension
//import com.android.build.gradle.api.BaseVariant
//import com.android.ide.common.xml.AndroidManifestParser
//import com.android.io.FileWrapper
//import com.bian.source.plugin.BytesWriter.safeWriteText
//import com.bian.source.plugin.dto.FileDto
//import com.bian.source.plugin.dto.SourceBundle
//import com.bian.source.plugin.extension.SourceExtension
//import com.bian.source.plugin.task.GenerateAssets
//import com.bian.source.plugin.task.GenerateBinding
//import org.gradle.api.DomainObjectSet
//import org.gradle.api.Plugin
//import org.gradle.api.Project
//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
//import java.io.File
//import java.math.BigInteger
//import java.security.MessageDigest
//import java.util.*
//import kotlin.collections.ArrayList

/**
 * author fhbianling@163.com
 * date 2021/5/13 14:43
 * 类描述：
 */
//class SourcePlugin : Plugin<Project> {
//    override fun apply(target: Project) {
//        val baseExtension = target.extensions.findByType(BaseExtension::class.java) ?: return
//        val sourceExt = target.extensions.create("sourceExt", SourceExtension::class.java)
//        sourceExt.moduleName = target.name
//        sourceExt.projectDir = target.projectDir.absolutePath
//        target.afterEvaluate {
//            if (baseExtension is AppExtension) {
//                registerGenerateAssets(it, baseExtension)
//            }
//            if (sourceExt.enableBinding)
//                registerGenerateBinding(target, baseExtension)
//        }
//    }

//    private fun registerGenerateAssets(target: Project, appExtension: AppExtension) {
//        appExtension.sourceSets.findByName("main")?.assets?.srcDir(target.outputAssetsDir())
//        val task = target.tasks.register("generateAssets", GenerateAssets::class.java) {
//            val sourceBundles = ArrayList<SourceBundle>()
//            target.rootProject.subprojects { subProject ->
//                subProject.extensions.findByType(SourceExtension::class.java)
//                    ?.let { sourceExtension ->
//                        sourceBundles.add(sourceExtension.toBundle())
//                    }
//            }
//            it.outputDir = File(target.outputAssetsDir())
//            it.sourceBundles = sourceBundles
//        }
//        appExtension.applicationVariants.all { variant ->
//            variant.mergeAssetsProvider?.get()?.dependsOn(task)
//        }
//    }

//    private fun registerGenerateBinding(target: Project, baseExtension: BaseExtension) {
//        val variants = baseExtension.getVariants() ?: return
//        variants.all { variant ->
//            val outputBindingClassDir = target.outputBindingClassDir()
//            val variantGeneratedSourceSetPath = "$outputBindingClassDir\\${variant.name}"
//            baseExtension.sourceSets.findByName(variant.name)?.java?.srcDir(
//                variantGeneratedSourceSetPath
//            )
//            val packageName = variant.getPackageName()
//            val javaOutputDir = target.file(variantGeneratedSourceSetPath)
//            val capitalizeVariantName = variant.name.compatCapitalize()
//            val taskProvider = target.tasks.register(
//                "generateBinding$capitalizeVariantName",
//                GenerateBinding::class.java
//            ) {
//                it.packageName = packageName
//                it.outputDir = javaOutputDir
//                it.dtoList =
//                    ArrayList(target.extensions.findByType(SourceExtension::class.java)?.dtoList)
//            }
//            variant.registerJavaGeneratingTask(taskProvider.get(), javaOutputDir)
//            val compileKotlin =
//                target.tasks.findByName("compile${capitalizeVariantName}Kotlin") ?: return@all
//            if (compileKotlin is KotlinCompile) {
//                compileKotlin.dependsOn(taskProvider)
//                val srcSet = target.objects.sourceDirectorySet("source-plugin", "source-plugin")
//                    .srcDir(variantGeneratedSourceSetPath)
//                compileKotlin.source(srcSet)
//            }
//        }
//    }

//    companion object {
//        const val OUTPUT_JSON = true
//        internal fun Project.outputAssetsDir(): String {
//            return "${buildDir.absolutePath}\\generated\\assets\\sourceView"
//        }
//
//        internal fun Project.writeAssetsJson(json: String) {
//            val path = "${buildDir.absolutePath}\\outputs\\sourceView\\assets.json"
//            val dst = File(path)
//            dst.safeWriteText(json)
//            println("generate json[${name}]:${dst.toRelativeString(rootDir)}")
//        }
//
//        internal fun Project.outputBindingClassDir(): String {
//            return "${buildDir.absolutePath}\\generated\\source\\sourceView"
//        }
//
//        internal fun SourceExtension.toBundle(): SourceBundle {
//            return SourceBundle(
//                ignoreUnknownFile,
//                ignoreEmptyDirectory,
//                moduleName,
//                ArrayList(dtoList)
//            )
//        }
//
//        internal fun BaseExtension.getVariants(): DomainObjectSet<out BaseVariant>? {
//            if (this is AppExtension)
//                return applicationVariants
//            if (this is LibraryExtension)
//                return libraryVariants
//            return null
//        }
//
//        internal fun Number.paddedLength(): Int {
//            val number = this.toInt()
//            if (number < 4) return 4
//            return (number - 1 + (4 - (number - 1) % 4))
//        }
//
//        private fun getMD5ForString(string: String): String {
//            val md5 = MessageDigest.getInstance("MD5")
//            md5.update(string.toByteArray())
//            return BigInteger(md5.digest()).toString(16)
//        }
//
//        private fun BaseVariant.getPackageName(): String? {
//            val find = sourceSets.find { it.manifestFile.exists() } ?: return null
//            val manifest = AndroidManifestParser.parse(FileWrapper(find.manifestFile))
//            return manifest.`package`
//        }
//
//        private fun String.compatCapitalize(): String {
//            return capitalize(Locale.getDefault())
//        }
//
//        internal fun FileDto.generateId(moduleName: String): String {
//            return getMD5ForString("$moduleName$${name}")
//        }
//    }
//}