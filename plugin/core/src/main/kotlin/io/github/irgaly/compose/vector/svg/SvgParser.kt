package io.github.irgaly.compose.vector.svg

import io.github.irgaly.compose.Logger
import io.github.irgaly.compose.vector.node.ImageVector
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVG12DOMImplementation
import org.apache.batik.anim.dom.SVGGraphicsElement
import org.apache.batik.anim.dom.SVGOMAElement
import org.apache.batik.anim.dom.SVGOMAnimatedLength
import org.apache.batik.anim.dom.SVGOMAnimatedRect
import org.apache.batik.anim.dom.SVGOMCircleElement
import org.apache.batik.anim.dom.SVGOMClipPathElement
import org.apache.batik.anim.dom.SVGOMCursorElement
import org.apache.batik.anim.dom.SVGOMDefsElement
import org.apache.batik.anim.dom.SVGOMDescElement
import org.apache.batik.anim.dom.SVGOMDocument
import org.apache.batik.anim.dom.SVGOMElement
import org.apache.batik.anim.dom.SVGOMEllipseElement
import org.apache.batik.anim.dom.SVGOMGElement
import org.apache.batik.anim.dom.SVGOMLineElement
import org.apache.batik.anim.dom.SVGOMMetadataElement
import org.apache.batik.anim.dom.SVGOMPathElement
import org.apache.batik.anim.dom.SVGOMPolygonElement
import org.apache.batik.anim.dom.SVGOMPolylineElement
import org.apache.batik.anim.dom.SVGOMRectElement
import org.apache.batik.anim.dom.SVGOMSVGElement
import org.apache.batik.anim.dom.SVGOMScriptElement
import org.apache.batik.anim.dom.SVGOMStyleElement
import org.apache.batik.anim.dom.SVGOMSymbolElement
import org.apache.batik.anim.dom.SVGOMTitleElement
import org.apache.batik.anim.dom.SVGOMUseElement
import org.apache.batik.anim.dom.SVGOMViewElement
import org.apache.batik.anim.dom.SVGStylableElement
import org.apache.batik.bridge.AbstractSVGGradientElementBridge
import org.apache.batik.bridge.AbstractSVGGradientElementBridge.SVGStopElementBridge
import org.apache.batik.bridge.Bridge
import org.apache.batik.bridge.BridgeContext
import org.apache.batik.bridge.BridgeExtension
import org.apache.batik.bridge.CSSUtilities
import org.apache.batik.bridge.DocumentLoader
import org.apache.batik.bridge.GVTBuilder
import org.apache.batik.bridge.PaintServer
import org.apache.batik.bridge.SVGUseElementBridge
import org.apache.batik.bridge.UserAgentAdapter
import org.apache.batik.bridge.svg12.SVG12BridgeContext
import org.apache.batik.css.engine.CSSContext
import org.apache.batik.css.engine.CSSEngine
import org.apache.batik.css.engine.CSSStylableElement
import org.apache.batik.css.engine.SVGCSSEngine
import org.apache.batik.css.engine.StyleMap
import org.apache.batik.css.engine.value.AbstractColorManager
import org.apache.batik.css.engine.value.AbstractValue
import org.apache.batik.css.engine.value.ComputedValue
import org.apache.batik.css.engine.value.FloatValue
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
import org.apache.batik.dom.svg.SVGAnimatedPathDataSupport
import org.apache.batik.dom.svg.SVGOMMatrix
import org.apache.batik.ext.awt.LinearGradientPaint
import org.apache.batik.ext.awt.MultipleGradientPaint
import org.apache.batik.ext.awt.RadialGradientPaint
import org.apache.batik.gvt.CompositeGraphicsNode
import org.apache.batik.gvt.CompositeShapePainter
import org.apache.batik.gvt.FillShapePainter
import org.apache.batik.gvt.GraphicsNode
import org.apache.batik.gvt.ShapeNode
import org.apache.batik.gvt.ShapePainter
import org.apache.batik.gvt.StrokeShapePainter
import org.apache.batik.parser.PathHandler
import org.apache.batik.parser.PathParser
import org.apache.batik.svggen.SVGGeneratorContext
import org.apache.batik.svggen.SVGPath
import org.apache.batik.util.XMLResourceDescriptor
import org.w3c.css.sac.LexicalUnit
import org.w3c.dom.DOMImplementation
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.css.CSSPrimitiveValue
import org.w3c.dom.css.CSSValue
import org.w3c.dom.svg.SVGLinearGradientElement
import org.w3c.dom.svg.SVGMatrix
import org.w3c.dom.svg.SVGPathSegList
import org.w3c.dom.svg.SVGRadialGradientElement
import java.awt.Color
import java.awt.Paint
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.io.IOException
import java.io.InputStream
import java.util.Collections
import kotlin.reflect.KProperty

