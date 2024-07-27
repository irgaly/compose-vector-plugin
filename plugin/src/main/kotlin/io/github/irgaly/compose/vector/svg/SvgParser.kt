package io.github.irgaly.compose.vector.svg

import io.github.irgaly.compose.vector.node.ImageVector
import io.github.irgaly.compose.vector.node.ImageVector.Matrix
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVG12DOMImplementation
import org.apache.batik.anim.dom.SVGGraphicsElement
import org.apache.batik.anim.dom.SVGOMAnimatedLength
import org.apache.batik.anim.dom.SVGOMAnimatedRect
import org.apache.batik.anim.dom.SVGOMCircleElement
import org.apache.batik.anim.dom.SVGOMDocument
import org.apache.batik.anim.dom.SVGOMElement
import org.apache.batik.anim.dom.SVGOMEllipseElement
import org.apache.batik.anim.dom.SVGOMGElement
import org.apache.batik.anim.dom.SVGOMLineElement
import org.apache.batik.anim.dom.SVGOMPathElement
import org.apache.batik.anim.dom.SVGOMPolygonElement
import org.apache.batik.anim.dom.SVGOMPolylineElement
import org.apache.batik.anim.dom.SVGOMRectElement
import org.apache.batik.anim.dom.SVGOMSVGElement
import org.apache.batik.anim.dom.SVGOMUseElement
import org.apache.batik.anim.dom.SVGStylableElement
import org.apache.batik.bridge.Bridge
import org.apache.batik.bridge.BridgeContext
import org.apache.batik.bridge.BridgeExtension
import org.apache.batik.bridge.DocumentLoader
import org.apache.batik.bridge.GVTBuilder
import org.apache.batik.bridge.SVGUseElementBridge
import org.apache.batik.bridge.UserAgentAdapter
import org.apache.batik.bridge.svg12.SVG12BridgeContext
import org.apache.batik.css.dom.CSSOMSVGStyleDeclaration
import org.apache.batik.css.engine.CSSContext
import org.apache.batik.css.engine.CSSEngine
import org.apache.batik.css.engine.CSSStylableElement
import org.apache.batik.css.engine.StyleMap
import org.apache.batik.css.engine.value.AbstractColorManager
import org.apache.batik.css.engine.value.AbstractValue
import org.apache.batik.css.engine.value.ShorthandManager
import org.apache.batik.css.engine.value.StringValue
import org.apache.batik.css.engine.value.Value
import org.apache.batik.css.engine.value.ValueConstants.NUMBER_0
import org.apache.batik.css.engine.value.ValueManager
import org.apache.batik.css.parser.CSSLexicalUnit
import org.apache.batik.css.parser.ExtendedParser
import org.apache.batik.css.parser.LexicalUnits
import org.apache.batik.css.parser.Parser
import org.apache.batik.dom.AbstractStylableDocument
import org.apache.batik.dom.GenericAttr
import org.apache.batik.dom.svg.SVGAnimatedPathDataSupport
import org.apache.batik.dom.svg.SVGOMMatrix
import org.apache.batik.gvt.CompositeGraphicsNode
import org.apache.batik.gvt.GraphicsNode
import org.apache.batik.gvt.ShapeNode
import org.apache.batik.parser.PathHandler
import org.apache.batik.parser.PathParser
import org.apache.batik.svggen.SVGColor
import org.apache.batik.svggen.SVGGeneratorContext
import org.apache.batik.svggen.SVGPath
import org.apache.batik.util.XMLResourceDescriptor
import org.w3c.css.sac.LexicalUnit
import org.w3c.dom.DOMImplementation
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.css.CSSPrimitiveValue
import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.svg.SVGMatrix
import org.w3c.dom.svg.SVGPathSegList
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.io.IOException
import java.io.InputStream
import java.util.Collections
import kotlin.reflect.KProperty

/**
 * SVG -> ImageVector
 */
