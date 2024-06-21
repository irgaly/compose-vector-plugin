/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/sdk-common/src/main/java/com/android/ide/common/vectordrawable/Svg2Vector.java
 *
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.irgaly.compose.icons.svg

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Sets
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Converts SVG to VectorDrawable's XML
 */
internal object Svg2Vector {
    private val logger: Logger = Logger.getLogger(Svg2Vector::class.java.simpleName)
    const val SVG_POLYGON: String = "polygon"
    const val SVG_RECT: String = "rect"
    const val SVG_CIRCLE: String = "circle"
    const val SVG_LINE: String = "line"
    const val SVG_PATH: String = "path"
    const val SVG_GROUP: String = "g"
    const val SVG_TRANSFORM: String = "transform"
    const val SVG_WIDTH: String = "width"
    const val SVG_HEIGHT: String = "height"
    const val SVG_VIEW_BOX: String = "viewBox"
    const val SVG_STYLE: String = "style"
    const val SVG_DISPLAY: String = "display"
    const val SVG_D: String = "d"
    const val SVG_STROKE_COLOR: String = "stroke"
    const val SVG_STROKE_OPACITY: String = "stroke-opacity"
    const val SVG_STROKE_LINEJOINE: String = "stroke-linejoin"
    const val SVG_STROKE_LINECAP: String = "stroke-linecap"
    const val SVG_STROKE_WIDTH: String = "stroke-width"
    const val SVG_FILL_COLOR: String = "fill"
    const val SVG_FILL_OPACITY: String = "fill-opacity"
    const val SVG_OPACITY: String = "opacity"
    const val SVG_CLIP: String = "clip"
    const val SVG_POINTS: String = "points"
    val presentationMap: ImmutableMap<String, String> = ImmutableMap.builder<String, String>()
        .put(SVG_STROKE_COLOR, "android:strokeColor")
        .put(SVG_STROKE_OPACITY, "android:strokeAlpha")
        .put(SVG_STROKE_LINEJOINE, "android:strokeLinejoin")
        .put(SVG_STROKE_LINECAP, "android:strokeLinecap")
        .put(SVG_STROKE_WIDTH, "android:strokeWidth")
        .put(SVG_FILL_COLOR, "android:fillColor")
        .put(SVG_FILL_OPACITY, "android:fillAlpha")
        .put(SVG_CLIP, "android:clip").put(SVG_OPACITY, "android:fillAlpha")
        .build()

    // List all the Svg nodes that we don't support. Categorized by the types.
    private val unsupportedSvgNodes: HashSet<String> = Sets.newHashSet( // Animation elements
        "animate",
        "animateColor",
        "animateMotion",
        "animateTransform",
        "mpath",
        "set",  // Container elements
        "a",
        "defs",
        "glyph",
        "marker",
        "mask",
        "missing-glyph",
        "pattern",
        "switch",
        "symbol",  // Filter primitive elements
        "feBlend",
        "feColorMatrix",
        "feComponentTransfer",
        "feComposite",
        "feConvolveMatrix",
        "feDiffuseLighting",
        "feDisplacementMap",
        "feFlood",
        "feFuncA",
        "feFuncB",
        "feFuncG",
        "feFuncR",
        "feGaussianBlur",
        "feImage",
        "feMerge",
        "feMergeNode",
        "feMorphology",
        "feOffset",
        "feSpecularLighting",
        "feTile",
        "feTurbulence",  // Font elements
        "font",
        "font-face",
        "font-face-format",
        "font-face-name",
        "font-face-src",
        "font-face-uri",
        "hkern",
        "vkern",  // Gradient elements
        "linearGradient",
        "radialGradient",
        "stop",  // Graphics elements
        "ellipse",
        "polyline",
        "text",
        "use",  // Light source elements
        "feDistantLight",
        "fePointLight",
        "feSpotLight",  // Structural elements
        "defs",
        "symbol",
        "use",  // Text content elements
        "altGlyph",
        "altGlyphDef",
        "altGlyphItem",
        "glyph",
        "glyphRef",
        "textPath",
        "text",
        "tref",
        "tspan",  // Text content child elements
        "altGlyph",
        "textPath",
        "tref",
        "tspan",  // Uncategorized elements
        "clipPath",
        "color-profile",
        "cursor",
        "filter",
        "foreignObject",
        "script",
        "view"
    )