/**
 * SVG -> ImageVector
 */
class SvgParser(
    private val logger: Logger,
) {
    /**
     * @throws IOException
     * @throws IllegalStateException parse error
     */
    fun parse(
        input: InputStream,
        name: String,
        autoMirror: Boolean = false,
    ): ImageVector {
        val document = try {
            SAXSVGDocumentFactoryCSS3ColorFix(
                XMLResourceDescriptor.getXMLParserClassName()
            ).createDocument("xml", input) as SVGOMDocument
        } catch (error: IOException) {
            logger.error("open XML document error", error)
            throw error
        }
        val bridgeContext = document.initializeSvgCssEngine()
        val svg = (document.rootElement as SVGOMSVGElement)
        val viewBox = (svg.viewBox as SVGOMAnimatedRect)
        val svgWidth = (svg.width as SVGOMAnimatedLength)
        val svgHeight = (svg.height as SVGOMAnimatedLength)
        val viewBoxWidth = if (viewBox.isSpecified) {
            svg.viewBox.baseVal.width
        } else {
            if (svgWidth.isSpecified) {
                svgWidth.baseVal.valueInSpecifiedUnits
            } else {
                // default width value
                // https://svgwg.org/specs/integration/#svg-css-sizing
                300f
            }
        }
        val viewBoxHeight = if (viewBox.isSpecified) {
            svg.viewBox.baseVal.height
        } else {
            if (svgHeight.isSpecified) {
                svgHeight.baseVal.valueInSpecifiedUnits
            } else {
                // default height value
                // https://svgwg.org/specs/integration/#svg-css-sizing
                150f
            }
        }
        val width = if (svgWidth.isSpecified) {
            svgWidth.baseVal.valueInSpecifiedUnits
        } else viewBoxWidth
        val height = if (svgHeight.isSpecified) {
            svgHeight.baseVal.valueInSpecifiedUnits
        } else viewBoxHeight
        logger.debug("viewBox width = $viewBoxWidth, height = $viewBoxHeight")
        logger.debug("width = $width, height = $height")
        val groups = mutableListOf(
            // root group
            GroupInfo(svg, ImageVector.VectorNode.VectorGroup(emptyList()))
        )
        var extraId: Long = 0
        svg.traverse(
            onElementPreprocess = { element ->
                val styles = (element as? SVGStylableElement)?.getComputedStyleMap(null)
                val display = styles?.getValue(SVGCSSEngine.DISPLAY_INDEX)?.stringValue ?: "inline"
                val visibility = styles?.getValue(SVGCSSEngine.VISIBILITY_INDEX)?.stringValue ?: "visible"
                val visibleElement = (
                        !display.equals("none", ignoreCase = true) &&
                        !visibility.equals("hidden", ignoreCase = true)
                        )
                val process = when {
                    (element is SVGOMTitleElement) ||
                            (element is SVGOMDescElement) ||
                            (element is SVGOMMetadataElement) ||
                            (element is SVGOMViewElement) ||
                            (element is SVGOMScriptElement) ||
                            (element is SVGOMCursorElement) ||
                            (element is SVGOMDefsElement) ||
                            (element is SVGOMSymbolElement) ||
                            (element is SVGOMStyleElement) ||
                            (element is SVGOMClipPathElement) ||
                            (element is SVGLinearGradientElement) ||
                            (element is SVGRadialGradientElement)
                    -> {
                        // Skip process element
                        false
                    }

                    else -> {
                        visibleElement
                    }
                }
                if (!process) {
                    logger.debug("skip element: ${element.toDebugString()}")
                }
                process
            },
            onElementBegin = { element ->
                val graphicsNode: GraphicsNode? = bridgeContext.getGraphicsNode(element)
                val clipPathShape = graphicsNode?.clip?.clipPath
                logger.debug("element begin: ${element.toDebugString()}")
                when (element) {
                    is SVGOMSVGElement,
                    is SVGOMGElement,
                    is SVGOMUseElement,
                    is SVGOMAElement,
                    -> {
                        check(element is SVGStylableElement)
                        check(graphicsNode != null)
                        val extra = element.getStyleExtra(extraId = extraId.toString())
                        if (extra != null) {
                            extraId++
                        }
                        val group: ImageVector.VectorNode.VectorGroup
                        if (element == svg) {
                            // root svg group
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
                            val ctm = if (basicMatrix) ImageVector.Matrix(1f, 0f, 0f, 1f, 0f, 0f) else matrix
                            if (!basicMatrix) {
                                logger.debug("    matrix = $ctm")
                            }
                            group = ImageVector.VectorNode.VectorGroup(
                                nodes = emptyList(),
                                scaleX = if (basicMatrix && matrix.a != 1f) matrix.a else null,
                                scaleY = if (basicMatrix && matrix.d != 1f) matrix.d else null,
                                translationX = if (basicMatrix && matrix.e != 0f) matrix.e else null,
                                translationY = if (basicMatrix && matrix.f != 0f) matrix.f else null,
                                currentTransformationMatrix = ctm,
                                extra = extra
                            )
                        } else {
                            val parentGroup = groups.last().group
                            val ctm = AffineTransform().apply {
                                concatenate(parentGroup.currentTransformationMatrix.toAffineTransform())
                                concatenate(graphicsNode.transform)
                            }
                            if (!ctm.isIdentity) {
                                logger.debug("    matrix = ${ctm.toMatrix()}")
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
                        groups.add(GroupInfo(element, group))
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
                            concatenate(parentGroup.currentTransformationMatrix.toAffineTransform())
                            concatenate(graphicsNode.transform)
                        }
                        if (!ctm.isIdentity) {
                            logger.debug("    matrix = ${ctm.toMatrix()}")
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
                        val extra = element.getStyleExtra("")
                        val fillBrush = graphicsNode.getFillBrush(ctm)
                        val fill = if (fillBrush != null && fillBrush !is ImageVector.Brush.SolidColor) {
                            fillBrush
                        } else extra?.fill
                        val strokeBrush = graphicsNode.getStrokeBrush(ctm)
                        val stroke = if (strokeBrush != null && strokeBrush !is ImageVector.Brush.SolidColor) {
                            strokeBrush
                        } else extra?.stroke
                        var pathFillTypeId: String? = null
                        var fillId: String? = null
                        var fillAlphaId: String? = null
                        var strokeId: String? = null
                        var strokeAlphaId: String? = null
                        var strokeLineWidthId: String? = null
                        var strokeLineCapId: String? = null
                        var strokeLineJoinId: String? = null
                        var strokeLineMiterId: String? = null
                        groups.reversed().forEach { group ->
                            val groupExtra = group.group.extra
                            if (groupExtra != null) {
                                if (extra?.pathFillType == null && pathFillTypeId == null && groupExtra.pathFillType != null) {
                                    pathFillTypeId = groupExtra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::pathFillType)
                                }
                                if (fill == null && fillId == null && groupExtra.fill != null) {
                                    fillId = groupExtra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::fill)
                                }
                                if (extra?.fillAlpha == null && fillAlphaId == null && groupExtra.fillAlpha != null) {
                                    fillAlphaId = groupExtra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::fillAlpha)
                                }
                                if (stroke == null && strokeId == null && groupExtra.stroke != null) {
                                    strokeId = groupExtra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::stroke)
                                }
                                if (extra?.strokeAlpha == null && strokeAlphaId == null && groupExtra.strokeAlpha != null) {
                                    strokeAlphaId = groupExtra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::strokeAlpha)
                                }
                                if (extra?.strokeLineWidth == null && strokeLineWidthId == null && groupExtra.strokeLineWidth != null) {
                                    strokeLineWidthId = groupExtra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::strokeLineWidth)
                                }
                                if (extra?.strokeLineCap == null && strokeLineCapId == null && groupExtra.strokeLineCap != null) {
                                    strokeLineCapId = groupExtra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::strokeLineCap)
                                }
                                if (extra?.strokeLineJoin == null && strokeLineJoinId == null && groupExtra.strokeLineJoin != null) {
                                    strokeLineJoinId = groupExtra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::strokeLineJoin)
                                }
                                if (extra?.strokeLineMiter == null && strokeLineMiterId == null && groupExtra.strokeLineMiter != null) {
                                    strokeLineMiterId = groupExtra.id
                                    group.referencedProperties.add(ImageVector.VectorNode.VectorGroup.Extra::strokeLineMiter)
                                }
                            }
                        }
                        val extraReference = if (
                            (pathFillTypeId != null) ||
                            (fillId != null) ||
                            (fillAlphaId != null) ||
                            (strokeId != null) ||
                            (strokeAlphaId != null) ||
                            (strokeLineWidthId != null) ||
                            (strokeLineCapId != null) ||
                            (strokeLineJoinId != null) ||
                            (strokeLineMiterId != null)
                        ) {
                            ImageVector.VectorNode.VectorPath.ExtraReference(
                                pathFillTypeId = pathFillTypeId,
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
                                pathFillType = extra?.pathFillType,
                                name = element.xmlId.ifEmpty { null },
                                fill = fill,
                                fillAlpha = extra?.fillAlpha,
                                stroke = stroke,
                                strokeAlpha = extra?.strokeAlpha,
                                strokeLineWidth = extra?.strokeLineWidth,
                                strokeLineCap = extra?.strokeLineCap,
                                strokeLineJoin = extra?.strokeLineJoin,
                                strokeLineMiter = extra?.strokeLineMiter,
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

                    else -> {
                        // SVG elements: https://developer.mozilla.org/en-US/docs/Web/SVG/Element
                        logger.error("Unsupported element: ${element.toDebugString()}")
                        error("Unsupported element: ${element.toDebugString()}")
                    }
                }
            },
            onElementEnd = { element ->
                when (element) {
                    is SVGOMSVGElement,
                    is SVGOMGElement,
                    is SVGOMUseElement,
                    -> {
                        val group = groups.removeLast()
                        val parent = groups.last()
                        val currentGroup = group.group.copy(
                            nodes = group.nodes.toList(),
                            referencedExtra = group.getReferencedExtra(),
                        )
                        if (
                            currentGroup.nodes.isEmpty() || (
                                (currentGroup.rotate == null) &&
                                (currentGroup.pivotX == null) &&
                                (currentGroup.pivotY == null) &&
                                (currentGroup.scaleX == null) &&
                                (currentGroup.scaleY == null) &&
                                (currentGroup.translationX == null) &&
                                (currentGroup.translationY == null) &&
                                currentGroup.clipPathData.isEmpty() &&
                                ((element == svg) || (currentGroup.referencedExtra == null))
                            )
                        ) {
                            // skip group
                            parent.nodes.addAll(currentGroup.nodes)
                            if (element == svg) {
                                // copy extra to root group
                                parent.referencedProperties.addAll(group.referencedProperties)
                                groups.removeLast()
                                groups.add(
                                    parent.copy(
                                        group = parent.group.copy(
                                            extra = currentGroup.extra
                                        )
                                    )
                                )
                            }
                        } else {
                            // add group
                            parent.nodes.add(currentGroup)
                        }
                    }

                    else -> {
                        // Nothing to do
                    }
                }
            }
        )
        val root = groups.first()
        val imageVector = ImageVector(
            name = name,
            defaultWidth = width.toDouble(),
            defaultHeight = height.toDouble(),
            viewportWidth = viewBoxWidth,
            viewportHeight = viewBoxHeight,
            autoMirror = autoMirror,
            rootGroup = root.group.copy(
                nodes = root.nodes.toList(),
                referencedExtra = root.getReferencedExtra()
            )
        )
        return imageVector
    }

    private data class GroupInfo(
        val element: SVGOMElement,
        val group: ImageVector.VectorNode.VectorGroup,
        val nodes: MutableList<ImageVector.VectorNode> = mutableListOf(),
        val referencedProperties: MutableSet<KProperty<*>> = mutableSetOf(),
    ) {
        fun getReferencedExtra(): ImageVector.VectorNode.VectorGroup.Extra? {
            return if (group.extra != null && referencedProperties.isNotEmpty()) {
                ImageVector.VectorNode.VectorGroup.Extra(
                    id = group.extra.id,
                    pathFillType = if (referencedProperties.contains(ImageVector.VectorNode.VectorGroup.Extra::pathFillType)) group.extra.pathFillType else null,
                    fill = if (referencedProperties.contains(ImageVector.VectorNode.VectorGroup.Extra::fill)) group.extra.fill else null,
                    fillAlpha = if (referencedProperties.contains(ImageVector.VectorNode.VectorGroup.Extra::fillAlpha)) group.extra.fillAlpha else null,
                    stroke = if (referencedProperties.contains(ImageVector.VectorNode.VectorGroup.Extra::stroke)) group.extra.stroke else null,
                    strokeAlpha = if (referencedProperties.contains(ImageVector.VectorNode.VectorGroup.Extra::strokeAlpha)) group.extra.strokeAlpha else null,
                    strokeLineWidth = if (referencedProperties.contains(ImageVector.VectorNode.VectorGroup.Extra::strokeLineWidth)) group.extra.strokeLineWidth else null,
                    strokeLineCap = if (referencedProperties.contains(ImageVector.VectorNode.VectorGroup.Extra::strokeLineCap)) group.extra.strokeLineCap else null,
                    strokeLineJoin = if (referencedProperties.contains(ImageVector.VectorNode.VectorGroup.Extra::strokeLineJoin)) group.extra.strokeLineJoin else null,
                    strokeLineMiter = if (referencedProperties.contains(ImageVector.VectorNode.VectorGroup.Extra::strokeLineMiter)) group.extra.strokeLineMiter else null,
                )
            } else null
        }
    }
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
                add(OverrideBridgeExtension())
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
    onElementPreprocess: (element: SVGOMElement) -> Boolean,
    onElementBegin: (element: SVGOMElement) -> Unit,
    onElementEnd: (element: SVGOMElement) -> Unit,
) {
    val continueTraverse = onElementPreprocess(this)
    if (continueTraverse) {
        onElementBegin(this)
        children().forEach { child ->
            child.traverse(onElementPreprocess, onElementBegin, onElementEnd)
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

private fun AffineTransform.toMatrix(): ImageVector.Matrix {
    return SVGOMMatrix(this).toMatrix()
}

/**
 * get locally applied styles, except inherited styles
 */
private fun SVGStylableElement.getStyleExtra(
    extraId: String,
): ImageVector.VectorNode.VectorGroup.Extra? {
    var extra: ImageVector.VectorNode.VectorGroup.Extra? = null
    val styles = getComputedStyleMap(null)
    val pathFillType =
        styles.getLocalValue(SVGCSSEngine.FILL_RULE_INDEX)?.stringValue?.toPathFillType()
    val fill = styles.getLocalValue(SVGCSSEngine.FILL_INDEX)?.toColor()?.toBrush()
    val fillAlpha = styles.getLocalValue(SVGCSSEngine.FILL_OPACITY_INDEX)?.toOpacity()?.let {
        if (it == 1f) {
            // 1f is Default value
            null
        } else it
    }
    val stroke = styles.getLocalValue(SVGCSSEngine.STROKE_INDEX)?.toColor()?.toBrush()
    val strokeAlpha = styles.getLocalValue(SVGCSSEngine.STROKE_OPACITY_INDEX)?.toOpacity()?.let {
        if (it == 1f) {
            // 1f is Default value
            null
        } else it
    }
    val strokeLineWidth = styles.getLocalValue(SVGCSSEngine.STROKE_WIDTH_INDEX)?.floatValue
    val strokeLineCap = styles.getLocalValue(SVGCSSEngine.STROKE_LINECAP_INDEX)?.stringValue?.toStrokeCap()?.let {
        if (it == ImageVector.StrokeCap.Butt) {
            // Butt is Default value
            null
        } else it
    }
    val strokeLineJoin = styles.getLocalValue(SVGCSSEngine.STROKE_LINEJOIN_INDEX)?.stringValue?.toStrokeJoin()?.let {
        if (it == ImageVector.StrokeJoin.Miter) {
            // Miter is Default value
            null
        } else it
    }
    val strokeLineMiter = styles.getLocalValue(SVGCSSEngine.STROKE_MITERLIMIT_INDEX)?.toMiterLimit()?.let {
        if (it == 4f) {
            // 4f is Default value
            null
        } else it
    }
    if (
        (pathFillType != null) ||
        (fill != null) ||
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
            pathFillType = pathFillType,
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

private fun StyleMap.getLocalValue(cssIndex: Int): Value? {
    return if (!isInherited(cssIndex))
        getValue(cssIndex)
    else null
}

private fun Value.toColor(): ImageVector.Color? {
    val value = if (this is ComputedValue) cascadedValue else this
    return when {
        (value is RGBAColorValue) -> {
            ImageVector.RgbColor(
                red = value.red.getColorValue(),
                green = value.green.getColorValue(),
                blue = value.blue.getColorValue(),
                alpha = value.alpha.getColorValue(),
            )
        }

        (value.primitiveType == CSSPrimitiveValue.CSS_RGBCOLOR) -> {
            ImageVector.RgbColor(
                red = value.red.getColorValue(),
                green = value.green.getColorValue(),
                blue = value.blue.getColorValue(),
            )
        }

        (value.primitiveType == CSSPrimitiveValue.CSS_IDENT) -> {
            val colorNameLowercase = value.stringValue.lowercase()
            // Support Compose Colors
            // https://developer.android.com/reference/kotlin/androidx/compose/ui/graphics/Color
            when (colorNameLowercase) {
                "none" -> {
                    null
                }
                "transparent" -> {
                    ImageVector.ComposeColor("Transparent")
                }
                "black" -> {
                    ImageVector.ComposeColor("Black")
                }
                "white" -> {
                    ImageVector.ComposeColor("White")
                }
                "red" -> {
                    ImageVector.ComposeColor("Red")
                }
                "lime" -> {
                    ImageVector.ComposeColor("Green")
                }
                "blue" -> {
                    ImageVector.ComposeColor("Blue")
                }
                "yellow" -> {
                    ImageVector.ComposeColor("Yellow")
                }
                "cyan", "aqua" -> {
                    ImageVector.ComposeColor("Cyan")
                }
                "fuchsia", "magenta" -> {
                    ImageVector.ComposeColor("Magenta")
                }
                else -> {
                    check(this is ComputedValue)
                    ImageVector.RgbColor(
                        red = red.floatValue.toInt(),
                        green = green.floatValue.toInt(),
                        blue = blue.floatValue.toInt(),
                    )
                }
            }
        }

        else -> null
    }
}

private fun Value.toOpacity(): Float {
    return PaintServer.convertOpacity(this)
}

private fun Value.toMiterLimit(): Float {
    return PaintServer.convertStrokeMiterlimit(this)
}

private fun Value.getColorValue(): Int {
    return if (primitiveType == CSSPrimitiveValue.CSS_PERCENTAGE) {
        val percentage = floatValue
        (percentage * 255f / 100f).toInt()
    } else {
        floatValue.toInt()
    }
}

private fun String.toPathFillType(): ImageVector.PathFillType? {
    return if (equals("nonzero", ignoreCase = true)) {
        ImageVector.PathFillType.NonZero
    } else if (equals("evenodd", ignoreCase = true)) {
        ImageVector.PathFillType.EvenOdd
    } else {
        error("invalid path-rule: $this")
    }
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
 * needs public, because this class will be registered to "org.w3c.css.sac.parser"
 */
internal class ParserCSS3ColorFix : Parser() {
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

private class SAXSVGDocumentFactoryCSS3ColorFix(
    parser: String?,
): SAXSVGDocumentFactory(parser) {
    override fun getDOMImplementation(ver: String?): DOMImplementation {
        return SVG120DOMImplementationCSS3ColorFix()
    }
}

private class SVG120DOMImplementationCSS3ColorFix : SVG12DOMImplementation() {
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

/**
 * Support rgba() and CSS Color Module Level 4 named colors
 * https://drafts.csswg.org/css-color/#named-colors
 */
private class AbstractColorManagerCSS3ColorFix(
    private val original: AbstractColorManager,
): AbstractColorManager() {
    init {
        values.put("transparent", StringValue(CSSPrimitiveValue.CSS_IDENT, "transparent"))
        values.put("rebeccapurple", StringValue(CSSPrimitiveValue.CSS_IDENT, "rebeccapurple"))
        computedValues.put("transparent", RGBAColorValue(NUMBER_0, NUMBER_0, NUMBER_0, NUMBER_0))
        computedValues.put("rebeccapurple", RGBAColorValue(102f, 51f, 153f, 255f))
        // other CSS Color Module Level 3 colors will be handled by
        // * org.apache.batik.css.engine.value.svg.SVGColorManager
        // * org.apache.batik.css.engine.value.svg.ColorManager
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

private class RGBAColorValue(
    private val r: Value,
    private val g: Value,
    private val b: Value,
    private val a: Value,
) : AbstractValue() {
    constructor(r: Float, g: Float, b: Float, a: Float):
        this(
            r = FloatValue(CSSPrimitiveValue.CSS_NUMBER, r),
            g = FloatValue(CSSPrimitiveValue.CSS_NUMBER, g),
            b = FloatValue(CSSPrimitiveValue.CSS_NUMBER, b),
            a = FloatValue(CSSPrimitiveValue.CSS_NUMBER, a),
        )

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

private fun SVGMatrix.toMatrix(): ImageVector.Matrix {
    return ImageVector.Matrix(
        a = a,
        b = b,
        c = c,
        d = d,
        e = e,
        f = f
    )
}

private fun ImageVector.Matrix.toAffineTransform(): AffineTransform {
    return AffineTransform(
        a,
        b,
        c,
        d,
        e,
        f,
    )
}

private fun Document.toPathData(shape: Shape): List<ImageVector.PathNode> {
    val pathString = SVGPath.toSVGPathData(shape, SVGGeneratorContext.createDefault(this))
    return PathDataPathHandler().also {
        PathParser().apply {
            pathHandler = it
        }.parse(pathString)
    }.getPath()
}

private fun SVGPathSegList.toPathData(): List<ImageVector.PathNode> {
    return PathDataPathHandler().also {
        SVGAnimatedPathDataSupport.handlePathSegList(this, it)
    }.getPath()
}

private class PathDataPathHandler : PathHandler {
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
private class SVGUseElementBridgeHrefFix : SVGUseElementBridge() {
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

private class OverrideBridgeExtension : BridgeExtension {
    override fun getPriority(): Float = 0f

    override fun getImplementedExtensions(): MutableIterator<Any?> = Collections.EMPTY_LIST.iterator()

    override fun getAuthor(): String = ""

    override fun getContactAddress(): String = ""

    override fun getURL(): String = ""

    override fun getDescription(): String = ""

    override fun registerTags(ctx: BridgeContext) {
        ctx.putBridge(SVGUseElementBridgeHrefFix())
        ctx.putBridge(SVGStopElementBridgeColorFix())
    }

    override fun isDynamicElement(e: Element): Boolean = false
}

private class SVGStopElementBridgeColorFix : SVGStopElementBridge() {
    override fun createStop(
        ctx: BridgeContext,
        gradientElement: Element,
        stopElement: Element,
        opacity: Float,
    ): AbstractSVGGradientElementBridge.Stop {
        val stop = super.createStop(ctx, gradientElement, stopElement, opacity)
        return AbstractSVGGradientElementBridge.Stop(
            convertStopColor(stopElement, opacity, ctx),
            stop.offset
        )
    }

    /**
     * from CSSUtilities.convertStopColor
     */
    private fun convertStopColor(e: Element, opacity: Float, ctx: BridgeContext): Color {
        val v = CSSUtilities.getComputedStyle(e, SVGCSSEngine.STOP_COLOR_INDEX)
        val o = CSSUtilities.getComputedStyle(e, SVGCSSEngine.STOP_OPACITY_INDEX)
        val newOpacity = opacity * PaintServer.convertOpacity(o)
        return if (v.cssValueType == CSSValue.CSS_PRIMITIVE_VALUE)
            convertColor(v, newOpacity)
        else PaintServer.convertRGBICCColor(e, v.item(0), v.item(1), newOpacity, ctx)
    }

    /**
     * from PaintServer.convertColor
     */
    private fun convertColor(c: Value, opacity: Float): Color {
        val r = PaintServer.resolveColorComponent(c.red)
        val g = PaintServer.resolveColorComponent(c.green)
        val b = PaintServer.resolveColorComponent(c.blue)
        val a = if (c is RGBAColorValue) {
            (PaintServer.resolveColorComponent(c.alpha) / 255f)
        } else 1.0f
        return Color(r, g, b, Math.round(255f * a * opacity))
    }
}

private fun GraphicsNode.getFillBrush(transform: AffineTransform): ImageVector.Brush? {
    val shapePainter = ((this as? ShapeNode)?.shapePainter as? CompositeShapePainter)
    val painter = shapePainter?.painters()?.filterIsInstance<FillShapePainter>()?.firstOrNull()
    return painter?.paint?.toBrush(transform)
}

private fun GraphicsNode.getStrokeBrush(transform: AffineTransform): ImageVector.Brush? {
    val shapePainter = ((this as? ShapeNode)?.shapePainter as? CompositeShapePainter)
    val painter = shapePainter?.painters()?.filterIsInstance<StrokeShapePainter>()?.firstOrNull()
    return painter?.paint?.toBrush(transform)
}

private fun CompositeShapePainter.painters(): Sequence<ShapePainter> {
    return sequence {
        (0..<shapePainterCount).forEach {
            yield(getShapePainter(it))
        }
    }
}

private fun Paint.toBrush(transform: AffineTransform): ImageVector.Brush {
    return when (this) {
        is LinearGradientPaint -> {
            val applyTransform = AffineTransform().apply {
                concatenate(transform)
                concatenate(this@toBrush.transform)
            }
            val start = applyTransform.transform(startPoint, null)
            val end = applyTransform.transform(endPoint, null)
            ImageVector.Brush.LinearGradient(
                colorStops = fractions.zip(colors).map { (stop, color) ->
                    Pair(stop, color.toImageVectorColor())
                },
                start = Pair(start.x.toFloat(), start.y.toFloat()),
                end = Pair(end.x.toFloat(), end.y.toFloat()),
                tileMode = when (cycleMethod) {
                    MultipleGradientPaint.NO_CYCLE -> ImageVector.TileMode.Clamp
                    MultipleGradientPaint.REPEAT -> ImageVector.TileMode.Repeated
                    MultipleGradientPaint.REFLECT -> ImageVector.TileMode.Mirror
                    else -> error("unknown spreadMethod")
                }
            )
        }

        is RadialGradientPaint -> {
            val applyTransform = AffineTransform().apply {
                concatenate(transform)
                concatenate(this@toBrush.transform)
            }
            val center = applyTransform.transform(centerPoint, null)
            val radius = applyTransform.deltaTransform(Point2D.Float(radius, 0f), null).x.toFloat()
            ImageVector.Brush.RadialGradient(
                colorStops = fractions.zip(colors).map { (stop, color) ->
                    Pair(stop, color.toImageVectorColor())
                },
                center = Pair(center.x.toFloat(), center.y.toFloat()),
                radius = radius,
                tileMode = when (cycleMethod) {
                    MultipleGradientPaint.NO_CYCLE -> ImageVector.TileMode.Clamp
                    MultipleGradientPaint.REPEAT -> ImageVector.TileMode.Repeated
                    MultipleGradientPaint.REFLECT -> ImageVector.TileMode.Mirror
                    else -> error("unknown spreadMethod")
                }
            )
        }

        is Color -> {
            ImageVector.Brush.SolidColor(
                ImageVector.RgbColor(
                    red = red,
                    green = green,
                    blue = blue,
                    alpha = alpha
                )
            )
        }

        else -> error("unknown Paint: $this")
    }
}

private fun Color.toImageVectorColor(): ImageVector.Color {
    return if (alpha == 0) {
        ImageVector.ComposeColor("Transparent")
    } else {
        ImageVector.RgbColor(
            red = red,
            green = green,
            blue = blue,
            alpha = alpha
        )
    }
}

private fun Element.toDebugString(): String {
    return "<$nodeName ${
        attributes().joinToString {
            "${it.nodeName}=${
                it.nodeValue.trim().replace("\n", " ")
            }"
        }
    }> (${this::class.simpleName})"
}

private fun Element.attributes(): Sequence<Node> {
    return sequence {
        (0..<attributes.length).forEach {
            yield(attributes.item(it))
        }
    }
}