class SvgParser {
    /**
     * @throws IOException
     * @throws IllegalStateException parse error
     */
    fun parse(input: InputStream): ImageVector {
        val document = try {
            SAXSVGDocumentFactoryCSS3ColorFix(
                XMLResourceDescriptor.getXMLParserClassName()
            ).createDocument("xml", input) as SVGOMDocument
        } catch (error: IOException) {
            throw error
        }
        val bridgeContext = document.initializeSvgCssEngine()
        val svg = (document.rootElement as SVGOMSVGElement).apply {
            mergeStyle()
        }
        val viewBox = (svg.viewBox as SVGOMAnimatedRect)
        var viewBoxWidth = if (viewBox.isSpecified) svg.viewBox.baseVal.width else null
        var viewBoxHeight = if (viewBox.isSpecified) svg.viewBox.baseVal.height else null
        val width = if ((svg.width as SVGOMAnimatedLength).isSpecified) {
            svg.width.baseVal.valueInSpecifiedUnits
        } else viewBoxWidth ?: error("svg tag has no width")
        val height = if ((svg.height as SVGOMAnimatedLength).isSpecified) {
            svg.height.baseVal.valueInSpecifiedUnits
        } else viewBoxHeight ?: error("svg tag has no width")
        if (viewBoxWidth == null) {
            viewBoxWidth = width
        }
        if (viewBoxHeight == null) {
            viewBoxHeight = height
        }
        val groups = mutableListOf<GroupInfo>()
        var extraId: Long = 0
        svg.traverse(
            onElementBegin = { element ->
                val graphicsNode: GraphicsNode? = bridgeContext.getGraphicsNode(element)
                val clipPathShape = graphicsNode?.clip?.clipPath
                when (element) {
                    is SVGOMSVGElement,
                    is SVGOMGElement,
                    is SVGOMUseElement,
                    -> {
                        check(element is SVGStylableElement)
                        check(graphicsNode != null)
                        val extra = element.getStyleExtra(extraId = extraId.toString())
                        if (extra != null) {
                            extraId++
                        }
                        val group: ImageVector.VectorNode.VectorGroup
                        if (element == svg) {
                            // root group
                            val scaleX = (width / viewBoxWidth.toDouble())
                            val scaleY = (height / viewBoxHeight.toDouble())
                            val scale = AffineTransform().apply {
                                scale(scaleX, scaleY)
                            }
                            val matrix = AffineTransform().apply {
                                concatenate(scale.createInverse())
                                concatenate(graphicsNode.transform)
                            }.toMatrix()
                            val basicMatrix = (matrix.b == 0f && matrix.c == 0f)
                            group = ImageVector.VectorNode.VectorGroup(
                                nodes = emptyList(),
                                scaleX = if (basicMatrix && matrix.a != 1f) matrix.a else null,
                                scaleY = if (basicMatrix && matrix.d != 1f) matrix.d else null,
                                translationX = if (basicMatrix && matrix.e != 0f) matrix.e else null,
                                translationY = if (basicMatrix && matrix.f != 0f) matrix.f else null,
                                currentTransformationMatrix =
                                if (basicMatrix) Matrix(1f, 0f, 0f, 1f, 0f, 0f)
                                else matrix,
                                extra = extra
                            )
                        } else {
                            val parentGroup = groups.last().group
                            val ctm = AffineTransform().apply {
                                concatenate(graphicsNode.transform)
                                concatenate(parentGroup.currentTransformationMatrix.toAffineTransform())
                            }
                            group = ImageVector.VectorNode.VectorGroup(
                                nodes = emptyList(),
                                name = element.xmlId.ifEmpty { null },
                                currentTransformationMatrix = ctm.toMatrix(),
                                clipPathData = clipPathShape?.let {
                                    val transformedClipPathShape =
                                        ctm.createTransformedShape(it)
                                    document.toPathData(transformedClipPathShape)
                                } ?: emptyList(),
                                extra = extra
                            )
                        }
                        groups.add(GroupInfo(element, group, mutableListOf(), mutableSetOf()))
                    }

                    is SVGOMPathElement,
                    is SVGOMRectElement,
                    is SVGOMCircleElement,
                    is SVGOMEllipseElement,
                    is SVGOMLineElement,
                    is SVGOMPolylineElement,
                    is SVGOMPolygonElement,
                    -> {
                        check(element is SVGGraphicsElement)
                        check(graphicsNode != null)
                        val parentGroup = groups.last().group
                        val ctm = AffineTransform().apply {
                            concatenate(graphicsNode.transform)
                            concatenate(parentGroup.currentTransformationMatrix.toAffineTransform())
                        }
                        if (clipPathShape != null) {
                            // Wrap single element by group for clip path
                            val group = ImageVector.VectorNode.VectorGroup(
                                nodes = emptyList(),
                                currentTransformationMatrix = ctm.toMatrix(),
                                clipPathData = ctm.createTransformedShape(clipPathShape).let {
                                    document.toPathData(it)
                                },
                            )
                            groups.add(GroupInfo(element, group))
                        }
                        val fill = element.style.getColor("fill")?.toBrush()
                        val fillAlpha =
                            element.style.getNullablePropertyValue("fill-opacity")?.toFloat()
                        val stroke = element.style.getColor("stroke")?.toBrush()
                        val strokeAlpha =
                            element.style.getNullablePropertyValue("stroke-opacity")?.toFloat()
                        val strokeLineWidth = element.style.getFloatPxValue("stroke-width")
                        val strokeLineCap =
                            element.style.getNullablePropertyValue("stroke-linecap")?.toStrokeCap()
                        val strokeLineJoin =
                            element.style.getNullablePropertyValue("stroke-linejoin")
                                ?.toStrokeJoin()
                        val strokeLineMiter = element.style.getFloatPxValue("stroke-linemiter")
                        var fillId: String? = null
                        var fillAlphaId: String? = null
                        var strokeId: String? = null
                        var strokeAlphaId: String? = null
                        var strokeLineWidthId: String? = null
                        var strokeLineCapId: String? = null
                        var strokeLineJoinId: String? = null
                        var strokeLineMiterId: String? = null
                        groups.reversed().forEach { group ->
                            val extra = group.group.extra
                            if (extra != null) {
                                if (fill == null && fillId == null && extra.fill != null) {
                                    fillId = extra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::fill)
                                }
                                if (fillAlpha == null && fillAlphaId == null && extra.fillAlpha != null) {
                                    fillAlphaId = extra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::fillAlpha)
                                }
                                if (stroke == null && strokeId == null && extra.stroke != null) {
                                    strokeId = extra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::stroke)
                                }
                                if (strokeAlpha == null && strokeAlphaId == null && extra.strokeAlpha != null) {
                                    strokeAlphaId = extra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::strokeAlpha)
                                }
                                if (strokeLineWidth == null && strokeLineWidthId == null && extra.strokeLineWidth != null) {
                                    strokeLineWidthId = extra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::strokeLineWidth)
                                }
                                if (strokeLineCap == null && strokeLineCapId == null && extra.strokeLineCap != null) {
                                    strokeLineCapId = extra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::strokeLineCap)
                                }
                                if (strokeLineJoin == null && strokeLineJoinId == null && extra.strokeLineJoin != null) {
                                    strokeLineJoinId = extra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::strokeLineJoin)
                                }
                                if (strokeLineMiter == null && strokeLineMiterId == null && extra.strokeLineMiter != null) {
                                    strokeLineMiterId = extra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::strokeLineMiter)
                                }
                            }
                        }
                        val extraReference = if ((fillId != null) ||
                            (fillAlphaId != null) ||
                            (strokeId != null) ||
                            (strokeAlphaId != null) ||
                            (strokeLineWidthId != null) ||
                            (strokeLineCapId != null) ||
                            (strokeLineJoinId != null) ||
                            (strokeLineMiterId != null)
                        ) {
                            ImageVector.VectorNode.VectorPath.ExtraReference(
                                fillId = fillId,
                                fillAlphaId = fillAlphaId,
                                strokeId = strokeId,
                                strokeAlphaId = strokeAlphaId,
                                strokeLineWidthId = strokeLineWidthId,
                                strokeLineCapId = strokeLineCapId,
                                strokeLineJoinId = strokeLineJoinId,
                                strokeLineMiterId = strokeLineMiterId,
                            )
                        } else null
                        val shape = (graphicsNode as ShapeNode).shape
                        val pathData = if (ctm.isIdentity) {
                            when (element) {
                                is SVGOMPathElement -> {
                                    // keep original path commands, if possible.
                                    element.pathSegList.toPathData()
                                }
                                else -> {
                                    document.toPathData(shape)
                                }
                            }
                        } else {
                            // path commands are simplified by AWT Shape compatible (AWTPathProducer)
                            val transformedShape = ctm.createTransformedShape(shape)
                            document.toPathData(transformedShape)
                        }
                        groups.last().nodes.add(
                            ImageVector.VectorNode.VectorPath(
                                pathData = pathData,
                                pathFillType = null,
                                name = element.xmlId.ifEmpty { null },
                                fill = fill,
                                fillAlpha = fillAlpha,
                                stroke = stroke,
                                strokeAlpha = strokeAlpha,
                                strokeLineWidth = strokeLineWidth,
                                strokeLineCap = strokeLineCap,
                                strokeLineJoin = strokeLineJoin,
                                strokeLineMiter = strokeLineMiter,
                                trimPathStart = null,
                                trimPathEnd = null,
                                trimPathOffset = null,
                                extraReference = extraReference
                            )
                        )
                        if (clipPathShape != null) {
                            // close clip path group
                            val group = groups.removeLast()
                            val parent = groups.last()
                            parent.nodes.add(
                                group.group.copy(
                                    nodes = group.nodes.toList(),
                                )
                            )
                        }
                    }

                    else -> {}
                }
            },
            onElementEnd = { element ->
                when (element) {
                    is SVGOMSVGElement,
                    is SVGOMGElement,
                    is SVGOMUseElement,
                    -> {
                        val group = groups.removeLast()
                        val parent = groups.lastOrNull()
                        val extra = group.group.extra
                        val properties = group.referencedProperties
                        val referencedExtra = if (extra != null && properties.isNotEmpty()) {
                            ImageVector.VectorNode.VectorGroup.Extra(
                                id = extra.id,
                                fill = if (properties.contains(ImageVector.VectorNode.VectorGroup.Extra::fill)) extra.fill else null,
                                fillAlpha = if (properties.contains(ImageVector.VectorNode.VectorGroup.Extra::fillAlpha)) extra.fillAlpha else null,
                                stroke = if (properties.contains(ImageVector.VectorNode.VectorGroup.Extra::stroke)) extra.stroke else null,
                                strokeAlpha = if (properties.contains(ImageVector.VectorNode.VectorGroup.Extra::strokeAlpha)) extra.strokeAlpha else null,
                                strokeLineWidth = if (properties.contains(ImageVector.VectorNode.VectorGroup.Extra::strokeLineWidth)) extra.strokeLineWidth else null,
                                strokeLineCap = if (properties.contains(ImageVector.VectorNode.VectorGroup.Extra::strokeLineCap)) extra.strokeLineCap else null,
                                strokeLineJoin = if (properties.contains(ImageVector.VectorNode.VectorGroup.Extra::strokeLineJoin)) extra.strokeLineJoin else null,
                                strokeLineMiter = if (properties.contains(ImageVector.VectorNode.VectorGroup.Extra::strokeLineMiter)) extra.strokeLineMiter else null,
                            )
                        } else null
                        if (parent == null) {
                            // root svg group
                            groups.add(
                                group.copy(
                                    group = group.group.copy(
                                        nodes = group.nodes.toList(),
                                        referencedExtra = referencedExtra,
                                    )
                                )
                            )
                        } else {
                            parent.nodes.add(
                                group.group.copy(
                                    nodes = group.nodes.toList(),
                                    referencedExtra = referencedExtra,
                                )
                            )
                        }
                    }

                    else -> {}
                }
            }
        )
        val rootGroup = groups.last().group
        val imageVector = ImageVector(
            name = "IconName",
            defaultWidth = width.toDouble(),
            defaultHeight = height.toDouble(),
            viewportWidth = viewBoxWidth,
            viewportHeight = viewBoxHeight,
            autoMirror = false,
            rootGroup = rootGroup
        )
        return imageVector
    }

    private data class GroupInfo(
        val element: SVGOMElement,
        val group: ImageVector.VectorNode.VectorGroup,
        val nodes: MutableList<ImageVector.VectorNode> = mutableListOf(),
        val referencedProperties: MutableSet<KProperty<*>> = mutableSetOf(),
    )
}