    @Throws(Exception::class)
    private fun parse(f: File): SvgTree {
        val svgTree: SvgTree = SvgTree()
        val doc: Document = svgTree.parse(f)
        // Parse svg elements
        val nSvgNode = doc.getElementsByTagName("svg")
        check(nSvgNode.length == 1) { "Not a proper SVG file" }
        val rootNode = nSvgNode.item(0)
        for (i in 0 until nSvgNode.length) {
            val nNode = nSvgNode.item(i)
            if (nNode.nodeType == Node.ELEMENT_NODE) {
                parseDimension(svgTree, nNode)
            }
        }
        if (svgTree.viewBox == null) {
            svgTree.logErrorLine(
                "Missing \"viewBox\" in <svg> element",
                rootNode,
                SvgTree.SvgLogLevel.ERROR
            )
            return svgTree
        }
        if ((svgTree.w == 0f || svgTree.h == 0f) && (svgTree.viewBox!![2] > 0) && (svgTree.viewBox!![3] > 0) ) {
            svgTree.w = svgTree.viewBox!![2]
            svgTree.h = svgTree.viewBox!![3]
        }
        svgTree.matrix = FloatArray(6)
        svgTree.matrix!![0] = 1f
        svgTree.matrix!![3] = 1f
        // Parse transformation information.
        // TODO: Properly handle transformation in the group level. In the "use" case, we treat
        // it as global for now.
        val nUseTags = doc.getElementsByTagName("use")
        for (temp in 0 until nUseTags.length) {
            val nNode = nUseTags.item(temp)
            if (nNode.nodeType == Node.ELEMENT_NODE) {
                parseTransformation(svgTree, nNode)
            }
        }
        val root: SvgGroupNode = SvgGroupNode(svgTree, rootNode, "root")
        svgTree.root = root
        // Parse all the group and path node recursively.
        traverseSVGAndExtract(svgTree, root, rootNode)
        svgTree.dump(root)
        return svgTree
    }

    private fun traverseSVGAndExtract(svgTree: SvgTree, currentGroup: SvgGroupNode, item: Node) {
        // Recursively traverse all the group and path nodes
        val allChildren = item.childNodes
        for (i in 0 until allChildren.length) {
            val currentNode = allChildren.item(i)
            val nodeName = currentNode.nodeName
            if (SVG_PATH == nodeName || SVG_RECT == nodeName || SVG_CIRCLE == nodeName || SVG_POLYGON == nodeName || SVG_LINE == nodeName) {
                val child: SvgLeafNode = SvgLeafNode(svgTree, currentNode, nodeName + i)
                extractAllItemsAs(svgTree, child, currentNode)
                currentGroup.addChild(child)
            } else if (SVG_GROUP == nodeName) {
                val childGroup: SvgGroupNode = SvgGroupNode(svgTree, currentNode, "child$i")
                currentGroup.addChild(childGroup)
                traverseSVGAndExtract(svgTree, childGroup, currentNode)
            } else {
                // For other fancy tags, like <refs>, they can contain children too.
                // Report the unsupported nodes.
                if (unsupportedSvgNodes.contains(nodeName)) {
                    svgTree.logErrorLine(
                        "<$nodeName> is not supported", currentNode,
                        SvgTree.SvgLogLevel.ERROR
                    )
                }
                traverseSVGAndExtract(svgTree, currentGroup, currentNode)
            }
        }
    }

