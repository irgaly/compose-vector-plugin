package io.github.irgaly.compose.vector.svg

import io.github.irgaly.compose.vector.node.ImageVector
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.util.XMLResourceDescriptor
import java.io.IOException
import java.io.InputStream

/**
 * SVG -> ImageVector
 */
class SvgParser {
    /**
     * @throws IOException
     */
    fun parse(input: InputStream): ImageVector {
        val doc = try {
            SAXSVGDocumentFactory(
                XMLResourceDescriptor.getXMLParserClassName()
            ).createDocument("xml", input)
        } catch (error: IOException) {
            throw error
        }
        val nodes = mutableListOf<ImageVector.VectorNode>()
        nodes.add(
            ImageVector.VectorNode.VectorGroup(
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
                listOf(
                    ImageVector.VectorNode.VectorGroup(
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
                        ),
                        emptyList()
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
                )
            )
        )
        nodes.add(
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
        val imageVector = ImageVector(
            name = "IconName",
            defaultWidth = 100.0,
            defaultHeight = 100.0,
            viewportWidth = 100f,
            viewportHeight = 100f,
            autoMirror = false,
            nodes = nodes.toList()
        )
        return imageVector
    }
}
