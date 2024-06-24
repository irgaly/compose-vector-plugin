package io.github.irgaly.compose.vector.svg

import io.github.irgaly.compose.vector.node.ImageVector
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGOMAnimatedLength
import org.apache.batik.anim.dom.SVGOMAnimatedRect
import org.apache.batik.anim.dom.SVGOMDocument
import org.apache.batik.anim.dom.SVGOMElement
import org.apache.batik.anim.dom.SVGOMGElement
import org.apache.batik.anim.dom.SVGOMPathElement
import org.apache.batik.anim.dom.SVGOMSVGElement
import org.apache.batik.util.XMLResourceDescriptor
import java.io.IOException
import java.io.InputStream

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
        }
        val svg = (document.rootElement as SVGOMSVGElement)
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
        // TODO: svg fill attributeに対応する
        // TODO: useタグに対応する
        // * id付きのタグをfunで定義して使い回す
        var rootNodes = mutableListOf<ImageVector.VectorNode>()
        val currentNodesStack = mutableListOf(rootNodes)
        svg.traverse(
            onGroupBegin = { _ ->
                currentNodesStack.add(mutableListOf())
            },
            onGroupEnd = { element ->
                val childNodes = currentNodesStack.removeLast()
                val currentNodes = currentNodesStack.last()
                currentNodes.add(
                    ImageVector.VectorNode.VectorGroup(
                        childNodes,
                        name = element.xmlId.ifEmpty { null },
                        // TODO: implement other g feature
                    )
                )
            },
            onOtherNode = {
                val currentNodes = currentNodesStack.last()
                // other node
            }
        )
        rootNodes.add(
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
                        ImageVector.Brush.SolidColor(ImageVector.Color("0xFF000000")),
                        1f,
                        ImageVector.Brush.SolidColor(ImageVector.Color("0xFF000000")),
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
        rootNodes.add(
            ImageVector.VectorNode.VectorPath(
                listOf(
                    ImageVector.PathNode.HorizontalTo(10f),
                    ImageVector.PathNode.VerticalTo(99f)
                ),
                ImageVector.PathFillType.EvenOdd,
                "path2",
                ImageVector.Brush.SolidColor(ImageVector.Color("0xFF000000")),
                1f,
                ImageVector.Brush.SolidColor(ImageVector.Color("0xFF000000")),
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
        if (viewBoxX != 0f || viewBoxY != 0f) {
            // translate by viewBox
            rootNodes = mutableListOf(
                ImageVector.VectorNode.VectorGroup(
                    nodes = rootNodes,
                    translationX = if (viewBoxX != 0f) -viewBoxX else null,
                    translationY = if (viewBoxY != 0f) -viewBoxY else null,
                    clipPathData = emptyList(),
                )
            )
        }
        val imageVector = ImageVector(
            name = "IconName",
            defaultWidth = width.toDouble(),
            defaultHeight = height.toDouble(),
            viewportWidth = viewBoxWidth,
            viewportHeight = viewBoxHeight,
            autoMirror = false,
            nodes = rootNodes.toList()
        )
        return imageVector
    }

    private fun SVGOMElement.traverse(
        onGroupBegin: (element: SVGOMGElement) -> Unit,
        onGroupEnd: (element: SVGOMGElement) -> Unit,
        onOtherNode: () -> Unit,
    ) {
        var child = (this.firstElementChild as? SVGOMElement)
        while (child != null) {
            when (child) {
                is SVGOMGElement -> {
                    onGroupBegin(child)
                    child.traverse(onGroupBegin, onGroupEnd, onOtherNode)
                    onGroupEnd(child)
                }
                is SVGOMPathElement -> {
                    onOtherNode()
                }
                else -> {}
            }
            child = (child.nextElementSibling as? SVGOMElement)
        }
    }
}
