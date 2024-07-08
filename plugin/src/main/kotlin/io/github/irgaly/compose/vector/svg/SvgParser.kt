package io.github.irgaly.compose.vector.svg

import com.steadystate.css.dom.CSSValueImpl
import com.steadystate.css.parser.CSSOMParser
import com.steadystate.css.parser.LexicalUnitImpl
import com.steadystate.css.parser.SACParserCSS3
import com.steadystate.css.parser.Token
import io.github.irgaly.compose.vector.node.ImageVector
import io.github.irgaly.compose.vector.node.ImageVector.Brush
import io.github.irgaly.compose.vector.node.ImageVector.StrokeCap
import io.github.irgaly.compose.vector.node.ImageVector.StrokeJoin
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGOMAnimatedLength
import org.apache.batik.anim.dom.SVGOMAnimatedRect
import org.apache.batik.anim.dom.SVGOMDocument
import org.apache.batik.anim.dom.SVGOMElement
import org.apache.batik.anim.dom.SVGOMGElement
import org.apache.batik.anim.dom.SVGOMPathElement
import org.apache.batik.anim.dom.SVGOMSVGElement
import org.apache.batik.anim.dom.SVGStylableElement
import org.apache.batik.bridge.BridgeContext
import org.apache.batik.bridge.DocumentLoader
import org.apache.batik.bridge.GVTBuilder
import org.apache.batik.bridge.UserAgentAdapter
import org.apache.batik.dom.GenericAttr
import org.apache.batik.svggen.SVGColor
import org.apache.batik.util.XMLResourceDescriptor
import org.w3c.css.sac.CSSParseException
import org.w3c.css.sac.ErrorHandler
import org.w3c.css.sac.InputSource
import org.w3c.css.sac.LexicalUnit
import org.w3c.dom.css.CSSPrimitiveValue
import org.w3c.dom.css.CSSPrimitiveValue.CSS_PERCENTAGE
import org.w3c.dom.css.CSSPrimitiveValue.CSS_PX
import org.w3c.dom.css.CSSStyleDeclaration
import java.io.IOException
import java.io.InputStream
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
            SAXSVGDocumentFactory(
                XMLResourceDescriptor.getXMLParserClassName()
            ).createDocument("xml", input) as SVGOMDocument
        } catch (error: IOException) {
            throw error
        }.apply {
            initializeSvgCssEngine()
        }
        val svg = (document.rootElement as SVGOMSVGElement).apply {
            mergeStyle()
        }
        val viewBox = (svg.viewBox as SVGOMAnimatedRect)
        val viewBoxX = if (viewBox.isSpecified) svg.viewBox.baseVal.x else 0f
        val viewBoxY = if (viewBox.isSpecified) svg.viewBox.baseVal.y else 0f
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
        // TODO: useタグに対応する
        // * id付きのタグをfunで定義して使い回す
        var extraId: Long = 0
        val rootExtra = svg.getStyleExtra(extraId = extraId.toString())
        if (rootExtra != null) {
            extraId++
        }
        var rootGroup = ImageVector.VectorNode.VectorGroup(
            nodes = emptyList(),
            // translate by viewBoxX
            translationX = if (viewBoxX != 0f) -viewBoxX else null,
            // translate by viewBoxY
            translationY = if (viewBoxY != 0f) -viewBoxY else null,
            extra = rootExtra
        )
        val groups = mutableListOf(Triple(rootGroup, mutableListOf<ImageVector.VectorNode>(), mutableSetOf<KProperty<*>>()))
        svg.traverse(
            onElementBegin = { element ->
                when (element) {
                    is SVGOMGElement -> {
                        val extra = element.getStyleExtra(extraId = extraId.toString())
                        if (extra != null) {
                            extraId++
                        }
                        val group = ImageVector.VectorNode.VectorGroup(
                            nodes = emptyList(),
                            name = element.xmlId.ifEmpty { null },
                            // TODO: implement translation
                            // TODO: implement clipPath
                            extra = extra
                        )
                        groups.add(Triple(group, mutableListOf(), mutableSetOf()))
                    }

                    is SVGOMPathElement -> {
                        val style = element.getStyleDeclaration()
                        val fill = style.getColorValue("fill")?.toBrush()
                        val fillAlpha = style.getNullablePropertyValue("fill-opacity")?.toFloat()
                        val stroke = style.getColorValue("stroke")?.toBrush()
                        val strokeAlpha = style.getNullablePropertyValue("stroke-opacity")?.toFloat()
                        val strokeLineWidth = style.getFloatPxValue("stroke-width")
                        val strokeLineCap = style.getNullablePropertyValue("stroke-linecap")?.toStrokeCap()
                        val strokeLineJoin = style.getNullablePropertyValue("stroke-linejoin")?.toStrokeJoin()
                        var fillId: String? = null
                        var fillAlphaId: String? = null
                        var strokeId: String? = null
                        var strokeAlphaId: String? = null
                        var strokeLineWidthId: String? = null
                        var strokeLineCapId: String? = null
                        var strokeLineJoinId: String? = null
                        groups.reversed().forEach { group ->
                            val extra = group.first.extra
                            if (extra != null) {
                                if (fill == null && fillId == null && extra.fill != null) {
                                    fillId = extra.id
                                    group.third.add(ImageVector.VectorNode.VectorGroup.Extra::fill)
                                }
                                if (fillAlpha == null && fillAlphaId == null && extra.fillAlpha != null) {
                                    fillAlphaId = extra.id
                                    group.third.add(ImageVector.VectorNode.VectorGroup.Extra::fillAlpha)
                                }
                                if (stroke == null && strokeId == null && extra.stroke != null) {
                                    strokeId = extra.id
                                    group.third.add(ImageVector.VectorNode.VectorGroup.Extra::stroke)
                                }
                                if (strokeAlpha == null && strokeAlphaId == null && extra.strokeAlpha != null) {
                                    strokeAlphaId = extra.id
                                    group.third.add(ImageVector.VectorNode.VectorGroup.Extra::strokeAlpha)
                                }
                                if (strokeLineWidth == null && strokeLineWidthId == null && extra.strokeLineWidth != null) {
                                    strokeLineWidthId = extra.id
                                    group.third.add(ImageVector.VectorNode.VectorGroup.Extra::strokeLineWidth)
                                }
                                if (strokeLineCap == null && strokeLineCapId == null && extra.strokeLineCap != null) {
                                    strokeLineCapId = extra.id
                                    group.third.add(ImageVector.VectorNode.VectorGroup.Extra::strokeLineCap)
                                }
                                if (strokeLineJoin == null && strokeLineJoinId == null && extra.strokeLineJoin != null) {
                                    strokeLineJoinId = extra.id
                                    group.third.add(ImageVector.VectorNode.VectorGroup.Extra::strokeLineJoin)
                                }
                            }
                        }
                        val extraReference = if ((fillId != null) ||
                            (fillAlphaId != null) ||
                            (strokeId != null) ||
                            (strokeAlphaId != null) ||
                            (strokeLineWidthId != null) ||
                            (strokeLineCapId != null) ||
                            (strokeLineJoinId != null)) {
                            ImageVector.VectorNode.VectorPath.ExtraReference(
                                fillId = fillId,
                                fillAlphaId = fillAlphaId,
                                strokeId = strokeId,
                                strokeAlphaId = strokeAlphaId,
                                strokeLineWidthId = strokeLineWidthId,
                                strokeLineCapId = strokeLineCapId,
                                strokeLineJoinId = strokeLineJoinId,
                            )
                        } else null
                        groups.last().second.add(
                            ImageVector.VectorNode.VectorPath(
                                pathData = emptyList(),
                                pathFillType = null,
                                name = element.xmlId.ifEmpty { null },
                                fill = fill,
                                fillAlpha = fillAlpha,
                                stroke = stroke,
                                strokeAlpha = strokeAlpha,
                                strokeLineWidth = strokeLineWidth,
                                strokeLineCap = strokeLineCap,
                                strokeLineJoin = strokeLineJoin,
                                strokeLineMiter = null,
                                trimPathStart = null,
                                trimPathEnd = null,
                                trimPathOffset = null,
                                extraReference = extraReference
                            )
                        )
                    }

                    else -> {}
                }
            },
            onElementEnd = { element ->
                when (element) {
                    is SVGOMGElement -> {
                        val group = groups.removeLast()
                        val parent = groups.last()
                        val extra = group.first.extra
                        val properties = group.third
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
                            )
                        } else null
                        parent.second.add(
                            group.first.copy(
                                nodes = group.second.toList(),
                                referencedExtra = referencedExtra,
                            )
                        )
                    }

                    is SVGOMPathElement -> {

                    }

                    else -> {}
                }
            }
        )
        groups.last().second.add(
            ImageVector.VectorNode.VectorGroup(
                listOf(
                    ImageVector.VectorNode.VectorGroup(
                        emptyList(),
                        "group2",
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        listOf(
                            ImageVector.PathNode.HorizontalTo(10f),
                            ImageVector.PathNode.VerticalTo(99f)
                        )
                    ),
                    ImageVector.VectorNode.VectorPath(
                        listOf(
                            ImageVector.PathNode.HorizontalTo(10f),
                            ImageVector.PathNode.VerticalTo(99f)
                        ),
                        ImageVector.PathFillType.EvenOdd,
                        "path1",
                        ImageVector.Brush.SolidColor(ImageVector.RgbColor(0x00, 0x00, 0x00)),
                        1f,
                        ImageVector.Brush.SolidColor(ImageVector.RgbColor(0x00, 0x00, 0x00)),
                        1f,
                        1f,
                        ImageVector.StrokeCap.Round,
                        ImageVector.StrokeJoin.Round,
                        1f,
                        0f,
                        0f,
                        0f
                    )
                ),
                "group1",
                0f,
                0f,
                0f,
                0f,
                0f,
                0f,
                0f,
                listOf(
                    ImageVector.PathNode.HorizontalTo(10f),
                    ImageVector.PathNode.VerticalTo(99f)
                ),
            )
        )
        groups.last().second.add(
            ImageVector.VectorNode.VectorPath(
                listOf(
                    ImageVector.PathNode.HorizontalTo(10f),
                    ImageVector.PathNode.VerticalTo(99f)
                ),
                ImageVector.PathFillType.EvenOdd,
                "path2",
                ImageVector.Brush.SolidColor(ImageVector.RgbColor(0x00, 0x00, 0x00)),
                1f,
                ImageVector.Brush.SolidColor(ImageVector.RgbColor(0x00, 0x00, 0x00)),
                1f,
                1f,
                ImageVector.StrokeCap.Round,
                ImageVector.StrokeJoin.Round,
                1f,
                0f,
                0f,
                0f
            )
        )
        val extra = rootGroup.extra
        val properties = groups.last().third
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
            )
        } else null
        rootGroup = groups.last().first.copy(
            nodes = groups.last().second,
            referencedExtra = referencedExtra
        )
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
}

