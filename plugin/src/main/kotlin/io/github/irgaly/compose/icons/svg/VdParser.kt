/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/sdk-common/src/main/java/com/android/ide/common/vectordrawable/VdParser.java
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

import io.github.irgaly.compose.icons.svg.android.SdkConstants
import org.xml.sax.Attributes
import org.xml.sax.ContentHandler
import org.xml.sax.InputSource
import org.xml.sax.Locator
import org.xml.sax.SAXException
import java.io.InputStream
import java.net.URL
import java.util.Locale
import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Pattern
import javax.xml.parsers.SAXParserFactory
import kotlin.math.min

/**
 * Parse a VectorDrawble's XML file, and generate an internal tree representation,
 * which can be used for drawing / previewing.
 */
internal class VdParser {
    internal interface ElemParser {
        fun parse(path: VdTree, attributes: Attributes)
    }

    var mParseSize: ElemParser = object : ElemParser {
        override fun parse(
            tree: VdTree,
            attributes: Attributes
        ) {
            parseSize(tree, attributes)
        }
    }

    var mParsePath: ElemParser = object : ElemParser {
        override fun parse(
            tree: VdTree,
            attributes: Attributes
        ) {
            val p: VdPath = parsePathAttributes(attributes)
            tree.add(p)
        }
    }

    var mParseGroup: ElemParser = object : ElemParser {
        override fun parse(
            tree: VdTree,
            attributes: Attributes
        ) {
            val g: VdGroup = parseGroupAttributes(attributes)
            tree.add(g)
        }
    }

    var tagSwitch: HashMap<String, ElemParser> = HashMap()

    init {
        tagSwitch[SHAPE_VECTOR] = mParseSize
        tagSwitch[SHAPE_PATH] = mParsePath
        tagSwitch[SHAPE_GROUP] = mParseGroup
        // TODO: add <g> tag and start to build the tree.
    }

    // Note that the incoming file is the VectorDrawable's XML file, not the SVG.
    // TODO: Use Document to parse and make sure no big performance difference.
    fun parse(
        `is`: InputStream?,
        vdErrorLog: StringBuilder?
    ): VdTree? {
        try {
            val tree: VdTree =
                VdTree()
            val spf = SAXParserFactory.newInstance()
            val sp = spf.newSAXParser()
            val xr = sp.xmlReader

            xr.contentHandler = object : ContentHandler {
                var space: String = " "

                override fun setDocumentLocator(locator: Locator) {
                }

                @Throws(SAXException::class)
                override fun startDocument() {
                }

                @Throws(SAXException::class)
                override fun endDocument() {
                }

                @Throws(SAXException::class)
                override fun startPrefixMapping(s: String, s2: String) {
                }

                @Throws(SAXException::class)
                override fun endPrefixMapping(s: String) {
                }

                @Throws(SAXException::class)
                override fun startElement(
                    s: String,
                    s2: String,
                    s3: String,
                    attributes: Attributes
                ) {
                    val name = s3
                    if (tagSwitch.containsKey(name)) {
                        tagSwitch[name]!!.parse(tree, attributes)
                    }
                    space += " "
                }

                @Throws(SAXException::class)
                override fun endElement(s: String, s2: String, s3: String) {
                    space = space.substring(1)
                }

                @Throws(SAXException::class)
                override fun characters(chars: CharArray, i: Int, i2: Int) {
                }

                @Throws(SAXException::class)
                override fun ignorableWhitespace(chars: CharArray, i: Int, i2: Int) {
                }

                @Throws(SAXException::class)
                override fun processingInstruction(s: String, s2: String) {
                }

                @Throws(SAXException::class)
                override fun skippedEntity(s: String) {
                }
            }
            xr.parse(InputSource(`is`))
            tree.parseFinish()
            return tree
        } catch (e: Exception) {
            vdErrorLog!!.append(
                """
    Exception while parsing XML file:
    ${e.message}
    """.trimIndent()
            )
            return null
        }
    }

    private class ExtractFloatResult {
        // We need to return the position of the next separator and whether the
        // next float starts with a '-' or a '.'.
        var mEndPosition: Int = 0
        var mEndWithNegOrDot: Boolean = false
    }

    @Throws(Exception::class)
    fun parse(r: URL, vdErrorLog: StringBuilder?): VdTree? {
        return parse(r.openStream(), vdErrorLog)
    }

