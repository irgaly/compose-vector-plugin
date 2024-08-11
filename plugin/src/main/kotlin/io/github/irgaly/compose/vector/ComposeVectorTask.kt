package io.github.irgaly.compose.vector

import io.github.irgaly.compose.Logger
import io.github.irgaly.compose.vector.svg.SvgParser
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.pathString

@CacheableTask
abstract class ComposeVectorTask: DefaultTask() {
    @get:Incremental
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputDirectory
    abstract val inputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val packageName: Property<String>

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        val outputBaseDirectory = outputDir.get()
        val packageName = packageName.get()
        val packageDirectory = outputBaseDirectory.dir(packageName.replace(".", "/"))
        val parser = SvgParser(getParserLogger())
        val generator = ImageVectorGenerator()
        val buildDirectory = project.layout.buildDirectory.get()
        if (outputBaseDirectory.asFile.startsWith(buildDirectory.asFile)) {
            // outputDir is under build directory
            logger.info("clean $outputBaseDirectory because of initial build or full rebuild for incremental task and there in under project build directory.")
            outputBaseDirectory.asFile.deleteRecursively()
        }
        inputChanges.getFileChanges(inputDir)
            .filter {
                (it.fileType != FileType.DIRECTORY)
            }.filter {
                it.file.extension.equals("svg", ignoreCase = true)
            }.forEach { change ->
                logger.info("changed: $change")
                val svgFile = change.file
                val outputDirectoryRelativePath = Path.of(change.normalizedPath).parent
                val outputDirectory = packageDirectory.dir(outputDirectoryRelativePath.pathString)
                val destinationPropertyName = svgFile.nameWithoutExtension.toKotlinName()
                val destinationClassNames = outputDirectoryRelativePath.segments().map {
                    it.toKotlinClassName()
                }.toList()
                val extensionPackage = listOf(
                    packageName,
                    *outputDirectoryRelativePath.segments().toList().toTypedArray()
                ).joinToString(".")
                val outputFile = outputDirectory.file("${destinationPropertyName}.kt")
                when (change.changeType) {
                    ChangeType.ADDED,
                    ChangeType.MODIFIED,
                    -> {
                        logger.info("convert ${change.normalizedPath} to ${outputDirectoryRelativePath}/${outputFile.asFile.name}")
                        outputDirectory.asFile.mkdirs()
                        try {
                            val imageVector = change.file.inputStream().use { stream ->
                                parser.parse(
                                    input = stream,
                                    name = destinationPropertyName,
                                    autoMirror = destinationClassNames.contains("AutoMirrored")
                                )
                            }
                            val kotlinSource = generator.generate(
                                imageVector = imageVector,
                                destinationClassPackage = packageName,
                                destinationClassNames = destinationClassNames,
                                extensionPackage = extensionPackage,
                            )
                            outputFile.asFile.writeText(kotlinSource)
                        } catch (error: Exception) {
                            logger.error("SVG Parser Error: $svgFile", error)
                        }
                    }

                    ChangeType.REMOVED -> {
                        logger.info("delete ${outputDirectoryRelativePath}/${outputFile.asFile.name}")
                        // delete target kotlin file
                        outputFile.asFile.delete()
                        // try to delete parent directory if empty
                        outputDirectory.asFile.delete()
                    }
                }
        }
        inputDir.get().asFile.listFiles(File::isDirectory)?.forEach { rootDirectory ->
            fun File.toObjectClass(): ImageVectorGenerator.ObjectClass {
                return ImageVectorGenerator.ObjectClass(
                    name = name.toKotlinClassName(),
                    children = listFiles(File::isDirectory)?.sorted()?.map {
                        it.toObjectClass()
                    } ?: emptyList()
                )
            }

            val objectClass = rootDirectory.toObjectClass()
            val objectFileName = "${objectClass.name}.kt"
            logger.info("write object file: $objectFileName")
            packageDirectory.file(objectFileName).asFile.writeText(
                generator.generateObjectClasses(objectClass, packageName)
            )
        }
    }

    private fun getParserLogger(): Logger {
        return object : Logger {
            override fun debug(message: String) {
                logger.debug(message)
            }

            override fun info(message: String) {
                logger.info(message)
            }

            override fun warn(message: String, error: Exception?) {
                logger.warn(message, error)
            }

            override fun error(message: String, error: Exception?) {
                logger.error(message, error)
            }
        }
    }

    /**
     * "my_icon" -> "MyIcon"
     * "_my_icon" -> "MyIcon"
     * "my_icon_" -> "MyIcon"
     * "my_icon_0" -> "MyIcon0"
     * "0_my_icon" -> "_0MyIcon"
     * "MyIcon" -> "MyIcon"
     */
    private fun String.toKotlinName(): String {
        return this
            // replace all "{symbol}" to "_"
            .replace(asciiSymbolsPattern, "_")
            // split chunks and remove "_"
            .splitToSequence("_")
            .filter { it.isNotEmpty() }
            .flatMap { part ->
                val wordChunks = mutableListOf<String>()
                // reverse string to parse from end to start
                // eg. "MySVGIcon" -> "nocIGVSyM"
                var str = part.reversed()
                while (str.isNotEmpty()) {
                    // get word chunks from head
                    // eg. "nocIGVSyM" -> head chunk = "nocI", remains = "GVSyM"
                    //     "GVSyM" -> head chunk = "GVS", remains = "yM"
                    //     "yM" -> head chunk = "yM", remains = ""
                    val match = chunkPattern.matchAt(str, 0)?.value ?: str.take(1)
                    wordChunks.add(
                        // reverse chunk
                        // eg. "nocI" -> "Icon"
                        match.reversed()
                    )
                    str = str.drop(match.length)
                }
                // reverse chunks
                // eg. ["Icon", "SVG", "My"] -> ["My", "SVG", "Icon"]
                wordChunks.reversed()
            }.map { wordCuhnk ->
                // capitalize word
                // eg. "icon" -> "Icon"
                wordCuhnk.replaceFirstChar { it.uppercase() }
            }
            // join strings
            // eg. ["My", "SVG", "Icon"] -> "MySVGIcon"
            .joinToString("")
            // add "_" if first character is a number
            .replace("^[0-9]".toRegex()) { "_${it.value}" }
    }

    private fun String.toKotlinClassName(): String {
        return if (equals("automirrored", ignoreCase = true)) {
            "AutoMirrored"
        } else toKotlinName()
    }

    companion object {
        private val asciiSymbolsPattern: Regex =
            """[ !@#\\$%^&*()_+={}\[\]:;"'<>,.?/~`|-]""".toRegex()

        /**
         * "esaclemaC" (<- "Camelcase" reversed)
         */
        private val reverseCamelPattern: String = "[a-z]+[A-Z]"

        /**
         * "UPPERCASE"
         */
        private val upperCasesPattern: String = "[A-Z]+"

        /**
         * "文字列"
         */
        private val nonAlphanumericsPattern: String = "[^a-zA-Z0-9]+"

        /**
         * "012"
         */
        private val numericsPattern: String = "[0-9]+"

        /**
         * "lowercase"
         */
        private val lowerCasesPattern: String = "[a-z]+"

        /**
         * match chunk string
         */
        private val chunkPattern =
            "$reverseCamelPattern|$upperCasesPattern|$nonAlphanumericsPattern|$numericsPattern|$lowerCasesPattern".toRegex()
    }
}

/**
 * Get path segments sequence
 */
private fun Path.segments(): Sequence<String> {
    return sequence {
        (0..<nameCount).forEach {
            yield(getName(it).name)
        }
    }
}
