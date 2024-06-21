/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/sdk-common/src/main/java/com/android/ide/common/vectordrawable/SvgLeafNode.java
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
import org.w3c.dom.Node
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.math.max
import kotlin.math.min

/**
 * Represent a SVG file's leave element.
 */
internal class SvgLeafNode(
    svgTree: SvgTree,
    node: Node?,
    nodeName: String?
) : SvgNode(svgTree, node, nodeName) {
    private var mPathData: String? = null

    // Key is the attributes for vector drawable, and the value is the converted from SVG.
    private val mVdAttributesMap = HashMap<String, String>()

    private fun getAttributeValues(presentationMap: ImmutableMap<String, String>): String {
        val sb = StringBuilder("/>\n")
        for (key in mVdAttributesMap.keys) {
            val vectorDrawableAttr = presentationMap[key]
            val svgValue = mVdAttributesMap[key]
            var vdValue: String? = svgValue!!.trim { it <= ' ' }
            // There are several cases we need to convert from SVG format to
            // VectorDrawable format. Like "none", "3px" or "rgb(255, 0, 0)"
            if ("none" == vdValue) {
                vdValue = "#00000000"
            } else if (vdValue!!.endsWith("px")) {
                vdValue = vdValue.substring(0, vdValue.length - 2)
            } else if (vdValue.startsWith("rgb")) {
                vdValue = vdValue.substring(3, vdValue.length)
                vdValue = convertRGBToHex(vdValue)
                if (vdValue == null) {
                    tree.logErrorLine(
                        "Unsupported Color format $vdValue", documentNode,
                        SvgTree.SvgLogLevel.ERROR
                    )
                }
            }
            val attr = """
        $vectorDrawableAttr="$vdValue""""
            sb.insert(0, attr)
        }
        return sb.toString()
    }

    /**
     * SVG allows using rgb(int, int, int) or rgb(float%, float%, float%) to
     * represent a color, but Android doesn't. Therefore, we need to convert
     * them into #RRGGBB format.
     * @param svgValue in either "(int, int, int)" or "(float%, float%, float%)"
     * @return #RRGGBB in hex format, or null, if an error is found.
     */
    private fun convertRGBToHex(svgValue: String): String? {
        // We don't support color keyword yet.
        // http://www.w3.org/TR/SVG11/types.html#ColorKeywords
        var result: String? = null
        var functionValue = svgValue.trim { it <= ' ' }
        functionValue = svgValue.substring(1, functionValue.length - 1)
        // After we cut the "(", ")", we can deal with the numbers.
        val numbers =
            functionValue.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (numbers.size != 3) {
            return null
        }
        val color = IntArray(3)
        for (i in 0..2) {
            var number = numbers[i]
            number = number.trim { it <= ' ' }
            if (number.endsWith("%")) {
                val value = number.substring(0, number.length - 1).toFloat()
                color[i] = clamp((value * 255.0f / 100.0f).toInt(), 0, 255)
            } else {
                val value = number.toInt()
                color[i] = clamp(value, 0, 255)
            }
        }
        val builder = StringBuilder()
        builder.append("#")
        for (i in 0..2) {
            builder.append(String.format("%02X", color[i]))
        }
        result = builder.toString()
        assert(result.length == 7)
        return result
    }

    override fun dumpNode(indent: String) {
        logger.log(
            Level.FINE, indent + (if (mPathData != null) mPathData else " null pathData ") +
                    (if (name != null) name else " null name ")
        )
    }

    fun setPathData(pathData: String?) {
        mPathData = pathData
    }

    override val isGroupNode: Boolean
        get() = false

    override fun transform(a: Float, b: Float, c: Float, d: Float, e: Float, f: Float) {
        if ("none" == mVdAttributesMap["fill"] || (mPathData == null)) {
            // Nothing to draw and transform, early return.
            return
        }
        // TODO: We need to just apply the transformation to group.
        val n: Array<VdPath.Node> =
            VdParser.parsePath(
                mPathData!!
            )
        if (!(a == 1f && d == 1f && b == 0f && c == 0f && e == 0f && f == 0f)) {
            VdPath.Node.transform(
                a,
                b,
                c,
                d,
                e,
                f,
                n
            )
        }
        mPathData = VdPath.Node.NodeListToString(n)
    }

    @Throws(IOException::class)
    override fun writeXML(writer: OutputStreamWriter) {
        val fillColor = mVdAttributesMap[Svg2Vector.SVG_FILL_COLOR]
        val strokeColor = mVdAttributesMap[Svg2Vector.SVG_STROKE_COLOR]
        logger.log(Level.FINE, "fill color $fillColor")
        val emptyFill = fillColor != null && ("none" == fillColor || "#0000000" == fillColor)
        val emptyStroke = strokeColor == null || "none" == strokeColor
        val emptyPath = mPathData == null
        val nothingToDraw = emptyPath || emptyFill && emptyStroke
        if (nothingToDraw) {
            return
        }

        writer.write("    <path\n")
        if (!mVdAttributesMap.containsKey(Svg2Vector.SVG_FILL_COLOR)) {
            logger.log(Level.FINE, "ADDING FILL SVG_FILL_COLOR")
            writer.write("        android:fillColor=\"#FF000000\"\n")
        }
        writer.write("        android:pathData=\"$mPathData\"")
        writer.write(getAttributeValues(Svg2Vector.presentationMap))
    }

    fun fillPresentationAttributes(name: String, value: String) {
        logger.log(Level.FINE, ">>>> PROP $name = $value")
        if (value.startsWith("url(")) {
            tree.logErrorLine(
                "Unsupported URL value: $value", documentNode,
                SvgTree.SvgLogLevel.ERROR
            )
            return
        }
        mVdAttributesMap[name] = value
    }

    companion object {
        private val logger: Logger = Logger.getLogger(SvgLeafNode::class.java.simpleName)

        fun clamp(`val`: Int, min: Int, max: Int): Int {
            return max(min.toDouble(), min(max.toDouble(), `val`.toDouble()))
                .toInt()
        }
    }
}