    private fun parseSize(
        vdTree: VdTree,
        attributes: Attributes
    ) {
        val pattern = Pattern.compile("^\\s*(\\d+(\\.\\d+)*)\\s*([a-zA-Z]+)\\s*$")
        val m = HashMap<String, Int>()
        m[SdkConstants.UNIT_PX] = 1
        m[SdkConstants.UNIT_DIP] = 1
        m[SdkConstants.UNIT_DP] = 1
        m[SdkConstants.UNIT_SP] = 1
        m[SdkConstants.UNIT_PT] = 1
        m[SdkConstants.UNIT_IN] = 1
        m[SdkConstants.UNIT_MM] = 1
        val len = attributes.length

        for (i in 0 until len) {
            val name = attributes.getQName(i)
            val value = attributes.getValue(i)
            val matcher = pattern.matcher(value)
            var size = 0f
            if (matcher.matches()) {
                val v = matcher.group(1).toFloat()
                val unit = matcher.group(3).lowercase(Locale.getDefault())
                size = v
            }

            // -- Extract dimension units.
            if ("android:width" == name) {
                vdTree.baseWidth = size
            } else if ("android:height" == name) {
                vdTree.baseHeight = size
            } else if ("android:viewportWidth" == name) {
                vdTree.mPortWidth = value.toFloat()
            } else if ("android:viewportHeight" == name) {
                vdTree.mPortHeight = value.toFloat()
            } else if ("android:alpha" == name) {
                vdTree.mRootAlpha = value.toFloat()
            } else {
                continue
            }
        }
    }

    private fun parsePathAttributes(attributes: Attributes): VdPath {
        val len = attributes.length
        val vgPath: VdPath =
            VdPath()

        for (i in 0 until len) {
            val name = attributes.getQName(i)
            val value = attributes.getValue(i)
            logger.log(Level.FINE, "name " + name + "value " + value)
            setNameValue(vgPath, name, value)
        }
        return vgPath
    }

    private fun parseGroupAttributes(attributes: Attributes): VdGroup {
        val len = attributes.length
        val vgGroup: VdGroup =
            VdGroup()

        for (i in 0 until len) {
            val name = attributes.getQName(i)
            val value = attributes.getValue(i)
            logger.log(Level.FINE, "name " + name + "value " + value)
        }
        return vgGroup
    }

    fun setNameValue(
        vgPath: VdPath,
        name: String,
        value: String
    ) {
        if (PATH_DESCRIPTION == name) {
            vgPath.mNode = parsePath(value)
        } else if (PATH_ID == name) {
            vgPath.name = value
        } else if (PATH_FILL == name) {
            vgPath.mFillColor = calculateColor(value)
            if (!java.lang.Float.isNaN(vgPath.mFillOpacity)) {
                vgPath.mFillColor = vgPath.mFillColor and 0x00FFFFFF
                vgPath.mFillColor =
                    vgPath.mFillColor or (((0xFF * vgPath.mFillOpacity).toInt()) shl 24)
            }
        } else if (PATH_STROKE == name) {
            vgPath.mStrokeColor = calculateColor(value)
            if (!java.lang.Float.isNaN(vgPath.mStrokeOpacity)) {
                vgPath.mStrokeColor = vgPath.mStrokeColor and 0x00FFFFFF
                vgPath.mStrokeColor =
                    vgPath.mStrokeColor or (((0xFF * vgPath.mStrokeOpacity).toInt()) shl 24)
            }
        } else if (PATH_FILL_OPACTIY == name) {
            vgPath.mFillOpacity = value.toFloat()
            vgPath.mFillColor = vgPath.mFillColor and 0x00FFFFFF
            vgPath.mFillColor = vgPath.mFillColor or (((0xFF * vgPath.mFillOpacity).toInt()) shl 24)
        } else if (PATH_STROKE_OPACTIY == name) {
            vgPath.mStrokeOpacity = value.toFloat()
            vgPath.mStrokeColor = vgPath.mStrokeColor and 0x00FFFFFF
            vgPath.mStrokeColor =
                vgPath.mStrokeColor or (((0xFF * vgPath.mStrokeOpacity).toInt()) shl 24)
        } else if (PATH_STROKE_WIDTH == name) {
            vgPath.mStrokeWidth = value.toFloat()
        } else if (PATH_ROTATION == name) {
            vgPath.mRotate = value.toFloat()
        } else if (PATH_SHIFT_X == name) {
            vgPath.mShiftX = value.toFloat()
        } else if (PATH_SHIFT_Y == name) {
            vgPath.mShiftY = value.toFloat()
        } else if (PATH_ROTATION_Y == name) {
            vgPath.mRotateY = value.toFloat()
        } else if (PATH_ROTATION_X == name) {
            vgPath.mRotateX = value.toFloat()
        } else if (PATH_CLIP == name) {
            vgPath.mClip = value.toBoolean()
        } else if (PATH_TRIM_START == name) {
            vgPath.mTrimPathStart = value.toFloat()
        } else if (PATH_TRIM_END == name) {
            vgPath.mTrimPathEnd = value.toFloat()
        } else if (PATH_TRIM_OFFSET == name) {
            vgPath.mTrimPathOffset = value.toFloat()
        } else if (PATH_STROKE_LINECAP == name) {
            if (LINECAP_BUTT == value) {
                vgPath.mStrokeLineCap = 0
            } else if (LINECAP_ROUND == value) {
                vgPath.mStrokeLineCap = 1
            } else if (LINECAP_SQUARE == value) {
                vgPath.mStrokeLineCap = 2
            }
        } else if (PATH_STROKE_LINEJOIN == name) {
            if (LINEJOIN_MITER == value) {
                vgPath.mStrokeLineJoin = 0
            } else if (LINEJOIN_ROUND == value) {
                vgPath.mStrokeLineJoin = 1
            } else if (LINEJOIN_BEVEL == value) {
                vgPath.mStrokeLineJoin = 2
            }
        } else if (PATH_STROKE_MITERLIMIT == name) {
            vgPath.mStrokeMiterlimit = value.toFloat()
        } else {
            logger.log(Level.FINE, ">>>>>> DID NOT UNDERSTAND ! \"$name\" <<<<")
        }
    }