/**
 * Initialize CSSEngine
 *
 * * see https://stackoverflow.com/a/46845740/13403244
 * * see https://github.com/afester/CodeSamples/blob/b3ddb0efdd03713b0adf2d8488fc088dabfeea49/Java/JavaFXSample/src/com/example/svg/SVGDocumentLoader.java#L144
 */
private fun SVGOMDocument.initializeSvgCssEngine() {
    val userAgent = UserAgentAdapter()
    val bridgeContext = BridgeContext(
        userAgent, DocumentLoader(userAgent)
    ).apply {
        setDynamicState(BridgeContext.DYNAMIC)
    }
    GVTBuilder().build(bridgeContext, this)
}

private fun SVGOMElement.traverse(
    onElementBegin: (element: SVGOMElement) -> Unit,
    onElementEnd: (element: SVGOMElement) -> Unit,
) {
    var child = (this.firstElementChild as? SVGOMElement)
    while (child != null) {
        (child as? SVGStylableElement)?.mergeStyle()
        val style = (child as? SVGStylableElement)?.style
        val styleDisplayValue = style?.getPropertyValue("display")
        if (styleDisplayValue != "none") {
            onElementBegin(child)
            child.traverse(onElementBegin, onElementEnd)
            onElementEnd(child)
        }
        child = (child.nextElementSibling as? SVGOMElement)
    }
}