    private fun parseTransformation(avg: SvgTree, nNode: Node) {
        val a = nNode.attributes
        val len = a.length
        for (i in 0 until len) {
            val n = a.item(i)
            val name = n.nodeName
            var value = n.nodeValue
            if (SVG_TRANSFORM == name) {
                if (value.startsWith("matrix(")) {
                    value = value.substring("matrix(".length, value.length - 1)
                    val sp =
                        value.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (j in sp.indices) {
                        avg.matrix!![j] = sp[j].toFloat()
                    }
                }
            } else if (name == "y") {
                value.toFloat()
            } else if (name == "x") {
                value.toFloat()
            }
        }
    }

    private fun parseDimension(avg: SvgTree, nNode: Node) {
        val a = nNode.attributes
        val len = a.length
        for (i in 0 until len) {
            val n = a.item(i)
            val name = n.nodeName
            val value = n.nodeValue
            var subStringSize = value.length
            if (subStringSize > 2) {
                if (value.endsWith("px")) {
                    subStringSize = subStringSize - 2
                }
            }
            if (SVG_WIDTH == name) {
                avg.w = value.substring(0, subStringSize).toFloat()
            } else if (SVG_HEIGHT == name) {
                avg.h = value.substring(0, subStringSize).toFloat()
            } else if (SVG_VIEW_BOX == name) {
                avg.viewBox = FloatArray(4)
                val strbox =
                    value.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (j in 0 until avg.viewBox!!.size) {
                    avg.viewBox!![j] = strbox[j].toFloat()
                }
            }
        }
        if (avg.viewBox == null && avg.w != 0f && avg.h != 0f) {
            avg.viewBox = FloatArray(4)
            avg.viewBox!![2] = avg.w
            avg.viewBox!![3] = avg.h
        }
    }

    // Read the content from currentItem, and fill into "child"
    private fun extractAllItemsAs(avg: SvgTree, child: SvgLeafNode, currentItem: Node) {
        var currentGroup = currentItem.parentNode
        var hasNodeAttr = false
        var styleContent = ""
        var nothingToDisplay = false
        while (currentGroup != null && currentGroup.nodeName == "g") {
            // Parse the group's attributes.
            logger.log(Level.FINE, "Printing current parent")
            printlnCommon(currentGroup)
            val attr = currentGroup.attributes
            val nodeAttr = attr.getNamedItem(SVG_STYLE)
            // Search for the "display:none", if existed, then skip this item.
            if (nodeAttr != null) {
                styleContent += nodeAttr.textContent + ";"
                logger.log(Level.FINE, "styleContent is :" + styleContent + "at number group ")
                if (styleContent.contains("display:none")) {
                    logger.log(Level.FINE, "Found none style, skip the whole group")
                    nothingToDisplay = true
                    break
                } else {
                    hasNodeAttr = true
                }
            }
            val displayAttr = attr.getNamedItem(SVG_DISPLAY)
            if (displayAttr != null && "none" == displayAttr.nodeValue) {
                logger.log(Level.FINE, "Found display:none style, skip the whole group")
                nothingToDisplay = true
                break
            }
            currentGroup = currentGroup.parentNode
        }
        if (nothingToDisplay) {
            // Skip this current whole item.
            return
        }
        logger.log(Level.FINE, "Print current item")
        printlnCommon(currentItem)
        if (hasNodeAttr && styleContent != null) {
            addStyleToPath(child, styleContent)
        }
        val currentGroupNode = currentItem
        if (SVG_PATH == currentGroupNode.nodeName) {
            extractPathItem(avg, child, currentGroupNode)
        }
        if (SVG_RECT == currentGroupNode.nodeName) {
            extractRectItem(avg, child, currentGroupNode)
        }
        if (SVG_CIRCLE == currentGroupNode.nodeName) {
            extractCircleItem(avg, child, currentGroupNode)
        }
        if (SVG_POLYGON == currentGroupNode.nodeName) {
            extractPolyItem(avg, child, currentGroupNode)
        }
        if (SVG_LINE == currentGroupNode.nodeName) {
            extractLineItem(avg, child, currentGroupNode)
        }
    }

