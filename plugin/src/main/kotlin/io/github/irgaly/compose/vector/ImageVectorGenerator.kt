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
                            // TODO: use タグを fun で再現する
                            imageVector.nodes.recursiveForEach(
                                onGroupBegin = { node ->
                                    val groupArguments = buildList<CodeBlock.Builder.() -> Unit> {
                                        if (node.name != null) {
                                            add { add("name = %S", node.name) }
                                        }
                                        if (node.rotate != null) {
                                            add { add("rotate = %Lf", node.rotate) }
                                        }
                                        if (node.pivotX != null) {
                                            add { add("pivotX = %Lf", node.pivotX) }
                                        }
                                        if (node.pivotY != null) {
                                            add { add("pivotY = %Lf", node.pivotY) }
                                        }
                                        if (node.scaleX != null) {
                                            add { add("scaleX = %Lf", node.scaleX) }
                                        }
                                        if (node.scaleY != null) {
                                            add { add("scaleY = %Lf", node.scaleY) }
                                        }
                                        if (node.translationX != null) {
                                            add { add("translationX = %Lf", node.translationX) }
                                        }
                                        if (node.translationY != null) {
                                            add { add("translationY = %Lf", node.translationY) }
                                        }
                                        if (node.clipPathData.isNotEmpty()) {
                                            add {
                                                add("clipPathData = %M {\n", MemberNames.Vector.PathData)
                                                indent()
                                                node.clipPathData.forEach { pathNode ->
                                                    add(pathNode.toCodeBlock())
                                                    add("\n")
                                                }
                                                unindent()
                                                add("}")
                                            }
                                        }
                                    }
                                    if (groupArguments.isNotEmpty()) {
                                        add("%M(", MemberNames.Vector.Group)
                                        groupArguments.forEachIndexed { index, block ->
                                            if (0 < index) {
                                                add(", ")
                                            }
                                            block()
                                        }
                                        beginControlFlow(")")
                                    } else {
                                        beginControlFlow("%M", MemberNames.Vector.Group)
                                    }
                                },
                                onGroupEnd = { _ ->
                                    endControlFlow()
                                },
                                onPath = { node ->
                                    add("addPath(")
                                    buildList<CodeBlock.Builder.() -> Unit> {
                                        add {
                                            add(/* pathData = */ "%M {\n", MemberNames.Vector.PathData)
                                            indent()
                                            node.pathData.forEach { pathNode ->
                                                add(pathNode.toCodeBlock())
                                                add("\n")
                                            }
                                            unindent()
                                            add("}")
                                        }
                                        if (node.pathFillType != null) {
                                            add { add("pathFillType = %M.%L", MemberNames.PathFillType, node.pathFillType.name) }
                                        }
                                        if (node.name != null) {
                                            add { add("name = %S", node.name) }
                                        }
                                        if (node.fill != null) {
                                            add {
                                                add("fill = ")
                                                add(node.fill.toCodeBlock())
                                            }
                                        }
                                        if (node.fillAlpha != null) {
                                            add { add("fillAlpha = %Lf", node.fillAlpha) }
                                        }
                                        if (node.stroke != null) {
                                            add {
                                                add("stroke = ")
                                                add(node.stroke.toCodeBlock())
                                            }
                                        }
                                        if (node.strokeAlpha != null) {
                                            add { add("strokeAlpha = %Lf", node.strokeAlpha) }
                                        }
                                        if (node.strokeLineWidth != null) {
                                            add { add("strokeLineWidth = %Lf", node.strokeLineWidth) }
                                        }
                                        if (node.strokeLineCap != null) {
                                            add { add("strokeLineCap = %M.%L", MemberNames.StrokeCap, node.strokeLineCap.name) }
                                        }
                                        if (node.strokeLineJoin != null) {
                                            add { add("strokeLineJoin = %M.%L", MemberNames.StrokeJoin, node.strokeLineJoin.name) }
                                        }
                                        if (node.strokeLineMiter != null) {
                                            add { add("strokeLineMiter = %Lf", node.strokeLineMiter) }
                                        }
                                        if (node.trimPathStart != null) {
                                            add { add("trimPathStart = %Lf", node.trimPathStart) }
                                        }
                                        if (node.trimPathEnd != null) {
                                            add { add("trimPathEnd = %Lf", node.trimPathEnd) }
                                        }
                                        if (node.trimPathOffset != null) {
                                            add { add("trimPathOffset = %Lf", node.trimPathOffset) }
                                        }
                                    }.forEachIndexed { index, block ->
                                        if (0 < index) {
                                            add(", ")
                                        }
                                        block()
                                    }
                                    addStatement(")")
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

    private fun List<ImageVector.VectorNode>.recursiveForEach(
        onGroupBegin: (node: ImageVector.VectorNode.VectorGroup) -> Unit,
        onGroupEnd: (node: ImageVector.VectorNode.VectorGroup) -> Unit,
        onPath: (node: ImageVector.VectorNode.VectorPath) -> Unit,
    ) {
        forEach { node ->
            when (node) {
                is ImageVector.VectorNode.VectorGroup -> {
                    onGroupBegin(node)
                    node.nodes.recursiveForEach(
                        onGroupBegin = onGroupBegin,
                        onGroupEnd = onGroupEnd,
                        onPath = onPath
                    )
                    onGroupEnd(node)
                }

                is ImageVector.VectorNode.VectorPath -> onPath(node)
            }
        }
    }

    private fun ImageVector.PathNode.toCodeBlock(): CodeBlock {
        return buildCodeBlock {
            when (this@toCodeBlock) {
                is ImageVector.PathNode.ArcTo -> {
                    add(
                        "arcTo(" +
                                /* horizontalEllipseRadius = */ "%Lf, " +
                                /* verticalEllipseRadius = */ "%Lf, " +
                                /* theta = */ "%Lf, " +
                                /* isMoreThanHalf = */ "%Lf, " +
                                /* isPositiveArc = */ "%Lf, " +
                                /* x1 = */ "%Lf, " +
                                /* y1 = */ "%Lf" +
                                ")",
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
                    add("close()")
                }

                is ImageVector.PathNode.CurveTo -> {
                    add(
                        "curveTo(" +
                                /* x1 = */ "%Lf, " +
                                /* y1 = */ "%Lf, " +
                                /* x2 = */ "%Lf, " +
                                /* y2 = */ "%Lf, " +
                                /* x3 = */ "%Lf, " +
                                /* y3 = */ "%Lf" +
                                ")",
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
                        "horizontalLineTo(" +
                                /* x = */ "%Lf" +
                                ")",
                        x
                    )
                }

                is ImageVector.PathNode.LineTo -> {
                    add(
                        "lineTo(" +
                                /* x = */ "%Lf, " +
                                /* y = */ "%Lf" +
                                ")",
                        x,
                        y
                    )
                }

                is ImageVector.PathNode.MoveTo -> {
                    add(
                        "moveTo(" +
                                /* x = */ "%Lf, " +
                                /* y = */ "%Lf" +
                                ")",
                        x,
                        y
                    )
                }

                is ImageVector.PathNode.QuadTo -> {
                    add(
                        "quadTo(" +
                                /* x1 = */ "%Lf, " +
                                /* y1 = */ "%Lf, " +
                                /* x2 = */ "%Lf, " +
                                /* y2 = */ "%Lf" +
                                ")",
                        x1,
                        y1,
                        x2,
                        y2
                    )
                }

                is ImageVector.PathNode.ReflectiveCurveTo -> {
                    add(
                        "reflectiveCurveTo(" +
                                /* x1 = */ "%Lf, " +
                                /* y1 = */ "%Lf, " +
                                /* x2 = */ "%Lf, " +
                                /* y2 = */ "%Lf" +
                                ")",
                        x1,
                        y1,
                        x2,
                        y2
                    )
                }

                is ImageVector.PathNode.ReflectiveQuadTo -> {
                    add(
                        "reflectiveQuadTo(" +
                                /* x = */ "%Lf, " +
                                /* y = */ "%Lf" +
                                ")",
                        x,
                        y
                    )
                }

                is ImageVector.PathNode.RelativeArcTo -> {
                    add(
                        "arcToRelative(" +
                                /* a = */ "%Lf, " +
                                /* b = */ "%Lf, " +
                                /* theta = */ "%Lf, " +
                                /* isMoreThanHalf = */ "%Lf, " +
                                /* isPositiveArc = */ "%Lf, " +
                                /* dx1 = */ "%Lf, " +
                                /* dy1 = */ "%Lf" +
                                ")",
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
                        "curveToRelative(" +
                                /* dx1 = */ "%Lf, " +
                                /* dy1 = */ "%Lf, " +
                                /* dx2 = */ "%Lf, " +
                                /* dy2 = */ "%Lf, " +
                                /* dx3 = */ "%Lf, " +
                                /* dy3 = */ "%Lf" +
                                ")",
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
                        "horizontalLineToRelative(" +
                                /* dx = */ "%Lf" +
                                ")",
                        dx
                    )
                }

                is ImageVector.PathNode.RelativeLineTo -> {
                    add(
                        "lineToRelative(" +
                                /* dx = */ "%Lf, " +
                                /* dy = */ "%Lf" +
                                ")",
                        dx,
                        dy
                    )
                }

                is ImageVector.PathNode.RelativeMoveTo -> {
                    add(
                        "moveToRelative(" +
                                /* dx = */ "%Lf, " +
                                /* dy= */ "%Lf" +
                                ")",
                        dx,
                        dy
                    )
                }

                is ImageVector.PathNode.RelativeQuadTo -> {
                    add(
                        "quadToRelative(" +
                                /* dx1 = */ "%Lf, " +
                                /* dy1 = */ "%Lf, " +
                                /* dx2 = */ "%Lf, " +
                                /* dy2 = */ "%Lf" +
                                ")",
                        dx1,
                        dy1,
                        dx2,
                        dy2
                    )
                }

                is ImageVector.PathNode.RelativeReflectiveCurveTo -> {
                    add(
                        "reflectiveCurveToRelative(" +
                                /* dx1 = */ "%Lf, " +
                                /* dy1 = */ "%Lf, " +
                                /* dx2 = */ "%Lf, " +
                                /* dy2 = */ "%Lf" +
                                ")",
                        dx1,
                        dy1,
                        dx2,
                        dy2
                    )
                }

                is ImageVector.PathNode.RelativeReflectiveQuadTo -> {
                    add(
                        "reflectiveQuadToRelative(" +
                                /* dx = */ "%Lf, " +
                                /* dy = */ "%Lf" +
                                ")",
                        dx,
                        dy
                    )
                }

                is ImageVector.PathNode.RelativeVerticalTo -> {
                    add(
                        "verticalLineToRelative(" +
                                /* dy = */ "%Lf" +
                                ")",
                        dy
                    )
                }

                is ImageVector.PathNode.VerticalTo -> {
                    add(
                        "verticalLineTo(" +
                                /* y = */ "%Lf" +
                                ")",
                        y
                    )
                }
            }
        }
    }

    fun ImageVector.Brush.toCodeBlock(): CodeBlock {
        return buildCodeBlock {
            when (this@toCodeBlock) {
                is ImageVector.Brush.SolidColor -> {
                    add("%M(%M(%L))", MemberNames.SolidColor, MemberNames.Color, color.hex)
                }
            }
        }
    }
}
