package io.github.irgaly.compose.vector

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
import java.io.File

class ComposeVectorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create<ComposeVectorExtension>("composeVector")
        val srcDir: File? = if (target.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
            val sourceSet = target.extensions.findByType<KotlinMultiplatformExtension>()?.sourceSets?.findByName(
                KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME
            )
            sourceSet?.kotlin?.srcDirs?.firstOrNull()
        } else {
            val sourceSet = target.extensions.findByType<BaseExtension>()?.sourceSets?.findByName(
                SourceSet.MAIN_SOURCE_SET_NAME
            )
            @Suppress("DEPRECATION")
            val srcDirs = (sourceSet?.kotlin as? com.android.build.gradle.api.AndroidSourceDirectorySet)?.srcDirs
            srcDirs?.firstOrNull {
                (it.name == "kotlin")
            }
        }
        if (srcDir == null) {
            error("Cannot get source directory.")
        }
        val task = target.tasks.register<ComposeVectorTask>("generateImageVector") {
            group = "generate compose vector"
            inputDir.set(target.layout.projectDirectory.dir("images"))
            outputDir.set(srcDir.resolve("io/github/irgaly/compose/vector/sample/icons"))
            packageName.set("package_name")
        }
        target.tasks
            .withType<KotlinCompile>()
            .configureEach {
                it.dependsOn(task)
            }
    }
}
