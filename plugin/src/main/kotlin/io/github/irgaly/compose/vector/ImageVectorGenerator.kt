package io.github.irgaly.compose.vector

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.buildCodeBlock
import io.github.irgaly.compose.icons.xml.setIndent
import io.github.irgaly.compose.vector.node.ImageVector

/**
 * ImageVector to Kotlin Implementation
 */
class ImageVectorGenerator {
    fun generate(
        imageVector: ImageVector,
        destinationPackage: String,
    ): String {
        val destinationClass = ClassName(destinationPackage, "Icons")
        val builder = FileSpec.builder(
            packageName = destinationPackage,
            fileName = "${imageVector.name}.kt"
        )
        val backingProperty = PropertySpec.builder(
            name = "_${imageVector.name.replaceFirstChar { it.lowercase() }}",
            type = ClassNames.ImageVector.copy(nullable = true)
        ).mutable(true)
            .addModifiers(KModifier.PRIVATE)
            .initializer("null")
            .build()
        val propertySpec = PropertySpec.builder(
            name = imageVector.name, type = ClassNames.ImageVector
        ).addAnnotation(
            AnnotationSpec.builder(Suppress::class)
                .addMember(
                    "%S", "RedundantVisibilityModifier"
                ).build()
        ).receiver(destinationClass)
            .getter(
                FunSpec.getterBuilder()
                    .addCode(
                        buildCodeBlock {
                            beginControlFlow("if (%N != null)", backingProperty)
                            addStatement("return %N!!", backingProperty)
                            endControlFlow()
                        }
                    ).addCode(
                        buildCodeBlock {
                            addStatement(
                                "%N = %M(" +
                                        /* name = */ "%S," +
                                        /* defaultWidth = */ "%L.%M," +
                                        /* defaultHeight = */ "%L.%M," +
                                        /* viewportWidth = */ "%L," +
                                        /* viewportHeight = */ "%L" +
                                        (if (imageVector.autoMirror) ",autoMirror = true" else "") +
                                        ").apply {",
                                backingProperty,
                                MemberNames.ImageVectorBuilder,
                                imageVector.name,
                                imageVector.defaultWidth,
                                MemberNames.Dp,
                                imageVector.defaultHeight,
                                MemberNames.Dp,
                                imageVector.viewportWidth,
                                imageVector.viewportHeight,
                            )
                            indent()
                            unindent()
                            addStatement("}.build()")
                        }
                    )
                    .addStatement("return %N!!", backingProperty)
                    .build()
            )
        builder.addProperty(propertySpec.build())
        builder.addProperty(backingProperty)
        val preview1FunSpec = FunSpec.builder(
            "${imageVector.name}Preview"
        ).addAnnotation(
            AnnotationSpec.builder(ClassNames.Preview).build()
        ).addAnnotation(
            AnnotationSpec.builder(ClassNames.Composable).build()
        ).addModifiers(
            KModifier.PRIVATE
        ).addCode(
            "%M(${imageVector.name}, null)", MemberNames.Image
        )
        builder.addFunction(preview1FunSpec.build())
        val preview2FunSpec = FunSpec.builder(
            "${imageVector.name}BackgroundPreview"
        ).addAnnotation(
            AnnotationSpec.builder(ClassNames.Preview)
                .addMember(
                    "showBackground = true"
                ).build()
        ).addAnnotation(
            AnnotationSpec.builder(ClassNames.Composable).build()
        ).addModifiers(
            KModifier.PRIVATE
        ).addCode(
            "%M(${imageVector.name}, null)", MemberNames.Image
        )
        builder.addFunction(preview2FunSpec.build())
        return builder.setIndent().build().toString()
    }
}
