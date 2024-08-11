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
        val packageName = "io.github.irgaly.compose.vector.sample.icons"
        val buildDirectory = target.layout.buildDirectory.dir("compose-vector")
        val generatedSourceDirectory = buildDirectory.map { it.dir("src/main/kotlin") }
        val task = target.tasks.register<ComposeVectorTask>("generateImageVector") {
            group = "generate compose vector"
            inputDir.set(target.layout.projectDirectory.dir("images"))
            outputDir.set(generatedSourceDirectory)
            this.packageName.set(packageName)
        }
        target.tasks
            .withType<KotlinCompile>()
            .configureEach {
                it.dependsOn(task)
            }
        val srcDir = target.files(generatedSourceDirectory).builtBy(task)
        if (target.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
            val sourceSet = target.extensions.findByType<KotlinMultiplatformExtension>()?.sourceSets?.findByName(
                KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME
            )
            sourceSet?.kotlin?.srcDir(srcDir)
        } else {
            val sourceSet = target.extensions.findByType<BaseExtension>()?.sourceSets?.findByName(
                SourceSet.MAIN_SOURCE_SET_NAME
            )
            sourceSet?.kotlin?.srcDir(srcDir)
        }
    }
}
