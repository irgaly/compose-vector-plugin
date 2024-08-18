package io.github.irgaly.compose.vector.plugin

import org.gradle.api.Transformer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import java.io.File

abstract class ComposeVectorExtension(
    projectLayout: ProjectLayout
) {
    /**
     * Image classes package
     *
     * Example: com.example.your.app.images
     */
    abstract val packageName: Property<String>

    /**
     * Vector files directory
     *
     * Optional
     *
     * Default: {project directory}/images
     */
    abstract val inputDir: DirectoryProperty

    /**
     * Generated Kotlin Sources directory.
     * outputDir is registered to SourceSet when outputDir is inside of project's buildDirectory.
     *
     * Optional
     *
     * Default: {build directory}/compose-vector/src/main/kotlin
     */
    abstract val outputDir: DirectoryProperty

    /**
     * Custom Class Name pre conversion logic to image class names and image receiver class names.
     *
     * Optional
     *
     * For example, assume that source svg file is "my_icon.svg".
     *
     * * Apply custom preClassNameTransformer
     *     * Pair<File, String>
     *         * File: "my_icon.svg" file instance
     *         * String: "my_icon"
     *     * for example: returns "pre_custom_my_icon"
     * * Apply default transformer
     *     * "pre_custom_my_icon" -> "PreCustomMyIcon"
     * * Apply custom postClassNameTransformer
     *     * Pair<File, String>
     *         * File: "my_icon.svg" file instance
     *         * String: "PreCustomMyIcon"
     *     * For example: returns "PreCustomMyIconPostCustom"
     *
     * This is result to "PreCustomMyIconPostCustom" image class name.
     */
    abstract val preClassNameTransformer: Property<Transformer<String, Pair<File, String>>>

    /**
     * Custom Class Name post conversion logic to image class names and image receiver class names.
     *
     * Optional
     *
     * For example, assume that source svg file is "my_icon.svg".
     *
     * * Apply custom preClassNameTransformer
     *     * Pair<File, String>
     *         * File: "my_icon.svg" file instance
     *         * String: "my_icon"
     *     * For example: returns "pre_custom_my_icon"
     * * Apply default transformer
     *     * "pre_custom_my_icon" -> "PreCustomMyIcon"
     * * Apply custom postClassNameTransformer
     *     * Pair<File, String>
     *         * File: "my_icon.svg" file instance
     *         * String: "PreCustomMyIcon"
     *     * For example: returns "PreCustomMyIconPostCustom"
     *
     * This is result to "PreCustomMyIconPostCustom" image class name.
     */
    abstract val postClassNameTransformer: Property<Transformer<String, Pair<File, String>>>

    /**
     * Custom Package Name conversion logic.
     *
     * Optional
     *
     * * Pair<File, String>
     *     * File: target directory instance
     *     * String: target directory basename
     */
    abstract val packageNameTransformer: Property<Transformer<String, Pair<File, String>>>

    /**
     * Target SourceSets that generated images belongs to for KMP project.
     * This option is not affect to only KMP Project, not to Android only Project.
     */
    @get:Input
    abstract val multiplatformGenerationTarget: Property<GenerationTarget>

    /**
     * Generate androidx.compose.ui.tooling.preview.Preview functions for Android target or not
     *
     * Default: true
     */
    @get:Input
    abstract val generateAndroidPreview: Property<Boolean>

    /**
     * Generate org.jetbrains.compose.ui.tooling.preview.Preview functions for KMP common target or not
     *
     * Default: false
     */
    @get:Input
    abstract val generateJetbrainsPreview: Property<Boolean>

    /**
     * Generate androidx.compose.desktop.ui.tooling.preview.Preview functions for KMP common target or not
     *
     * Default: true
     */
    @get:Input
    abstract val generateDesktopPreview: Property<Boolean>

    init {
        inputDir.convention(
            projectLayout.projectDirectory.dir("images")
        )
        outputDir.convention(
            projectLayout.buildDirectory.dir("compose-vector/src/main/kotlin")
        )
        multiplatformGenerationTarget.convention(GenerationTarget.Common)
        generateAndroidPreview.convention(true)
        generateJetbrainsPreview.convention(false)
        generateDesktopPreview.convention(true)
    }

    /**
     * Target SourceSets that generated images belongs to.
     */
    enum class GenerationTarget {
        /**
         * commonMain target
         */
        Common,

        /**
         * androidMain target
         */
        Android
    }
}
