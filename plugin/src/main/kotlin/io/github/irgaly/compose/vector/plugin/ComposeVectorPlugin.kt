package io.github.irgaly.compose.vector.plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class ComposeVectorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val logger = target.logger
        val extension = target.extensions.create<ComposeVectorExtension>("composeVector")
        val task = target.tasks.register<ComposeVectorTask>("generateImageVector") {
            group = "generate compose vector"
            this.packageName.set(extension.packageName)
            inputDir.set(extension.inputDir)
            outputDir.set(extension.outputDir)
            preClassNameTransformer.set(extension.preClassNameTransformer)
            postClassNameTransformer.set(extension.postClassNameTransformer)
            packageNameTransformer.set(extension.packageNameTransformer)
        }
        target.tasks
            .withType<KotlinCompile>()
            .configureEach {
                it.dependsOn(task)
            }
        target.executeOnFinalize {
            val multiplatformExtension =
                target.extensions.findByType<KotlinMultiplatformExtension>()
            val androidExtension = target.extensions.findByType<BaseExtension>()
            val srcDir = target.files(extension.outputDir).builtBy(task)
            val outputDirPath = extension.outputDir.get().asFile.toPath()
            val insideBuildDir =
                outputDirPath.startsWith(target.layout.buildDirectory.get().asFile.toPath())
            var generationTarget = ComposeVectorExtension.GenerationTarget.Common
            if (multiplatformExtension != null) {
                when (checkNotNull(extension.multiplatformGenerationTarget.get())) {
                    ComposeVectorExtension.GenerationTarget.Common -> {
                        generationTarget = ComposeVectorExtension.GenerationTarget.Common
                    }

                    ComposeVectorExtension.GenerationTarget.Android -> {
                        if (androidExtension == null) {
                            error("multiplatformGenerationTarget is Android, but ${target.path} project does not have Android SourceSets.")
                        }
                        generationTarget = ComposeVectorExtension.GenerationTarget.Android
                    }
                }
            } else if (androidExtension != null) {
                // Android only Project
                generationTarget = ComposeVectorExtension.GenerationTarget.Android
            }
            logger.info("generation target: $generationTarget")
            if (insideBuildDir) {
                if ((multiplatformExtension != null) &&
                    (generationTarget == ComposeVectorExtension.GenerationTarget.Common)
                ) {
                    logger.info("Register $srcDir to Common Main SourceSets")
                    multiplatformExtension.addCommonMainSourceSet(srcDir)
                }
                if ((androidExtension != null) &&
                    (generationTarget == ComposeVectorExtension.GenerationTarget.Android)
                ) {
                    logger.info("Register $srcDir to Android Main SourceSets")
                    androidExtension.addMainSourceSet(srcDir)
                }
            }
            task.configure {
                it.apply {
                    hasAndroidPreview.set(
                        (generationTarget == ComposeVectorExtension.GenerationTarget.Android) &&
                                extension.generateAndroidPreview.get()
                    )
                    hasJetbrainsPreview.set(
                        (generationTarget == ComposeVectorExtension.GenerationTarget.Common) &&
                                extension.generateJetbrainsPreview.get()
                    )
                    hasDesktopPreview.set(
                        (generationTarget == ComposeVectorExtension.GenerationTarget.Common) &&
                                extension.generateDesktopPreview.get()
                    )
                }
            }
        }
    }

    /**
     * Run Block in finalizeDsl when Android Plugin available
     * or in afterEvaluate when Android Plugin not available.
     */
    private fun Project.executeOnFinalize(block: () -> Unit) {
        var hasAndroid = false
        setOf(
            "com.android.application",
            "com.android.library",
        ).forEach { pluginId ->
            pluginManager.withPlugin(pluginId) {
                hasAndroid = true
                extensions.configure(type = AndroidComponentsExtension::class) { extension ->
                    extension.finalizeDsl {
                        block()
                    }
                }
            }
        }
        afterEvaluate {
            if (!hasAndroid) {
                block()
            }
        }
    }

    private fun KotlinMultiplatformExtension.addCommonMainSourceSet(srcDir: FileCollection) {
        sourceSets.findByName(KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME)?.kotlin?.srcDir(srcDir)
    }

    private fun BaseExtension.addMainSourceSet(srcDir: FileCollection) {
        sourceSets.findByName(SourceSet.MAIN_SOURCE_SET_NAME)?.kotlin?.srcDir(srcDir)
    }
}
