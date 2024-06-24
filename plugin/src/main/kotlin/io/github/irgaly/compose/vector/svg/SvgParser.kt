package io.github.irgaly.compose.vector.svg

import io.github.irgaly.compose.vector.node.ImageVector
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGOMAnimatedLength
import org.apache.batik.anim.dom.SVGOMAnimatedRect
import org.apache.batik.anim.dom.SVGOMDocument
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
        val doc = try {
            SAXSVGDocumentFactory(
                XMLResourceDescriptor.getXMLParserClassName()
            ).createDocument("xml", input) as SVGOMDocument
        } catch (error: IOException) {
            throw error
        }
        val root = doc.rootElement
        val viewBox = (root.viewBox as SVGOMAnimatedRect)
        val viewBoxX = if (viewBox.isSpecified) root.viewBox.baseVal.x else 0f
        val viewBoxY = if (viewBox.isSpecified) root.viewBox.baseVal.y else 0f
        var viewBoxWidth = if (viewBox.isSpecified) root.viewBox.baseVal.width else null
        var viewBoxHeight = if (viewBox.isSpecified) root.viewBox.baseVal.height else null
        val width = if ((root.width as SVGOMAnimatedLength).isSpecified) {
            root.width.baseVal.valueInSpecifiedUnits
        } else viewBoxWidth ?: error("svg tag has no width")
        val height = if ((root.height as SVGOMAnimatedLength).isSpecified) {
            root.height.baseVal.valueInSpecifiedUnits
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
}