private fun SVGStylableElement.getStyleExtra(
    extraId: String,
): ImageVector.VectorNode.VectorGroup.Extra? {
    var extra: ImageVector.VectorNode.VectorGroup.Extra? = null
    val style = getStyleDeclaration()
    val fill = style.getColorValue("fill")?.toBrush()
    val fillAlpha = style.getNullablePropertyValue("fill-opacity")?.toFloat()
    val stroke = style.getColorValue("stroke")?.toBrush()
    val strokeAlpha = style.getNullablePropertyValue("stroke-opacity")?.toFloat()
    val strokeLineWidth = style.getFloatPxValue("stroke-width")
    val strokeLineCap = style.getNullablePropertyValue("stroke-linecap")?.toStrokeCap()
    val strokeLineJoin = style.getNullablePropertyValue("stroke-linejoin")?.toStrokeJoin()
    if (fill != null ||
        fillAlpha != null ||
        stroke != null ||
        strokeAlpha != null ||
        strokeLineWidth != null ||
        strokeLineCap != null ||
        strokeLineJoin != null
    ) {
        extra = ImageVector.VectorNode.VectorGroup.Extra(
            id = extraId,
            fill = fill,
            fillAlpha = fillAlpha,
            stroke = stroke,
            strokeAlpha = strokeAlpha,
            strokeLineWidth = strokeLineWidth,
            strokeLineCap = strokeLineCap,
            strokeLineJoin = strokeLineJoin
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
            append("FF")
            values.forEach { valueString ->
                val color = if (valueString.endsWith("%")) {
                    val value = valueString.substring(0, valueString.length - 1).toFloat()
                    (value * 255.0f / 100.0f).toInt().coerceIn(0, 255)
                } else {
                    valueString.toInt().coerceIn(0, 255)
                }
                append("%02X".format(color))
            }
        }
    } else {
        trimmed.replace("^#".toRegex(), "")
            .let {
                if ("^[0-9a-fA-F]{6,8}$".toRegex().matches(it)) {
                    when (it.length) {
                        6 -> "FF$it"
                        7 -> "0$it"
                        else -> it
                    }
                } else {
                    "FF000000"
                }
            }
    }
    return ImageVector.RgbColor(0, 0, 0)
}