/**
 * Initialize CSSEngine
 *
 * * see https://stackoverflow.com/a/46845740/13403244
 * * see https://github.com/afester/CodeSamples/blob/b3ddb0efdd03713b0adf2d8488fc088dabfeea49/Java/JavaFXSample/src/com/example/svg/SVGDocumentLoader.java#L144
 */
private fun SVGOMDocument.initializeSvgCssEngine(): BridgeContext {
    val userAgent = object: UserAgentAdapter() {
        override fun displayMessage(message: String) {
            println(message)
        }
    }
    val bridgeContext = object: SVG12BridgeContext(
        userAgent, DocumentLoader(userAgent)
    ) {
        override fun getBridgeExtensions(doc: Document?): MutableList<Any?> {
            return super.getBridgeExtensions(doc).apply {
                add(SVGUseElementBridgeHrefFixExtension())
            }
        }
    }.apply {
        setDynamicState(BridgeContext.DYNAMIC)
    }
    ParserCSS3ColorFix.registerParser()
    GVTBuilder().build(bridgeContext, this)
    return bridgeContext
}

private fun SVGOMElement.traverse(
    onElementBegin: (element: SVGOMElement) -> Unit,
    onElementEnd: (element: SVGOMElement) -> Unit,
) {
    (this as? SVGStylableElement)?.mergeStyle()
    if (this is SVGOMUseElement) {
        this.mergeHref()
    }
    val style = (this as? SVGStylableElement)?.style
    val styleDisplayValue = style?.getPropertyValue("display")
    if (styleDisplayValue != "none") {
        onElementBegin(this)
        children().forEach { child ->
            child.traverse(onElementBegin, onElementEnd)
        }
        onElementEnd(this)
    }
}