    private fun calculateColor(value: String): Int {
        val len = value.length
        var ret: Int
        var k = 0
        when (len) {
            7 -> {
                ret = value.substring(1).toLong(16).toInt()
                ret = ret or -0x1000000
            }

            9 -> ret = value.substring(1).toLong(16).toInt()
            4 -> {
                ret = value.substring(1).toLong(16).toInt()

                k = k or ((ret shr 8) and 0xF) * 0x110000
                k = k or ((ret shr 4) and 0xF) * 0x1100
                k = k or ((ret) and 0xF) * 0x11
                ret = k or -0x1000000
            }

            5 -> {
                ret = value.substring(1).toLong(16).toInt()
                k = k or ((ret shr 16) and 0xF) * 0x11000000
                k = k or ((ret shr 8) and 0xF) * 0x110000
                k = k or ((ret shr 4) and 0xF) * 0x1100
                k = k or ((ret) and 0xF) * 0x11
            }

            else -> return -0x1000000
        }
        logger.log(Level.FINE, "color = " + value + " = " + Integer.toHexString(ret))
        return ret
    }

    companion object {
        private val logger: Logger = Logger.getLogger(VdParser::class.java.simpleName)

        private const val PATH_SHIFT_X = "shift-x"
        private const val PATH_SHIFT_Y = "shift-y"

        private const val SHAPE_VECTOR = "vector"
        private const val SHAPE_PATH = "path"
        private const val SHAPE_GROUP = "group"

        private const val PATH_ID = "android:name"
        private const val PATH_DESCRIPTION = "android:pathData"
        private const val PATH_FILL = "android:fillColor"
        private const val PATH_FILL_OPACTIY = "android:fillAlpha"
        private const val PATH_STROKE = "android:strokeColor"
        private const val PATH_STROKE_OPACTIY = "android:strokeAlpha"

        private const val PATH_STROKE_WIDTH = "android:strokeWidth"
        private const val PATH_ROTATION = "android:rotation"
        private const val PATH_ROTATION_X = "android:pivotX"
        private const val PATH_ROTATION_Y = "android:pivotY"
        private const val PATH_TRIM_START = "android:trimPathStart"
        private const val PATH_TRIM_END = "android:trimPathEnd"
        private const val PATH_TRIM_OFFSET = "android:trimPathOffset"
        private const val PATH_STROKE_LINECAP = "android:strokeLinecap"
        private const val PATH_STROKE_LINEJOIN = "android:strokeLinejoin"
        private const val PATH_STROKE_MITERLIMIT = "android:strokeMiterlimit"
        private const val PATH_CLIP = "android:clipToPath"
        private const val LINECAP_BUTT = "butt"
        private const val LINECAP_ROUND = "round"
        private const val LINECAP_SQUARE = "square"
        private const val LINEJOIN_MITER = "miter"
        private const val LINEJOIN_ROUND = "round"
        private const val LINEJOIN_BEVEL = "bevel"

        private fun nextStart(s: String, end: Int): Int {
            var end = end
            var c: Char

            while (end < s.length) {
                c = s[end]
                // Note that 'e' or 'E' are not valid path commands, but could be
                // used for floating point numbers' scientific notation.
                // Therefore, when searching for next command, we should ignore 'e'
                // and 'E'.
                if ((((c.code - 'A'.code) * (c.code - 'Z'.code) <= 0) || ((c.code - 'a'.code) * (c.code - 'z'.code) <= 0))
                    && (c != 'e') && (c != 'E')
                ) {
                    return end
                }
                end++
            }
            return end
        }

        fun parsePath(value: String): Array<VdPath.Node> {
            var start = 0
            var end = 1

            val list: ArrayList<VdPath.Node> =
                ArrayList<VdPath.Node>()
            while (end < value.length) {
                end = nextStart(value, end)
                val s = value.substring(start, end)
                val `val` = getFloats(s)

                addNode(list, s[0], `val`)

                start = end
                end++
            }
            if ((end - start) == 1 && start < value.length) {
                addNode(list, value[start], FloatArray(0))
            }
            return list.toTypedArray<VdPath.Node>()
        }

        /**
         * Copies elements from `original` into a new array, from indexes start (inclusive) to
         * end (exclusive). The original order of elements is preserved.
         * If `end` is greater than `original.length`, the result is padded
         * with the value `0.0f`.
         *
         * @param original the original array
         * @param start the start index, inclusive
         * @param end the end index, exclusive
         * @return the new array
         * @throws ArrayIndexOutOfBoundsException if `start < 0 || start > original.length`
         * @throws IllegalArgumentException if `start > end`
         * @throws NullPointerException if `original == null`
         */
        private fun copyOfRange(original: FloatArray, start: Int, end: Int): FloatArray {
            require(start <= end)
            val originalLength = original.size
            if (start < 0 || start > originalLength) {
                throw ArrayIndexOutOfBoundsException()
            }
            val resultLength = end - start
            val copyLength = min(resultLength.toDouble(), (originalLength - start).toDouble())
                .toInt()
            val result = FloatArray(resultLength)
            System.arraycopy(original, start, result, 0, copyLength)
            return result
        }

        /**
         * Calculate the position of the next comma or space or negative sign
         * @param s the string to search
         * @param start the position to start searching
         * @param result the result of the extraction, including the position of the
         * the starting position of next number, whether it is ending with a '-'.
         */
        private fun extract(s: String, start: Int, result: ExtractFloatResult) {
            // Now looking for ' ', ',', '.' or '-' from the start.
            var currentIndex = start
            var foundSeparator = false
            result.mEndWithNegOrDot = false
            var secondDot = false
            var isExponential = false
            while (currentIndex < s.length) {
                val isPrevExponential = isExponential
                isExponential = false
                val currentChar = s[currentIndex]
                when (currentChar) {
                    ' ', ',' -> foundSeparator = true
                    '-' ->                     // The negative sign following a 'e' or 'E' is not a separator.
                        if (currentIndex != start && !isPrevExponential) {
                            foundSeparator = true
                            result.mEndWithNegOrDot = true
                        }

                    '.' -> if (!secondDot) {
                        secondDot = true
                    } else {
                        // This is the second dot, and it is considered as a separator.
                        foundSeparator = true
                        result.mEndWithNegOrDot = true
                    }

                    'e', 'E' -> isExponential = true
                }
                if (foundSeparator) {
                    break
                }
                currentIndex++
            }
            // When there is nothing found, then we put the end position to the end
            // of the string.
            result.mEndPosition = currentIndex
        }

        /**
         * parse the floats in the string this is an optimized version of parseFloat(s.split(",|\\s"));
         *
         * @param s the string containing a command and list of floats
         * @return array of floats
         */
        private fun getFloats(s: String): FloatArray {
            if (s[0] == 'z' || s[0] == 'Z') {
                return FloatArray(0)
            }
            try {
                val results = FloatArray(s.length)
                var count = 0
                var startPosition = 1
                var endPosition = 0

                val result = ExtractFloatResult()
                val totalLength = s.length

                // The startPosition should always be the first character of the
                // current number, and endPosition is the character after the current
                // number.
                while (startPosition < totalLength) {
                    extract(s, startPosition, result)
                    endPosition = result.mEndPosition

                    if (startPosition < endPosition) {
                        results[count++] = s.substring(startPosition, endPosition).toFloat()
                    }

                    startPosition = if (result.mEndWithNegOrDot) {
                        // Keep the '-' or '.' sign with next number.
                        endPosition
                    } else {
                        endPosition + 1
                    }
                }
                return copyOfRange(results, 0, count)
            } catch (e: NumberFormatException) {
                throw RuntimeException("error in parsing \"$s\"", e)
            }
        }

        // End of copy from PathParser.java
        ////////////////////////////////////////////////////////////////
        private fun addNode(
            list: ArrayList<VdPath.Node>,
            cmd: Char,
            `val`: FloatArray
        ) {
            list.add(VdPath.Node(cmd, `val`))
        }
    }
}