private fun ImageVector.Color.toBrush(): ImageVector.Brush {
    return ImageVector.Brush.SolidColor(this)
}

private fun CSSStyleDeclaration.getNullablePropertyValue(name: String): String? {
    return getPropertyValue(name).ifEmpty { null }
}

private fun CSSPrimitiveValue.getColorValue(): Int {
    return if (primitiveType == CSS_PERCENTAGE) {
        val percentage = getFloatValue(CSS_PERCENTAGE)
        (percentage * 255f / 100f).toInt()
    } else {
        getFloatValue(primitiveType).toInt()
    }
}

private fun CSSStyleDeclaration.getColorValue(name: String): ImageVector.Color? {
    val cssValue = (getPropertyCSSValue(name) as? CSSValueImpl)
    val unitValue = (cssValue?.value as? LexicalUnit)
    return when {
        (cssValue?.primitiveType == CSSPrimitiveValue.CSS_IDENT) -> {
            // color name
            val colorName = cssValue.stringValue.lowercase()
            val colorNameLowercase = cssValue.stringValue.lowercase()
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

        (cssValue?.primitiveType == CSSPrimitiveValue.CSS_RGBCOLOR) -> {
            val rgb = cssValue.rgbColorValue
            ImageVector.RgbColor(
                red = rgb.red.getColorValue(),
                green = rgb.green.getColorValue(),
                blue = rgb.blue.getColorValue(),
            )
        }

        (unitValue != null && unitValue.functionName.equals("rgba", ignoreCase = true)) -> {
            val colors = unitValue.parameters.sequence().toList()
                .filter { (it.lexicalUnitType == LexicalUnit.SAC_INTEGER) }
                .map { it.integerValue }
            if (colors.size != 4) {
                error("rgba() parameter size should be 4: ${colors.size}")
            }
            ImageVector.RgbColor(
                red = colors[0],
                green = colors[1],
                blue = colors[2],
                alpha = colors[3]
            )
        }

        else -> null
    }
}

private fun CSSStyleDeclaration.getFloatPxValue(name: String): Float? {
    return (getPropertyCSSValue(name) as? CSSPrimitiveValue)?.getFloatValue(CSS_PX)
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
    val style = getStyleDeclaration()
    listOf(
        "display",
        "stroke",
        "stroke-opacity",
        "stroke-width",
        "stroke-linecap",
        "stroke-linejoin",
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

private fun SVGStylableElement.getStyleDeclaration(): CSSStyleDeclaration {
    val value = (attributes.getNamedItem("style") as? GenericAttr)?.value ?: ""
    val handler = object : ErrorHandler {
        override fun warning(exception: CSSParseException) {
            println("warning = $exception")
        }

        override fun error(exception: CSSParseException) {
            println("error = $exception")
        }

        override fun fatalError(exception: CSSParseException) {
            println("fatal = $exception")
        }
    }
    SACParserCSS3ColorFix.registerSACParser()
    return CSSOMParser(SACParserCSS3ColorFix().apply {
        setErrorHandler(handler)
    }).parseStyleDeclaration(
        InputSource(value.reader())
    )
}

/**
 * needs public, because this class will be registered to "org.w3c.css.sac.parser"
 */
class SACParserCSS3ColorFix : SACParserCSS3() {
    companion object {
        fun registerSACParser() {
            System.setProperty(
                "org.w3c.css.sac.parser",
                SACParserCSS3ColorFix::class.java.canonicalName
            )
        }
    }

    override fun functionInternal(
        prev: LexicalUnit?,
        funct: String,
        params: LexicalUnit?,
    ): LexicalUnit {
        return if (
            (params != null)
            && (funct.equals("rgb(", ignoreCase = true) ||
                    funct.equals("rgba(", ignoreCase = true))
        ) {
            // Support
            //   rgb(0 0 0), rgb(0 0 0 0), rgb(0 0 0 / 0)
            //   rgba(0 0 0), rgba(0 0 0 0), rgba(0 0 0 / 0)
            // Convert parameters to comma separated format (e.g. "0,0,0,0")
            var p = (params as? LexicalUnitImpl)
            while (p != null) {
                when (p.nextLexicalUnit?.lexicalUnitType) {
                    LexicalUnit.SAC_OPERATOR_COMMA -> {
                        // move next value
                        p = (p.nextLexicalUnit?.nextLexicalUnit as? LexicalUnitImpl)
                    }

                    LexicalUnit.SAC_OPERATOR_SLASH -> {
                        // replace comma
                        val next = (p.nextLexicalUnit?.nextLexicalUnit as? LexicalUnitImpl)
                        LexicalUnitImpl.createComma(p).let {
                            next?.previousLexicalUnit = it
                            (it as LexicalUnitImpl).nextLexicalUnit = next
                        }
                        p = next
                    }

                    null -> {
                        p = null
                    }

                    else -> {
                        // insert comma
                        val next = (p.nextLexicalUnit as LexicalUnitImpl)
                        LexicalUnitImpl.createComma(p).let {
                            next.previousLexicalUnit = it
                            (it as LexicalUnitImpl).nextLexicalUnit = next
                        }
                        p = next
                    }
                }
            }
            val count = params.sequence().count {
                (it.lexicalUnitType != LexicalUnit.SAC_OPERATOR_COMMA)
            }
            if (count == 3) {
                LexicalUnitImpl.createRgbColor(prev, params)
            } else {
                LexicalUnitImpl.createFunction(prev, "rgba", params)
            }
        } else {
            super.functionInternal(prev, funct, params)
        }
    }

    override fun hexcolorInternal(prev: LexicalUnit?, token: Token): LexicalUnit {
        val length = token.image.length - 1
        return if (length == 4 || length == 8) {
            // Support 4 digits and 8 digits hex color
            // https://www.w3.org/TR/css-color-4/#hex-notation
            try {
                val i = 1
                var r: Int
                var g: Int
                var b: Int
                var a: Int
                if (length == 4) {
                    r = token.image.substring(i + 0, i + 1).toInt(16)
                    g = token.image.substring(i + 1, i + 2).toInt(16)
                    b = token.image.substring(i + 2, i + 3).toInt(16)
                    a = token.image.substring(i + 3, i + 4).toInt(16)
                    r = (r shl 4) or r
                    g = (g shl 4) or g
                    b = (b shl 4) or b
                    a = (a shl 4) or a
                } else {
                    // len == 8
                    r = token.image.substring(i + 0, i + 2).toInt(16)
                    g = token.image.substring(i + 2, i + 4).toInt(16)
                    b = token.image.substring(i + 4, i + 6).toInt(16)
                    a = token.image.substring(i + 6, i + 8).toInt(16)
                }
                val unit = LexicalUnitImpl.createNumber(null, r)
                var chain = LexicalUnitImpl.createComma(unit)
                chain = LexicalUnitImpl.createNumber(chain, g)
                chain = LexicalUnitImpl.createComma(chain)
                chain = LexicalUnitImpl.createNumber(chain, b)
                chain = LexicalUnitImpl.createComma(chain)
                LexicalUnitImpl.createNumber(chain, a)
                return LexicalUnitImpl.createFunction(prev, "rgba", unit)
            } catch (error: java.lang.NumberFormatException) {
                throw CSSParseException(
                    getSACParserMessages().getString("invalidColor").format(token),
                    inputSource.uri, token.beginLine,
                    token.beginColumn, error
                )
            }
        } else super.hexcolorInternal(prev, token)
    }
}