private fun SVGOMElement.children(): Sequence<SVGOMElement> {
    return sequence {
        var child = if (this@children is SVGOMUseElement) {
            this@children.cssFirstChild as? SVGOMElement
        } else {
            this@children.firstElementChild as? SVGOMElement
        }
        while (child != null) {
            yield(child)
            child = (child.nextElementSibling as? SVGOMElement)
        }
    }
}

private fun AffineTransform.toMatrix(): Matrix {
    return SVGOMMatrix(this).toMatrix()
}

private fun SVGStylableElement.getStyleExtra(
    extraId: String,
): ImageVector.VectorNode.VectorGroup.Extra? {
    var extra: ImageVector.VectorNode.VectorGroup.Extra? = null
    val fill = style.getColor("fill")?.toBrush()
    val fillAlpha = style.getNullablePropertyValue("fill-opacity")?.toFloat()
    val stroke = style.getColor("stroke")?.toBrush()
    val strokeAlpha = style.getNullablePropertyValue("stroke-opacity")?.toFloat()
    val strokeLineWidth = style.getFloatPxValue("stroke-width")
    val strokeLineCap = style.getNullablePropertyValue("stroke-linecap")?.toStrokeCap()
    val strokeLineJoin = style.getNullablePropertyValue("stroke-linejoin")?.toStrokeJoin()
    val strokeLineMiter = style.getNullablePropertyValue("stroke-miterlimit")?.toFloat()
    if ((fill != null) ||
        (fillAlpha != null) ||
        (stroke != null) ||
        (strokeAlpha != null) ||
        (strokeLineWidth != null) ||
        (strokeLineCap != null) ||
        (strokeLineJoin != null) ||
        (strokeLineMiter != null)
    ) {
        extra = ImageVector.VectorNode.VectorGroup.Extra(
            id = extraId,
            fill = fill,
            fillAlpha = fillAlpha,
            stroke = stroke,
            strokeAlpha = strokeAlpha,
            strokeLineWidth = strokeLineWidth,
            strokeLineCap = strokeLineCap,
            strokeLineJoin = strokeLineJoin,
            strokeLineMiter = strokeLineMiter,
        )
    }
    return extra
}

/**
 * Convert String to Color
 * * `#FF000000` => #FF000000
 * * `#FFFFFFF` => #0F000000
 * * `#FFFFFF` => #FF000000
 * * `rgb(0, 0, 0)` => #FF000000
 * * `rgb(0.0%, 0.0%, 0.0%)` => #FF000000
 */
