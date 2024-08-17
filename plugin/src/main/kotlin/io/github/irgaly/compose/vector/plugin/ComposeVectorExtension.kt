package io.github.irgaly.compose.vector.plugin

import org.gradle.api.Transformer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.Property
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
     * Generated Kotlin Sources directory
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

    init {
        inputDir.convention(
            projectLayout.projectDirectory.dir("images")
        )
        outputDir.convention(
            projectLayout.buildDirectory.dir("compose-vector/src/main/kotlin")
        )
    }
}
