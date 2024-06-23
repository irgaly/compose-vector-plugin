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
        val nodes = mutableListOf<ImageVector.Node>()
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