@Deprecated("unused")
private fun String.toColor(): ImageVector.Color {
    val trimmed = replace(" ", "")
    val group = """^rgb\((.*)\)$""".toRegex().matchEntire(trimmed)?.groups
    val hex = if (group != null) {
        val values = group[1]?.value?.split(",".toRegex())
        if (values?.size != 3) {
            error("invalid color string: $this")
        }
        buildString {
            values.forEach { valueString ->
                val color = if (valueString.endsWith("%")) {
                    val value = valueString.substring(0, valueString.length - 1).toFloat()
                    (value * 255.0f / 100.0f).toInt().coerceIn(0, 255)
                } else {
                    valueString.toInt().coerceIn(0, 255)
                }
                append("%02X".format(color))
            }
            if (values.size <= 3) {
                append("FF")
            }
        }
    } else {
        trimmed.replace("^#".toRegex(), "")
            .let {
                if ("^[0-9a-fA-F]{6,8}$".toRegex().matches(it)) {
                    if (it.length == 6) "${it}FF" else it
                } else {
                    "000000FF"
                }
            }
    }
    return ImageVector.RgbColor(
        red = hex.substring(0, 1).toInt(16),
        green = hex.substring(2, 3).toInt(16),
        blue = hex.substring(4, 5).toInt(16),
        alpha = hex.substring(6, 7).toInt(16),
    )
}

private fun ImageVector.Color.toBrush(): ImageVector.Brush {
    return ImageVector.Brush.SolidColor(this)
}

private fun CSSStyleDeclaration.getNullablePropertyValue(name: String): String? {
    return getPropertyValue(name).ifEmpty { null }
}

private fun CSSStyleDeclaration.getColor(name: String): ImageVector.Color? {
    val cssValue = (getPropertyCSSValue(name) as? CSSOMSVGStyleDeclaration.StyleDeclarationPaintValue)
    val value = cssValue?.value
    return when {
        (value is RGBAColorValue) -> {
            ImageVector.RgbColor(
                red = value.red.getColorValue(),
                green = value.green.getColorValue(),
                blue = value.blue.getColorValue(),
                alpha = value.alpha.getColorValue(),
            )
        }

        (value?.primitiveType == CSSPrimitiveValue.CSS_RGBCOLOR) -> {
            ImageVector.RgbColor(
                red = value.red.getColorValue(),
                green = value.green.getColorValue(),
                blue = value.blue.getColorValue(),
            )
        }

        (value?.primitiveType == CSSPrimitiveValue.CSS_IDENT) -> {
            // color name
            val colorName = value.stringValue.lowercase()
            val colorNameLowercase = value.stringValue.lowercase()
            if (colorNameLowercase == "transparent") {
                ImageVector.Transparent
            } else {
                val color = when (colorNameLowercase) {
                    "aqua" -> SVGColor.aqua
                    "black" -> SVGColor.black
                    "blue" -> SVGColor.blue
                    "fuchsia" -> SVGColor.fuchsia
                    "gray" -> SVGColor.gray
                    "green" -> SVGColor.green
                    "lime" -> SVGColor.lime
                    "maroon" -> SVGColor.maroon
                    "navy" -> SVGColor.navy
                    "olive" -> SVGColor.olive
                    "purple" -> SVGColor.purple
                    "red" -> SVGColor.red
                    "silver" -> SVGColor.silver
                    "teal" -> SVGColor.teal
                    "white" -> SVGColor.white
                    "yellow" -> SVGColor.yellow
                    else -> error("unknown color name: $colorName")
                }
                ImageVector.RgbColor(
                    red = color.red,
                    green = color.green,
                    blue = color.blue,
                    alpha = color.alpha
                )
            }
        }

        else -> null
    }
}

private fun Value.getColorValue(): Int {
    return if (primitiveType == CSSPrimitiveValue.CSS_PERCENTAGE) {
        val percentage = floatValue
        (percentage * 255f / 100f).toInt()
    } else {
        floatValue.toInt()
    }
}

private fun CSSStyleDeclaration.getFloatPxValue(name: String): Float? {
    return (getPropertyCSSValue(name) as? CSSPrimitiveValue)?.getFloatValue(CSSPrimitiveValue.CSS_PX)
}

private fun String.toStrokeCap(): ImageVector.StrokeCap? {
    return if (equals("butt", ignoreCase = true)) {
        ImageVector.StrokeCap.Butt
    } else if (equals("round", ignoreCase = true)) {
        ImageVector.StrokeCap.Round
    } else if (equals("square", ignoreCase = true)) {
        ImageVector.StrokeCap.Square
    } else if (equals("inherit", ignoreCase = true)) {
        null
    } else {
        error("invalid stroke-linecap: $this")
    }
}

private fun String.toStrokeJoin(): ImageVector.StrokeJoin? {
    return if (equals("miter", ignoreCase = true)) {
        ImageVector.StrokeJoin.Miter
    } else if (equals("round", ignoreCase = true)) {
        ImageVector.StrokeJoin.Round
    } else if (equals("bevel", ignoreCase = true)) {
        ImageVector.StrokeJoin.Bevel
    } else if (equals("inherit", ignoreCase = true)) {
        null
    } else {
        error("invalid stroke-linejoin: $this")
    }
}

private fun LexicalUnit.sequence(): Sequence<LexicalUnit> {
    val receiver = this@sequence
    return sequence {
        var current: LexicalUnit? = receiver
        while (current != null) {
            yield(current)
            current = current.nextLexicalUnit
        }
    }
}

/**
 * merge SVG presentation attributes to style attributes
 */
