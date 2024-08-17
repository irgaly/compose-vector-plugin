package io.github.irgaly.compose.vector.plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class ComposeVectorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
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
        target.afterEvaluate {
            val srcDir = target.files(extension.outputDir).builtBy(this)
            target.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
                val sourceSet =
                    target.extensions.findByType<KotlinMultiplatformExtension>()?.sourceSets?.findByName(
                        KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME
                    )
                sourceSet?.kotlin?.srcDir(srcDir)
            }
            listOf("com.android.application", "com.android.library").forEach { pluginId ->
                target.pluginManager.withPlugin(pluginId) {
                    val sourceSet =
                        target.extensions.findByType<BaseExtension>()?.sourceSets?.findByName(
                            SourceSet.MAIN_SOURCE_SET_NAME
                        )
                    sourceSet?.kotlin?.srcDir(srcDir)
                }
            }
        }
    }
}
