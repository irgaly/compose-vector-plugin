package io.github.irgaly.compose.vector

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

abstract class ComposeVectorTask: DefaultTask() {
    @get:Incremental
    @get:InputDirectory
    abstract val inputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val packageName: Property<String>

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        val baseDir = inputDir.get()
        inputChanges.getFileChanges(inputDir)
            .filter {
                (it.fileType != FileType.DIRECTORY)
            }.forEach { change ->
                logger.debug("changed: $change")
        }
    }
}
