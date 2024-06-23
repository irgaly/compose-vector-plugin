package io.github.irgaly.compose.vector

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
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
        val property = PropertySpec.builder(
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
                                        /* name = */ "%S, " +
                                        /* defaultWidth = */ "%L.%M, " +
                                        /* defaultHeight = */ "%L.%M, " +
                                        /* viewportWidth = */ "%Lf, " +
                                        /* viewportHeight = */ "%Lf" +
                                        (if (imageVector.autoMirror) ", autoMirror = true" else "") +
                                        ").apply {",
                                backingProperty,
                                MemberNames.ImageVector.Builder,
                                imageVector.name,
                                imageVector.defaultWidth,
                                MemberNames.Dp,
                                imageVector.defaultHeight,
                                MemberNames.Dp,
                                imageVector.viewportWidth,
                                imageVector.viewportHeight,
                            )
                            indent()
                            imageVector.nodes.recursiveForEach(
                                onGroupBegin = { node ->
                                    add(
                                        "%M(" +
                                                /* name = */ "%S," +
                                                /* rotate = */ " %Lf," +
                                                /* pivotX = */ " %Lf," +
                                                /* pivotY = */ " %Lf," +
                                                /* scaleX = */ " %Lf," +
                                                /* scaleY = */ " %Lf," +
                                                /* translationX = */ " %Lf," +
                                                /* translationY = */ " %Lf",
                                        MemberNames.group,
                                        node.name,
                                        node.rotate,
                                        node.pivotX,
                                        node.pivotY,
                                        node.scaleX,
                                        node.scaleY,
                                        node.translationX,
                                        node.translationY,
                                    )
                                    if (node.clipPathData.isNotEmpty()) {
                                        indent()
                                        add("," + /* clipPathData = */ " listOf(\n")
                                        node.clipPathData.forEachIndexed { index, pathNode ->
                                            add(pathNode.toCodeBlock())
                                            if (index != node.clipPathData.lastIndex) {
                                                add(",\n")
                                            }
                                        }
                                        unindent()
                                        add("))")
                                    } else {
                                        add(")")
                                    }
                                    beginControlFlow("")
                                },
                                onGroupEnd = { _ ->
                                    endControlFlow()
                                },
                                onLeafNode = { node ->
                                    addStatement("// other node")
                                }
                            )
                            unindent()
                            addStatement("}.build()")
                        }
                    )
                    .addStatement("return %N!!", backingProperty)
                    .build()
            ).build()
        builder.addProperty(property)
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
            "%M(%N, null)", MemberNames.Image, property,
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
            "%M(%N, null)", MemberNames.Image, property,
        )
        builder.addFunction(preview2FunSpec.build())
        return builder.setIndent().build().toString()
    }

    private fun List<ImageVector.Node>.recursiveForEach(
        onGroupBegin: (node: ImageVector.Group) -> Unit,
        onGroupEnd: (node: ImageVector.Group) -> Unit,
        onLeafNode: (node: ImageVector.Node) -> Unit,
    ) {
        forEach { node ->
            when (node) {
                is ImageVector.Group -> {
                    onGroupBegin(node)
                    node.nodes.recursiveForEach(
                        onGroupBegin, onGroupEnd, onLeafNode
                    )
                    onGroupEnd(node)
                }

                else -> onLeafNode(node)
            }
        }
    }

    private fun ImageVector.PathNode.toCodeBlock(): CodeBlock {
        return buildCodeBlock {
            when (this@toCodeBlock) {
                is ImageVector.PathNode.ArcTo -> {
                    add(
                        "%M(" +
                                /* horizontalEllipseRadius = */ "%Lf, " +
                                /* verticalEllipseRadius = */ "%Lf, " +
                                /* theta = */ "%Lf, " +
                                /* isMoreThanHalf = */ "%Lf, " +
                                /* isPositiveArc = */ "%Lf, " +
                                /* arcStartX = */ "%Lf, " +
                                /* arcStartY = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.ArcTo,
                        horizontalEllipseRadius,
                        verticalEllipseRadius,
                        theta,
                        isMoreThanHalf,
                        isPositiveArc,
                        arcStartX,
                        arcStartY
                    )
                }

                ImageVector.PathNode.Close -> {
                    add("%M", MemberNames.PathNode.Close)
                }

                is ImageVector.PathNode.CurveTo -> {
                    add(
                        "%M(" +
                                /* x1 = */ "%Lf, " +
                                /* y1 = */ "%Lf, " +
                                /* x2 = */ "%Lf, " +
                                /* y2 = */ "%Lf, " +
                                /* x3 = */ "%Lf, " +
                                /* y3 = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.CurveTo,
                        x1,
                        y1,
                        x2,
                        y2,
                        x3,
                        y3
                    )
                }

                is ImageVector.PathNode.HorizontalTo -> {
                    add(
                        "%M(" +
                                /* x = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.HorizontalTo,
                        x
                    )
                }

                is ImageVector.PathNode.LineTo -> {
                    add(
                        "%M(" +
                                /* x = */ "%Lf, " +
                                /* y = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.LineTo,
                        x,
                        y
                    )
                }

                is ImageVector.PathNode.MoveTo -> {
                    add(
                        "%M(" +
                                /* x = */ "%Lf, " +
                                /* y = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.MoveTo,
                        x,
                        y
                    )
                }

                is ImageVector.PathNode.QuadTo -> {
                    add(
                        "%M(" +
                                /* x1 = */ "%Lf, " +
                                /* y1 = */ "%Lf, " +
                                /* x2 = */ "%Lf, " +
                                /* y2 = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.QuadTo,
                        x1,
                        y1,
                        x2,
                        y2
                    )
                }

                is ImageVector.PathNode.ReflectiveCurveTo -> {
                    add(
                        "%M(" +
                                /* x1 = */ "%Lf, " +
                                /* y1 = */ "%Lf, " +
                                /* x2 = */ "%Lf, " +
                                /* y2 = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.ReflectiveCurveTo,
                        x1,
                        y1,
                        x2,
                        y2
                    )
                }

                is ImageVector.PathNode.ReflectiveQuadTo -> {
                    add(
                        "%M(" +
                                /* x = */ "%Lf, " +
                                /* y = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.ReflectiveQuadTo,
                        x,
                        y
                    )
                }

                is ImageVector.PathNode.RelativeArcTo -> {
                    add(
                        "%M(" +
                                /* horizontalEllipseRadius = */ "%Lf, " +
                                /* verticalEllipseRadius = */ "%Lf, " +
                                /* theta = */ "%Lf, " +
                                /* isMoreThanHalf = */ "%Lf, " +
                                /* isPositiveArc = */ "%Lf, " +
                                /* arcStartDx = */ "%Lf, " +
                                /* arcStartDy = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.RelativeArcTo,
                        horizontalEllipseRadius,
                        verticalEllipseRadius,
                        theta,
                        isMoreThanHalf,
                        isPositiveArc,
                        arcStartDx,
                        arcStartDy
                    )
                }

                is ImageVector.PathNode.RelativeCurveTo -> {
                    add(
                        "%M(" +
                                /* dx1 = */ "%Lf, " +
                                /* dy1 = */ "%Lf, " +
                                /* dx2 = */ "%Lf, " +
                                /* dy2 = */ "%Lf, " +
                                /* dx3 = */ "%Lf, " +
                                /* dy3 = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.RelativeCurveTo,
                        dx1,
                        dy1,
                        dx2,
                        dy2,
                        dx3,
                        dy3
                    )
                }

                is ImageVector.PathNode.RelativeHorizontalTo -> {
                    add(
                        "%M(" +
                                /* dx = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.RelativeHorizontalTo,
                        dx
                    )
                }

                is ImageVector.PathNode.RelativeLineTo -> {
                    add(
                        "%M(" +
                                /* dx = */ "%Lf, " +
                                /* dy = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.RelativeLineTo,
                        dx,
                        dy
                    )
                }

                is ImageVector.PathNode.RelativeMoveTo -> {
                    add(
                        "%M(" +
                                /* dx = */ "%Lf, " +
                                /* dy= */ "%Lf" +
                                ")",
                        MemberNames.PathNode.RelativeMoveTo,
                        dx,
                        dy
                    )
                }

                is ImageVector.PathNode.RelativeQuadTo -> {
                    add(
                        "%M(" +
                                /* dx1 = */ "%Lf, " +
                                /* dy1 = */ "%Lf, " +
                                /* dx2 = */ "%Lf, " +
                                /* dy2 = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.RelativeQuadTo,
                        dx1,
                        dy1,
                        dx2,
                        dy2
                    )
                }

                is ImageVector.PathNode.RelativeReflectiveCurveTo -> {
                    add(
                        "%M(" +
                                /* dx1 = */ "%Lf, " +
                                /* dy1 = */ "%Lf, " +
                                /* dx2 = */ "%Lf, " +
                                /* dy2 = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.RelativeReflectiveCurveTo,
                        dx1,
                        dy1,
                        dx2,
                        dy2
                    )
                }

                is ImageVector.PathNode.RelativeReflectiveQuadTo -> {
                    add(
                        "%M(" +
                                /* dx = */ "%Lf, " +
                                /* dy = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.RelativeReflectiveQuadTo,
                        dx,
                        dy
                    )
                }

                is ImageVector.PathNode.RelativeVerticalTo -> {
                    add(
                        "%M(" +
                                /* dy = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.RelativeVerticalTo,
                        dy
                    )
                }

                is ImageVector.PathNode.VerticalTo -> {
                    add(
                        "%M(" +
                                /* y = */ "%Lf" +
                                ")",
                        MemberNames.PathNode.VerticalTo,
                        y
                    )
                }
            }
        }
    }
}
