package com.bian.source.plugin


import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.BaseVariant
import com.android.ide.common.xml.AndroidManifestParser
import com.bian.source.plugin.dto.SourceBundle
import com.bian.source.plugin.task.GenerateAssets
import com.bian.source.plugin.task.GenerateBinding
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class SourcePlugin implements Plugin<Project> {

    private BaseExtension baseExt
    private SourceExt sourceView

    @Override
    void apply(Project target) {
        sourceView = target.extensions.create("sourceView", SourceExt)
        sourceView.moduleName = target.name
        sourceView.projectDir = target.projectDir
        target.afterEvaluate {
            baseExt = target.extensions.findByType(BaseExtension)
            if (!baseExt) return
            registerAssetsGenerateTask(target)
            if (!sourceView.enableBinding) return
            registerBindingClassGenerateTask(target)
        }
    }

    private void registerAssetsGenerateTask(Project target) {
        if (!(baseExt instanceof AppExtension)) {
            println("assets only generated in com.android.application")
            return
        }
        def assetsOutputDir = outputAssetsDir(target)
        baseExt.sourceSets.main.assets.srcDir(assetsOutputDir)
        def task = target.tasks.register("generateAssets", GenerateAssets) { GenerateAssets generateAssets ->
            try {
                def sourceExtensions = new ArrayList<SourceBundle>()
                target.rootProject.subprojects {
                    def sourceExt = it.extensions.findByType(SourceExt)
                    if (sourceExt) {
                        sourceExtensions.add(new SourceBundle(sourceExt))
                    }
                }
                generateAssets.sourceBundles = sourceExtensions
                generateAssets.outputDir = new File(assetsOutputDir)
                println("generated asset:$generateAssets.outputDir")
            } catch (Exception e) {
                println(e.message)
            }
        }
        getVariants(baseExt).all { variant ->
            (variant as BaseVariant).mergeAssetsProvider.get().dependsOn(task)
        }
    }

    private void registerBindingClassGenerateTask(Project target) {
        def variants = getVariants(baseExt)
        if (!variants) return
        variants.all { variant ->
            def variantGeneratedSourceSetPath = "${outputClassDir(target)}\\${variant.name}"
            baseExt.sourceSets.findByName(variant.name)?.java?.srcDir(variantGeneratedSourceSetPath)
            def packageName = getPackageName(variant)
            def javaOutputDir = target.file(variantGeneratedSourceSetPath)
            def capitalizeVariantName = variant.name.capitalize()
            def taskProvider = target.tasks
                    .register("generateBinding$capitalizeVariantName", GenerateBinding) {
                        it.mappingList = target.extensions.findByType(SourceExt)?.mappingList
                        it.outputDir = javaOutputDir
                        it.packageName = packageName
                    }
            variant.registerJavaGeneratingTask(taskProvider.get(), javaOutputDir)
            def compileKotlin = target.tasks.findByName("compile${capitalizeVariantName}Kotlin")
            if (compileKotlin && compileKotlin instanceof KotlinCompile) {
                compileKotlin.dependsOn(taskProvider)
                def srcSet = target.objects.sourceDirectorySet("sourceView", "sourceView")
                        .srcDir(variantGeneratedSourceSetPath)
                compileKotlin.source(srcSet)
            }
        }
    }

    static DomainObjectSet<BaseVariant> getVariants(BaseExtension extension) {
        if (extension instanceof AppExtension) {
            return extension.applicationVariants
        }
        if (extension instanceof LibraryExtension) {
            return extension.libraryVariants
        }
        return null
    }


    // 在gradle groovy script中暂时不支持groovy扩展方法，如果以后支持了，下面这些静态方法可以更换为扩展方法，可读性更高
    static String getPackageName(BaseVariant variant) {
        def find = variant.sourceSets.find { it.manifestFile.exists() }
        def manifest = AndroidManifestParser.parse(find.manifestFile.toPath())
        return manifest.package
    }

    static String outputAssetsDir(Project project) {
        return "$project.buildDir\\generated\\assets\\sourceView"
    }

    static String outputClassDir(Project project) {
        return "$project.buildDir\\generated\\source\\sourceView"
    }
}