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
                                imageVector.defaultWidth.toShortValueString(),
                                MemberNames.Dp,
                                imageVector.defaultHeight.toShortValueString(),
                                MemberNames.Dp,
                                imageVector.viewportWidth.toShortValueString(),
                                imageVector.viewportHeight.toShortValueString(),
                            )
                            indent()
                            val rootGroup = imageVector.rootGroup
                            var nodes = listOf<ImageVector.VectorNode>(rootGroup)
                            if (rootGroup.translationX == null && rootGroup.translationY == null) {
                                nodes = rootGroup.nodes
                                if (rootGroup.referencedExtra != null) {
                                    addExtraReferenceCodeBlock(rootGroup.referencedExtra)
                                }
                            }
                            nodes.recursiveForEach(
                                onGroupBegin = { node ->
                                    val groupArguments = buildList<CodeBlock.Builder.() -> Unit> {
                                        if (node.name != null) {
                                            add { add("name = %S", node.name) }
                                        }
                                        if (node.rotate != null) {
                                            add { add("rotate = %Lf", node.rotate.toShortValueString()) }
                                        }
                                        if (node.pivotX != null) {
                                            add { add("pivotX = %Lf", node.pivotX.toShortValueString()) }
                                        }
                                        if (node.pivotY != null) {
                                            add { add("pivotY = %Lf", node.pivotY.toShortValueString()) }
                                        }
                                        if (node.scaleX != null) {
                                            add { add("scaleX = %Lf", node.scaleX.toShortValueString()) }
                                        }
                                        if (node.scaleY != null) {
                                            add { add("scaleY = %Lf", node.scaleY.toShortValueString()) }
                                        }
                                        if (node.translationX != null) {
                                            add { add("translationX = %Lf", node.translationX.toShortValueString()) }
                                        }
                                        if (node.translationY != null) {
                                            add { add("translationY = %Lf", node.translationY.toShortValueString()) }
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
                                    if (node.referencedExtra != null) {
                                        addExtraReferenceCodeBlock(node.referencedExtra)
                                    }
                                },
                                onGroupEnd = { _ ->
                                    endControlFlow()
                                },
                                onPath = { node ->
                                    val useTrim = ((node.trimPathStart != null) ||
                                            (node.trimPathEnd != null) ||
                                            (node.trimPathOffset != null))
                                    if (useTrim) {
                                        add("addPath(")
                                    } else {
                                        add("%M(", MemberNames.Vector.Path)
                                    }
                                    buildList<CodeBlock.Builder.() -> Unit> {
                                        if (useTrim) {
                                            add {
                                                add(/* pathData = */ "%M {\n",
                                                    MemberNames.Vector.PathData
                                                )
                                                indent()
                                                node.pathData.forEach { pathNode ->
                                                    add(pathNode.toCodeBlock())
                                                    add("\n")
                                                }
                                                unindent()
                                                add("}")
                                            }
                                        }
                                        if (node.pathFillType != null) {
                                            add { add("pathFillType = %M.%L", MemberNames.PathFillType, node.pathFillType.name) }
                                        }
                                        if (node.name != null) {
                                            add { add("name = %S", node.name) }
                                        }
                                        if (node.extraReference?.fillId != null) {
                                            add {
                                                add("fill = fill${node.extraReference.fillId}")
                                            }
                                        } else if (node.fill != null) {
                                            add {
                                                add("fill = ")
                                                add(node.fill.toCodeBlock())
                                            }
                                        }
                                        if (node.extraReference?.fillAlphaId != null) {
                                            add {
                                                add("fillAlpha = fillAlpha${node.extraReference.fillAlphaId}")
                                            }
                                        } else if (node.fillAlpha != null) {
                                            add { add("fillAlpha = %Lf", node.fillAlpha.toShortValueString()) }
                                        }
                                        if (node.extraReference?.strokeId != null) {
                                            add {
                                                add("stroke = stroke${node.extraReference.strokeId}")
                                            }
                                        } else if (node.stroke != null) {
                                            add {
                                                add("stroke = ")
                                                add(node.stroke.toCodeBlock())
                                            }
                                        }
                                        if (node.extraReference?.strokeAlphaId != null) {
                                            add {
                                                add("strokeAlpha = strokeAlpha${node.extraReference.strokeAlphaId}")
                                            }
                                        } else if (node.strokeAlpha != null) {
                                            add { add("strokeAlpha = %Lf", node.strokeAlpha.toShortValueString()) }
                                        }
                                        if (node.extraReference?.strokeLineWidthId != null) {
                                            add {
                                                add("strokeLineWidth = strokeLineWidth${node.extraReference.strokeLineWidthId}")
                                            }
                                        } else if (node.strokeLineWidth != null) {
                                            add { add("strokeLineWidth = %Lf", node.strokeLineWidth.toShortValueString()) }
                                        }
                                        if (node.extraReference?.strokeLineCapId != null) {
                                            add {
                                                add("strokeLineCap = strokeLineCap${node.extraReference.strokeLineCapId}")
                                            }
                                        } else if (node.strokeLineCap != null) {
                                            add { add("strokeLineCap = %M.%L", MemberNames.StrokeCap, node.strokeLineCap.name) }
                                        }
                                        if (node.extraReference?.strokeLineJoinId != null) {
                                            add {
                                                add("strokeLineJoin = strokeLineJoin${node.extraReference.strokeLineJoinId}")
                                            }
                                        } else if (node.strokeLineJoin != null) {
                                            add { add("strokeLineJoin = %M.%L", MemberNames.StrokeJoin, node.strokeLineJoin.name) }
                                        }
                                        if (node.extraReference?.strokeLineMiterId != null) {
                                            add {
                                                add("strokeLineMiter = strokeLineMiter${node.extraReference.strokeLineMiterId}")
                                            }
                                        } else if (node.strokeLineMiter != null) {
                                            add { add("strokeLineMiter = %Lf", node.strokeLineMiter.toShortValueString()) }
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
                                    if (useTrim) {
                                        addStatement(")")
                                    } else {
                                        beginControlFlow(")")
                                        node.pathData.forEach { pathNode ->
                                            add(pathNode.toCodeBlock())
                                            add("\n")
                                        }
                                        endControlFlow()
                                    }
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

    private fun CodeBlock.Builder.addExtraReferenceCodeBlock(
        referencedExtra: ImageVector.VectorNode.VectorGroup.Extra
    ) {
        if (referencedExtra.fill != null) {
            add("val fill${referencedExtra.id} = ")
            add(referencedExtra.fill.toCodeBlock())
            add("\n")
        }
        if (referencedExtra.fillAlpha != null) {
            addStatement(
                "val fillAlpha${referencedExtra.id} = %Lf",
                referencedExtra.fillAlpha.toShortValueString()
            )
        }
        if (referencedExtra.stroke != null) {
            add("val stroke${referencedExtra.id} = ")
            add(referencedExtra.stroke.toCodeBlock())
            add("\n")
        }
        if (referencedExtra.strokeAlpha != null) {
            addStatement(
                "val strokeAlpha${referencedExtra.id} = %Lf",
                referencedExtra.strokeAlpha.toShortValueString()
            )
        }
        if (referencedExtra.strokeLineWidth != null) {
            addStatement(
                "val strokeLineWidth${referencedExtra.id} = %Lf",
                referencedExtra.strokeLineWidth.toShortValueString()
            )
        }
        if (referencedExtra.strokeLineCap != null) {
            addStatement(
                "val strokeLineCap${referencedExtra.id} = %M.%L",
                MemberNames.StrokeCap,
                referencedExtra.strokeLineCap.name
            )
        }
        if (referencedExtra.strokeLineJoin != null) {
            addStatement(
                "val strokeLineJoin${referencedExtra.id} = %M.%L",
                MemberNames.StrokeJoin,
                referencedExtra.strokeLineJoin.name
            )
        }
        if (referencedExtra.strokeLineMiter != null) {
            addStatement(
                "val strokeLineMiter${referencedExtra.id} = %Lf",
                referencedExtra.strokeLineMiter.toShortValueString()
            )
        }
    }
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
                            /* isMoreThanHalf = */ "%L, " +
                            /* isPositiveArc = */ "%L, " +
                            /* x1 = */ "%Lf, " +
                            /* y1 = */ "%Lf" +
                            ")",
                    horizontalEllipseRadius.toShortValueString(),
                    verticalEllipseRadius.toShortValueString(),
                    theta.toShortValueString(),
                    isMoreThanHalf,
                    isPositiveArc,
                    arcStartX.toShortValueString(),
                    arcStartY.toShortValueString()
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
                    x1.toShortValueString(),
                    y1.toShortValueString(),
                    x2.toShortValueString(),
                    y2.toShortValueString(),
                    x3.toShortValueString(),
                    y3.toShortValueString()
                )
            }

            is ImageVector.PathNode.HorizontalTo -> {
                add(
                    "horizontalLineTo(" +
                            /* x = */ "%Lf" +
                            ")",
                    x.toShortValueString()
                )
            }

            is ImageVector.PathNode.LineTo -> {
                add(
                    "lineTo(" +
                            /* x = */ "%Lf, " +
                            /* y = */ "%Lf" +
                            ")",
                    x.toShortValueString(),
                    y.toShortValueString()
                )
            }

            is ImageVector.PathNode.MoveTo -> {
                add(
                    "moveTo(" +
                            /* x = */ "%Lf, " +
                            /* y = */ "%Lf" +
                            ")",
                    x.toShortValueString(),
                    y.toShortValueString()
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
                    x1.toShortValueString(),
                    y1.toShortValueString(),
                    x2.toShortValueString(),
                    y2.toShortValueString()
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
                    x1.toShortValueString(),
                    y1.toShortValueString(),
                    x2.toShortValueString(),
                    y2.toShortValueString()
                )
            }

            is ImageVector.PathNode.ReflectiveQuadTo -> {
                add(
                    "reflectiveQuadTo(" +
                            /* x = */ "%Lf, " +
                            /* y = */ "%Lf" +
                            ")",
                    x.toShortValueString(),
                    y.toShortValueString()
                )
            }

            is ImageVector.PathNode.RelativeArcTo -> {
                add(
                    "arcToRelative(" +
                            /* a = */ "%Lf, " +
                            /* b = */ "%Lf, " +
                            /* theta = */ "%Lf, " +
                            /* isMoreThanHalf = */ "%L, " +
                            /* isPositiveArc = */ "%L, " +
                            /* dx1 = */ "%Lf, " +
                            /* dy1 = */ "%Lf" +
                            ")",
                    horizontalEllipseRadius.toShortValueString(),
                    verticalEllipseRadius.toShortValueString(),
                    theta.toShortValueString(),
                    isMoreThanHalf,
                    isPositiveArc,
                    arcStartDx.toShortValueString(),
                    arcStartDy.toShortValueString()
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
                    dx1.toShortValueString(),
                    dy1.toShortValueString(),
                    dx2.toShortValueString(),
                    dy2.toShortValueString(),
                    dx3.toShortValueString(),
                    dy3.toShortValueString()
                )
            }

            is ImageVector.PathNode.RelativeHorizontalTo -> {
                add(
                    "horizontalLineToRelative(" +
                            /* dx = */ "%Lf" +
                            ")",
                    dx.toShortValueString()
                )
            }

            is ImageVector.PathNode.RelativeLineTo -> {
                add(
                    "lineToRelative(" +
                            /* dx = */ "%Lf, " +
                            /* dy = */ "%Lf" +
                            ")",
                    dx.toShortValueString(),
                    dy.toShortValueString()
                )
            }

            is ImageVector.PathNode.RelativeMoveTo -> {
                add(
                    "moveToRelative(" +
                            /* dx = */ "%Lf, " +
                            /* dy= */ "%Lf" +
                            ")",
                    dx.toShortValueString(),
                    dy.toShortValueString()
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
                    dx1.toShortValueString(),
                    dy1.toShortValueString(),
                    dx2.toShortValueString(),
                    dy2.toShortValueString()
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
                    dx1.toShortValueString(),
                    dy1.toShortValueString(),
                    dx2.toShortValueString(),
                    dy2.toShortValueString()
                )
            }

            is ImageVector.PathNode.RelativeReflectiveQuadTo -> {
                add(
                    "reflectiveQuadToRelative(" +
                            /* dx = */ "%Lf, " +
                            /* dy = */ "%Lf" +
                            ")",
                    dx.toShortValueString(),
                    dy.toShortValueString()
                )
            }

            is ImageVector.PathNode.RelativeVerticalTo -> {
                add(
                    "verticalLineToRelative(" +
                            /* dy = */ "%Lf" +
                            ")",
                    dy.toShortValueString()
                )
            }

            is ImageVector.PathNode.VerticalTo -> {
                add(
                    "verticalLineTo(" +
                            /* y = */ "%Lf" +
                            ")",
                    y.toShortValueString()
                )
            }
        }
    }
}

private fun ImageVector.Brush.toCodeBlock(): CodeBlock {
    return buildCodeBlock {
        when (this@toCodeBlock) {
            is ImageVector.Brush.SolidColor -> {
                add("%M(", MemberNames.SolidColor)
                add(color.toCodeBlock())
                add(")")
            }

            is ImageVector.Brush.LinearGradient -> {
                add("%M(", MemberNames.Brush.LinearGradient)
                val args = buildList<CodeBlock.Builder.() -> Unit> {
                    colorStops.forEach {
                        add {
                            add("%Lf to ", it.first.toShortValueString())
                            add(it.second.toCodeBlock())
                        }
                    }
                    add {
                        add(
                            "start = %M(%Lf, %Lf)",
                            MemberNames.Offset,
                            start.first.toShortValueString(),
                            start.second.toShortValueString()
                        )
                    }
                    add {
                        add(
                            "end = %M(%Lf, %Lf)",
                            MemberNames.Offset,
                            end.first.toShortValueString(),
                            end.second.toShortValueString()
                        )
                    }
                    if (tileMode != ImageVector.TileMode.Clamp) {
                        add {
                            add("tileMode = %M.%L", MemberNames.TileMode, tileMode.name)
                        }
                    }
                }
                args.forEachIndexed { index, block ->
                    if (0 < index) {
                        add(", ")
                    }
                    block()
                }
                add(")")
            }

            is ImageVector.Brush.RadialGradient -> {
                add("%M(", MemberNames.Brush.RadialGradient)
                val args = buildList<CodeBlock.Builder.() -> Unit> {
                    colorStops.forEach {
                        add {
                            add("%Lf to ", it.first.toShortValueString())
                            add(it.second.toCodeBlock())
                        }
                    }
                    add {
                        add(
                            "center = %M(%Lf, %Lf)",
                            MemberNames.Offset,
                            center.first.toShortValueString(),
                            center.second.toShortValueString()
                        )
                    }
                    add { add("radius = %Lf", radius.toShortValueString()) }
                    if (tileMode != ImageVector.TileMode.Clamp) {
                        add {
                            add("tileMode = %M.%L", MemberNames.TileMode, tileMode.name)
                        }
                    }
                }
                args.forEachIndexed { index, block ->
                    if (0 < index) {
                        add(", ")
                    }
                    block()
                }
                add(")")
            }
        }
    }
}

private fun ImageVector.Color.toCodeBlock(): CodeBlock {
    return buildCodeBlock {
        when (this@toCodeBlock) {
            is ImageVector.ComposeColor -> {
                add(
                    "%M.%L",
                    MemberNames.Color,
                    name
                )
            }

            is ImageVector.RgbColor -> {
                add(
                    "%M(0x%L)",
                    MemberNames.Color,
                    teHexString()
                )
            }
        }
    }
}

/**
 * 1.5 -> "1.5"
 * 1.0 -> "1"
 */
fun Number.toShortValueString(): String {
    return toString().replace("\\.0$".toRegex(), "")
}