private fun SVGStylableElement.mergeStyle() {
    listOf(
        "display",
        "stroke",
        "stroke-opacity",
        "stroke-width",
        "stroke-linecap",
        "stroke-linejoin",
        "stroke-miterlimit",
        "fill",
        "fill-opacity",
    ).forEach { name ->
        val attributeValue = (attributes.getNamedItem(name) as? GenericAttr)?.value
        if (style.getPropertyValue(name).isEmpty() && attributeValue != null) {
            style.setProperty(name, attributeValue, "")
        }
    }
    setAttribute("style", style.cssText)
}

/**
 * merge "xlink:href" to "href"
 */
private fun SVGOMUseElement.mergeHref() {
    val href = getAttribute("href").ifEmpty { null }
    if (href == null) {
        val xlinkHref = getAttributeNS("http://www.w3.org/1999/xlink", "href")
        setAttribute("href", xlinkHref)
    }
}

/**
 * needs public, because this class will be registered to "org.w3c.css.sac.parser"
 */
class ParserCSS3ColorFix: Parser() {
    companion object {
        fun registerParser() {
            XMLResourceDescriptor.setCSSParserClassName(ParserCSS3ColorFix::class.java.canonicalName)
        }
    }

    override fun parseFunction(positive: Boolean, prev: LexicalUnit?): LexicalUnit {
        val name = scanner.stringValue
        return if (name.equals("rgb", ignoreCase = true) ||
            name.equals("rgba", ignoreCase = true)) {
            // Support
            //   rgb(0 0 0), rgb(0 0 0 0), rgb(0 0 0 / 0)
            //   rgba(0 0 0), rgba(0 0 0 0), rgba(0 0 0 / 0)
            // Convert parameters to comma separated format (e.g. "0,0,0,0")
            nextIgnoreSpaces()
            val params = parseExpression(true)
            if (
                current != LexicalUnits.RIGHT_BRACE) {
                throw createCSSParseException("token", arrayOf<Any>(current))
            }
            nextIgnoreSpaces()
            var p: LexicalUnit? = params
            while (p != null) {
                when (p.nextLexicalUnit?.lexicalUnitType) {
                    null -> {
                        p = null
                    }

                    LexicalUnit.SAC_OPERATOR_COMMA -> {
                        // move next value
                        p = p.nextLexicalUnit?.nextLexicalUnit
                    }

                    LexicalUnit.SAC_OPERATOR_SLASH -> {
                        // replace comma
                        val next = (p.nextLexicalUnit?.nextLexicalUnit as? CSSLexicalUnit)
                        CSSLexicalUnit.createSimple(LexicalUnit.SAC_OPERATOR_COMMA, p).let {
                            next?.previousLexicalUnit = it
                            it.nextLexicalUnit = next
                        }
                        p = next
                    }

                    else -> {
                        // insert comma
                        val next = (p.nextLexicalUnit as CSSLexicalUnit)
                        CSSLexicalUnit.createSimple(LexicalUnit.SAC_OPERATOR_COMMA, p).let {
                            next.previousLexicalUnit = it
                            it.nextLexicalUnit = next
                        }
                        p = next
                    }
                }
            }
            val count = params.sequence().count {
                (it.lexicalUnitType != LexicalUnit.SAC_OPERATOR_COMMA)
            }
            if (count == 3) {
                CSSLexicalUnit.createPredefinedFunction(LexicalUnit.SAC_RGBCOLOR, params, prev)
            } else {
                CSSLexicalUnit.createFunction("rgba", params, prev)
            }
        } else {
            super.parseFunction(positive, prev)
        }
    }

    override fun hexcolor(prev: LexicalUnit?): LexicalUnit {
        val value = scanner.stringValue
        val length = value.length
        var result: LexicalUnit? = if (length == 4 || length == 8) {
            // Support 4 digits and 8 digits hex color
            // https://www.w3.org/TR/css-color-4/#hex-notation
            try {
                var r: Int
                var g: Int
                var b: Int
                var a: Int
                if (length == 4) {
                    r = value[0].digitToInt(16)
                    g = value[1].digitToInt(16)
                    b = value[2].digitToInt(16)
                    a = value[3].digitToInt(16)
                    r = (r shl 4) or r
                    g = (g shl 4) or g
                    b = (b shl 4) or b
                    a = (a shl 4) or a
                } else {
                    // len == 8
                    r = value.substring(0, 2).toInt(16)
                    g = value.substring(2, 4).toInt(16)
                    b = value.substring(4, 6).toInt(16)
                    a = value.substring(6, 8).toInt(16)
                }
                val params = CSSLexicalUnit.createInteger(r, null)
                var chain = CSSLexicalUnit.createSimple(LexicalUnit.SAC_OPERATOR_COMMA, params)
                chain = CSSLexicalUnit.createInteger(g, chain)
                chain = CSSLexicalUnit.createSimple(LexicalUnit.SAC_OPERATOR_COMMA, chain)
                chain = CSSLexicalUnit.createInteger(b, chain)
                chain = CSSLexicalUnit.createSimple(LexicalUnit.SAC_OPERATOR_COMMA, chain)
                CSSLexicalUnit.createInteger(a, chain)
                CSSLexicalUnit.createFunction("rgba", params, prev)
            } catch (error: NumberFormatException) {
                throw createCSSParseException("rgb.color")
            }
        } else null
        if (result != null) {
            nextIgnoreSpaces()
        } else {
            result = super.hexcolor(prev)!!
        }
        return result
    }
}

