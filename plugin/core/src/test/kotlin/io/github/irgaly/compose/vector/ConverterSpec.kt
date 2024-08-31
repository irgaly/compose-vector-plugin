package io.github.irgaly.compose.vector

import io.github.irgaly.compose.Logger
import io.github.irgaly.compose.vector.svg.SvgParser
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.io.File

class ConverterSpec: DescribeSpec({
    val isCi = (System.getenv("CI") != null)
    val parser = SvgParser(object : Logger {
        override fun debug(message: String) {
            println("debug: $message")
        }

        override fun info(message: String) {
            println("info: $message")
        }

        override fun warn(message: String, error: Exception?) {
            println("warn: $message | $error")
        }

        override fun error(message: String, error: Exception?) {
            println("error: $message | $error")
        }
    })
    val generator = ImageVectorGenerator()
    describe("SVG file should be exported as expected codes") {
        val resources = File("src/test/resources")
        resources.listFiles()?.sorted()?.filter {
            it.extension == "svg"
        }?.forEach { svgFile ->
            it(svgFile.name) {
                val imageVector = parser.parse(
                    input = svgFile.inputStream(),
                    name = svgFile.nameWithoutExtension
                )
                val actualCodes = generator.generate(
                    imageVector = imageVector,
                    destinationPackage = "io.github.irgaly.compose.vector.test.image",
                    receiverClasses = emptyList(),
                    extensionPackage = "io.github.irgaly.compose.vector.test.image",
                    hasAndroidPreview = true
                )
                val resultFile = resources.resolve("${svgFile.nameWithoutExtension}.kt")
                if (!isCi) {
                    if (!resultFile.exists()) {
                        // First time, create new result file
                        resultFile.writeText(actualCodes)
                    }
                    val previewDirectory = File("../../sample/android/build/test")
                    if (!previewDirectory.exists()) {
                        previewDirectory.mkdirs()
                    }
                    val previewFile = previewDirectory.resolve("${svgFile.nameWithoutExtension}.kt")
                    if (!previewFile.exists() || (previewFile.readText() != actualCodes)) {
                        previewFile.writeText(actualCodes)
                    }
                }
                val expectCodes = resultFile.readText()
                actualCodes shouldBe expectCodes
            }
        }
    }
})