    private fun printlnCommon(n: Node) {
        logger.log(Level.FINE, " nodeName=\"" + n.nodeName + "\"")
        var `val` = n.namespaceURI
        if (`val` != null) {
            logger.log(Level.FINE, " uri=\"$`val`\"")
        }
        `val` = n.prefix
        if (`val` != null) {
            logger.log(Level.FINE, " pre=\"$`val`\"")
        }
        `val` = n.localName
        if (`val` != null) {
            logger.log(Level.FINE, " local=\"$`val`\"")
        }
        `val` = n.nodeValue
        if (`val` != null) {
            logger.log(Level.FINE, " nodeValue=")
            if (`val`.trim { it <= ' ' } == "") {
                // Whitespace
                logger.log(Level.FINE, "[WS]")
            } else {
                logger.log(Level.FINE, "\"" + n.nodeValue + "\"")
            }
        }
    }

    /**
     * Convert polygon element into a path.
     */
    private fun extractPolyItem(avg: SvgTree, child: SvgLeafNode, currentGroupNode: Node) {
        logger.log(Level.FINE, "Rect found" + currentGroupNode.textContent)
        if (currentGroupNode.nodeType == Node.ELEMENT_NODE) {
            val a = currentGroupNode.attributes
            val len = a.length
            for (itemIndex in 0 until len) {
                val n = a.item(itemIndex)
                val name = n.nodeName
                val value = n.nodeValue
                if (name == SVG_STYLE) {
                    addStyleToPath(child, value)
                } else if (presentationMap.containsKey(name)) {
                    child.fillPresentationAttributes(name, value)
                } else if (name == SVG_POINTS) {
                    val builder: PathBuilder = PathBuilder()
                    val split = value.split("[\\s,]+".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    var baseX = split[0].toFloat()
                    var baseY = split[1].toFloat()
                    builder.absoluteMoveTo(baseX, baseY)
                    var j = 2
                    while (j < split.size) {
                        val x = split[j].toFloat()
                        val y = split[j + 1].toFloat()
                        builder.relativeLineTo(x - baseX, y - baseY)
                        baseX = x
                        baseY = y
                        j += 2
                    }
                    builder.relativeClose()
                    child.setPathData(builder.toString())
                }
            }
        }
    }

    /**
     * Convert rectangle element into a path.
     */
    private fun extractRectItem(avg: SvgTree?, child: SvgLeafNode, currentGroupNode: Node) {
        logger.log(Level.FINE, "Rect found" + currentGroupNode.textContent)
        if (currentGroupNode.nodeType == Node.ELEMENT_NODE) {
            var x = 0f
            var y = 0f
            var width = Float.NaN
            var height = Float.NaN
            val a = currentGroupNode.attributes
            val len = a.length
            var pureTransparent = false
            for (j in 0 until len) {
                val n = a.item(j)
                val name = n.nodeName
                val value = n.nodeValue
                if (name == SVG_STYLE) {
                    addStyleToPath(child, value)
                    if (value.contains("opacity:0;")) {
                        pureTransparent = true
                    }
                } else if (presentationMap.containsKey(name)) {
                    child.fillPresentationAttributes(name, value)
                } else if (name == "clip-path" && value.startsWith("url(#SVGID_")) {
                } else if (name == "x") {
                    x = value.toFloat()
                } else if (name == "y") {
                    y = value.toFloat()
                } else if (name == "width") {
                    width = value.toFloat()
                } else if (name == "height") {
                    height = value.toFloat()
                } else if (name == "style") {
                }
            }
            if (!pureTransparent && avg != null && !java.lang.Float.isNaN(x) && !java.lang.Float.isNaN(
                    y
                )
                && !java.lang.Float.isNaN(width)
                && !java.lang.Float.isNaN(height)
            ) {
                // "M x, y h width v height h -width z"
                val builder: PathBuilder = PathBuilder()
                builder.absoluteMoveTo(x, y)
                builder.relativeHorizontalTo(width)
                builder.relativeVerticalTo(height)
                builder.relativeHorizontalTo(-width)
                builder.relativeClose()
                child.setPathData(builder.toString())
            }
        }
    }

    /**
     * Convert circle element into a path.
     */
    private fun extractCircleItem(avg: SvgTree?, child: SvgLeafNode, currentGroupNode: Node) {
        logger.log(Level.FINE, "circle found" + currentGroupNode.textContent)
        if (currentGroupNode.nodeType == Node.ELEMENT_NODE) {
            var cx = 0f
            var cy = 0f
            var radius = 0f
            val a = currentGroupNode.attributes
            val len = a.length
            var pureTransparent = false
            for (j in 0 until len) {
                val n = a.item(j)
                val name = n.nodeName
                val value = n.nodeValue
                if (name == SVG_STYLE) {
                    addStyleToPath(child, value)
                    if (value.contains("opacity:0;")) {
                        pureTransparent = true
                    }
                } else if (presentationMap.containsKey(name)) {
                    child.fillPresentationAttributes(name, value)
                } else if (name == "clip-path" && value.startsWith("url(#SVGID_")) {
                } else if (name == "cx") {
                    cx = value.toFloat()
                } else if (name == "cy") {
                    cy = value.toFloat()
                } else if (name == "r") {
                    radius = value.toFloat()
                }
            }
            if (!pureTransparent && avg != null && !java.lang.Float.isNaN(cx) && !java.lang.Float.isNaN(
                    cy
                )
            ) {
                // "M cx cy m -r, 0 a r,r 0 1,1 (r * 2),0 a r,r 0 1,1 -(r * 2),0"
                val builder: PathBuilder = PathBuilder()
                builder.absoluteMoveTo(cx, cy)
                builder.relativeMoveTo(-radius, 0f)
                builder.relativeArcTo(radius, radius, false, true, true, 2 * radius, 0f)
                builder.relativeArcTo(radius, radius, false, true, true, -2 * radius, 0f)
                child.setPathData(builder.toString())
            }
        }
    }

    /**
     * Convert line element into a path.
     */
    private fun extractLineItem(avg: SvgTree?, child: SvgLeafNode, currentGroupNode: Node) {
        logger.log(Level.FINE, "line found" + currentGroupNode.textContent)
        if (currentGroupNode.nodeType == Node.ELEMENT_NODE) {
            var x1 = 0f
            var y1 = 0f
            var x2 = 0f
            var y2 = 0f
            val a = currentGroupNode.attributes
            val len = a.length
            var pureTransparent = false
            for (j in 0 until len) {
                val n = a.item(j)
                val name = n.nodeName
                val value = n.nodeValue
                if (name == SVG_STYLE) {
                    addStyleToPath(child, value)
                    if (value.contains("opacity:0;")) {
                        pureTransparent = true
                    }
                } else if (presentationMap.containsKey(name)) {
                    child.fillPresentationAttributes(name, value)
                } else if (name == "clip-path" && value.startsWith("url(#SVGID_")) {
                    // TODO: Handle clip path here.
                } else if (name == "x1") {
                    x1 = value.toFloat()
                } else if (name == "y1") {
                    y1 = value.toFloat()
                } else if (name == "x2") {
                    x2 = value.toFloat()
                } else if (name == "y2") {
                    y2 = value.toFloat()
                }
            }
            if (!pureTransparent && avg != null && !java.lang.Float.isNaN(x1) && !java.lang.Float.isNaN(
                    y1
                )
                && !java.lang.Float.isNaN(x2) && !java.lang.Float.isNaN(y2)
            ) {
                // "M x1, y1 L x2, y2"
                val builder: PathBuilder = PathBuilder()
                builder.absoluteMoveTo(x1, y1)
                builder.absoluteLineTo(x2, y2)
                child.setPathData(builder.toString())
            }
        }
    }

    private fun extractPathItem(avg: SvgTree, child: SvgLeafNode, currentGroupNode: Node) {
        logger.log(Level.FINE, "Path found " + currentGroupNode.textContent)
        if (currentGroupNode.nodeType == Node.ELEMENT_NODE) {
            val eElement = currentGroupNode as Element
            val a = currentGroupNode.getAttributes()
            val len = a.length
            for (j in 0 until len) {
                val n = a.item(j)
                val name = n.nodeName
                val value = n.nodeValue
                if (name == SVG_STYLE) {
                    addStyleToPath(child, value)
                } else if (presentationMap.containsKey(name)) {
                    child.fillPresentationAttributes(name, value)
                } else if (name == SVG_D) {
                    val pathData = value.replace("(\\d)-".toRegex(), "$1,-")
                    child.setPathData(pathData)
                }
            }
        }
    }

    private fun addStyleToPath(path: SvgLeafNode, value: String?) {
        logger.log(Level.FINE, "Style found is $value")
        if (value != null) {
            val parts = value.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (k in parts.indices.reversed()) {
                val subStyle = parts[k]
                val nameValue: Array<String?> =
                    subStyle.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                if (nameValue.size == 2 && nameValue[0] != null && nameValue[1] != null) {
                    if (presentationMap.containsKey(
                            nameValue[0]
                        )
                    ) {
                        path.fillPresentationAttributes(nameValue[0]!!, nameValue[1]!!)
                    } else if (nameValue[0] == SVG_OPACITY) {
                        // TODO: This is hacky, since we don't have a group level
                        // android:opacity. This only works when the path didn't overlap.
                        path.fillPresentationAttributes(SVG_FILL_OPACITY, nameValue[1]!!)
                    }
                }
            }
        }
    }

    private const val head =
        "<vector xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"

    private fun getSizeString(w: Float, h: Float, scaleFactor: Float): String {
        val size = """        android:width="${(w * scaleFactor).toInt()}dp"
        android:height="${(h * scaleFactor).toInt()}dp"
"""
        return size
    }

    @Throws(IOException::class)
    private fun writeFile(outStream: OutputStream, svgTree: SvgTree) {
        val fw = OutputStreamWriter(outStream)
        fw.write(head)
        val finalWidth: Float = svgTree.w
        val finalHeight: Float = svgTree.h
        fw.write(getSizeString(finalWidth, finalHeight, svgTree.mScaleFactor))
        fw.write(("        android:viewportWidth=\"" + svgTree.w).toString() + "\"\n")
        fw.write(("        android:viewportHeight=\"" + svgTree.h).toString() + "\">\n")
        svgTree.normalize()
        // TODO: this has to happen in the tree mode!!!
        writeXML(svgTree, fw)
        fw.write("</vector>\n")
        fw.close()
    }

    @Throws(IOException::class)
    private fun writeXML(svgTree: SvgTree, fw: OutputStreamWriter) {
        svgTree.root!!.writeXML(fw)
    }

    /**
     * Convert a SVG file into VectorDrawable's XML content, if no error is found.
     *
     * @param inputSVG the input SVG file
     * @param outStream the converted VectorDrawable's content. This can be
     * empty if there is any error found during parsing
     * @return the error messages, which contain things like all the tags
     * VectorDrawble don't support or exception message.
     */
    fun parseSvgToXml(inputSVG: File, outStream: OutputStream): String? {
        // Write all the error message during parsing into SvgTree. and return here as getErrorLog().
        // We will also log the exceptions here.
        var errorLog: String? = null
        try {
            val svgTree: SvgTree = parse(inputSVG)
            errorLog = svgTree.errorLog
            // When there was anything in the input SVG file that we can't
            // convert to VectorDrawable, we logged them as errors.
            // After we logged all the errors, we skipped the XML file generation.
            if (svgTree.canConvertToVectorDrawable()) {
                writeFile(outStream, svgTree)
            }
        } catch (e: Exception) {
            errorLog = """
                EXCEPTION in parsing ${inputSVG.name}:
                ${e.message}
                """.trimIndent()
        }
        return errorLog
    }
}