class SAXSVGDocumentFactoryCSS3ColorFix(
    parser: String?,
): SAXSVGDocumentFactory(parser) {
    override fun getDOMImplementation(ver: String?): DOMImplementation {
        return SVG120DOMImplementationCSS3ColorFix()
    }
}

class SVG120DOMImplementationCSS3ColorFix: SVG12DOMImplementation() {
    override fun createCSSEngine(
        doc: AbstractStylableDocument,
        ctx: CSSContext,
        ep: ExtendedParser,
        vms: Array<out ValueManager>,
        sms: Array<out ShorthandManager>,
    ): CSSEngine {
        return super.createCSSEngine(doc, ctx, ep, vms, sms).apply {
            val managers = this.valueManagers
            managers.indices.forEach { index ->
                val manager = managers[index]
                if (manager is AbstractColorManager) {
                    managers[index] = AbstractColorManagerCSS3ColorFix(manager)
                }
            }
        }
    }
}

class AbstractColorManagerCSS3ColorFix(
    private val original: AbstractColorManager,
): AbstractColorManager() {
    init {
        values.put("transparent", StringValue(CSSPrimitiveValue.CSS_IDENT, "transparent"))
        computedValues.put("transparent", RGBAColorValue(NUMBER_0, NUMBER_0, NUMBER_0, NUMBER_0))
    }

    override fun createValue(lu: LexicalUnit, engine: CSSEngine): Value {
        return if ((lu.lexicalUnitType == LexicalUnit.SAC_FUNCTION) &&
            lu.functionName.equals("rgba", ignoreCase = true)) {
            var p = lu.parameters
            val red = createColorComponent(p)
            p = p.nextLexicalUnit.nextLexicalUnit
            val green = createColorComponent(p)
            p = p.nextLexicalUnit.nextLexicalUnit
            val blue = createColorComponent(p)
            p = p.nextLexicalUnit.nextLexicalUnit
            val alpha = createColorComponent(p)
            return RGBAColorValue(red, green, blue, alpha)
        } else original.createValue(lu, engine)
    }

    override fun computeValue(
        elt: CSSStylableElement?,
        pseudo: String?,
        engine: CSSEngine?,
        idx: Int,
        sm: StyleMap?,
        value: Value?,
    ): Value = original.computeValue(elt, pseudo, engine, idx, sm, value)

    override fun getPropertyName(): String = original.propertyName

    override fun isInheritedProperty(): Boolean = original.isInheritedProperty

    override fun isAnimatableProperty(): Boolean = original.isAnimatableProperty

    override fun isAdditiveProperty(): Boolean = original.isAdditiveProperty

    override fun getPropertyType(): Int = original.propertyType

    override fun getDefaultValue(): Value = original.defaultValue
}

class RGBAColorValue(
    private val r: Value,
    private val g: Value,
    private val b: Value,
    private val a: Value,
) : AbstractValue() {
    override fun getPrimitiveType(): Short {
        return CSSPrimitiveValue.CSS_RGBCOLOR
    }

    override fun getCssText(): String {
        return "rgba(${r.cssText}, ${g.cssText}, ${b.cssText}, ${a.cssText})"
    }

    override fun getRed(): Value {
        return r
    }

    override fun getGreen(): Value {
        return g
    }

    override fun getBlue(): Value {
        return b
    }

    val alpha: Value get() = a

    override fun toString(): String {
        return cssText
    }
}

fun SVGMatrix.toMatrix(): ImageVector.Matrix {
    return ImageVector.Matrix(
        a = a,
        b = b,
        c = c,
        d = d,
        e = e,
        f = f
    )
}

fun ImageVector.Matrix.toAffineTransform(): AffineTransform {
    return AffineTransform(
        a,
        b,
        c,
        d,
        e,
        f,
    )
}

fun Document.toPathData(shape: Shape): List<ImageVector.PathNode> {
    val pathString = SVGPath.toSVGPathData(shape, SVGGeneratorContext.createDefault(this))
    return PathDataPathHandler().also {
        PathParser().apply {
            pathHandler = it
        }.parse(pathString)
    }.getPath()
}

fun SVGPathSegList.toPathData(): List<ImageVector.PathNode> {
    return PathDataPathHandler().also {
        SVGAnimatedPathDataSupport.handlePathSegList(this, it)
    }.getPath()
}

class PathDataPathHandler: PathHandler {
    private val pathData = mutableListOf<ImageVector.PathNode>()

    fun getPath(): List<ImageVector.PathNode> {
        return pathData.toList()
    }

    override fun startPath() = Unit

    override fun endPath() = Unit

    override fun movetoRel(x: Float, y: Float) {
        pathData.add(
            ImageVector.PathNode.RelativeMoveTo(
                dx = x,
                dy = y
            )
        )
    }

    override fun movetoAbs(x: Float, y: Float) {
        pathData.add(
            ImageVector.PathNode.MoveTo(
                x = x,
                y = y
            )
        )
    }

    override fun closePath() {
        pathData.add(
            ImageVector.PathNode.Close
        )
    }

    override fun linetoRel(x: Float, y: Float) {
        pathData.add(
            ImageVector.PathNode.RelativeLineTo(
                dx = x,
                dy = y
            )
        )
    }

    override fun linetoAbs(x: Float, y: Float) {
        pathData.add(
            ImageVector.PathNode.LineTo(
                x = x,
                y = y
            )
        )
    }

    override fun linetoHorizontalRel(x: Float) {
        pathData.add(
            ImageVector.PathNode.RelativeHorizontalTo(
                dx = x,
            )
        )
    }

    override fun linetoHorizontalAbs(x: Float) {
        pathData.add(
            ImageVector.PathNode.HorizontalTo(
                x = x,
            )
        )
    }

    override fun linetoVerticalRel(y: Float) {
        pathData.add(
            ImageVector.PathNode.RelativeVerticalTo(
                dy = y,
            )
        )
    }

    override fun linetoVerticalAbs(y: Float) {
        pathData.add(
            ImageVector.PathNode.VerticalTo(
                y = y,
            )
        )
    }

    override fun curvetoCubicRel(x1: Float, y1: Float, x2: Float, y2: Float, x: Float, y: Float) {
        pathData.add(
            ImageVector.PathNode.RelativeCurveTo(
                dx1 = x1,
                dy1 = y1,
                dx2 = x2,
                dy2 = y2,
                dx3 = x,
                dy3 = y,
            )
        )
    }

    override fun curvetoCubicAbs(x1: Float, y1: Float, x2: Float, y2: Float, x: Float, y: Float) {
        pathData.add(
            ImageVector.PathNode.CurveTo(
                x1 = x1,
                y1 = y1,
                x2 = x2,
                y2 = y2,
                x3 = x,
                y3 = y,
            )
        )
    }

    override fun curvetoCubicSmoothRel(x2: Float, y2: Float, x: Float, y: Float) {
        pathData.add(
            ImageVector.PathNode.RelativeReflectiveCurveTo(
                dx1 = x2,
                dy1 = y2,
                dx2 = x,
                dy2 = y,
            )
        )
    }

    override fun curvetoCubicSmoothAbs(x2: Float, y2: Float, x: Float, y: Float) {
        pathData.add(
            ImageVector.PathNode.ReflectiveCurveTo(
                x1 = x2,
                y1 = y2,
                x2 = x,
                y2 = y,
            )
        )
    }

    override fun curvetoQuadraticRel(x1: Float, y1: Float, x: Float, y: Float) {
        pathData.add(
            ImageVector.PathNode.RelativeQuadTo(
                dx1 = x1,
                dy1 = y1,
                dx2 = x,
                dy2 = y,
            )
        )
    }

    override fun curvetoQuadraticAbs(x1: Float, y1: Float, x: Float, y: Float) {
        pathData.add(
            ImageVector.PathNode.QuadTo(
                x1 = x1,
                y1 = y1,
                x2 = x,
                y2 = y,
            )
        )
    }

    override fun curvetoQuadraticSmoothRel(x: Float, y: Float) {
        pathData.add(
            ImageVector.PathNode.RelativeReflectiveQuadTo(
                dx = x,
                dy = y,
            )
        )
    }

    override fun curvetoQuadraticSmoothAbs(x: Float, y: Float) {
        pathData.add(
            ImageVector.PathNode.ReflectiveQuadTo(
                x = x,
                y = y,
            )
        )
    }

    override fun arcRel(
        rx: Float,
        ry: Float,
        xAxisRotation: Float,
        largeArcFlag: Boolean,
        sweepFlag: Boolean,
        x: Float,
        y: Float,
    ) {
        pathData.add(
            ImageVector.PathNode.RelativeArcTo(
                horizontalEllipseRadius = rx,
                verticalEllipseRadius = ry,
                theta = xAxisRotation,
                isMoreThanHalf = largeArcFlag,
                isPositiveArc = sweepFlag,
                arcStartDx = x,
                arcStartDy = y,
            )
        )
    }

    override fun arcAbs(
        rx: Float,
        ry: Float,
        xAxisRotation: Float,
        largeArcFlag: Boolean,
        sweepFlag: Boolean,
        x: Float,
        y: Float,
    ) {
        pathData.add(
            ImageVector.PathNode.ArcTo(
                horizontalEllipseRadius = rx,
                verticalEllipseRadius = ry,
                theta = xAxisRotation,
                isMoreThanHalf = largeArcFlag,
                isPositiveArc = sweepFlag,
                arcStartX = x,
                arcStartY = y,
            )
        )
    }
}

/**
 * use href attribute
 */
class SVGUseElementBridgeHrefFix: SVGUseElementBridge() {
    override fun getInstance(): Bridge {
        return SVGUseElementBridgeHrefFix()
    }

    override fun buildCompositeGraphicsNode(
        ctx: BridgeContext,
        e: Element,
        gn: CompositeGraphicsNode?,
    ): CompositeGraphicsNode {
        val href = e.getAttribute("href").ifEmpty { null }
        if (href != null) {
            e.setAttributeNS("http://www.w3.org/1999/xlink", "href", href)
        }
        return super.buildCompositeGraphicsNode(ctx, e, gn)
    }
}

class SVGUseElementBridgeHrefFixExtension: BridgeExtension {
    override fun getPriority(): Float = 0f

    override fun getImplementedExtensions(): MutableIterator<Any?> = Collections.EMPTY_LIST.iterator()

    override fun getAuthor(): String = ""

    override fun getContactAddress(): String = ""

    override fun getURL(): String = ""

    override fun getDescription(): String = ""

    override fun registerTags(ctx: BridgeContext) {
        ctx.putBridge(SVGUseElementBridgeHrefFix())
    }

    override fun isDynamicElement(e: Element): Boolean = false
}
