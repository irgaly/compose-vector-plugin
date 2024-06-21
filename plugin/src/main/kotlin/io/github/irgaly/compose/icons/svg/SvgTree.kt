/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/sdk-common/src/main/java/com/android/ide/common/vectordrawable/SvgTree.java
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

import io.github.irgaly.compose.icons.svg.blame.SourcePosition
import com.google.common.base.Strings
import io.github.irgaly.compose.icons.svg.utls.PositionXmlParser
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.File
import java.io.FileInputStream
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Represent the SVG file in an internal data structure as a tree.
 */
internal class SvgTree {
    var w: Float = 0f
    var h: Float = 0f
    var matrix: FloatArray? = null
    var viewBox: FloatArray? = null
    var mScaleFactor: Float = 1f

    private var mRoot: SvgGroupNode? = null
    private var mFileName: String? = null

    private val mErrorLines = ArrayList<String>()

    enum class SvgLogLevel {
        ERROR,
        WARNING
    }

    @Throws(Exception::class)
    fun parse(f: File): Document {
        mFileName = f.name
        val doc: Document = PositionXmlParser.parse(FileInputStream(f), false)
        return doc
    }

    fun normalize() {
        if (matrix != null) {
            transform(matrix!![0], matrix!![1], matrix!![2], matrix!![3], matrix!![4], matrix!![5])
        }

        if (viewBox != null && (viewBox!![0] != 0f || viewBox!![1] != 0f)) {
            transform(1f, 0f, 0f, 1f, -viewBox!![0], -viewBox!![1])
        }
        logger.log(Level.FINE, "matrix=" + matrix.contentToString())
    }

    private fun transform(a: Float, b: Float, c: Float, d: Float, e: Float, f: Float) {
        mRoot!!.transform(a, b, c, d, e, f)
    }

    fun dump(root: SvgGroupNode) {
        logger.log(Level.FINE, "current file is :$mFileName")
        root.dumpNode("")
    }

    var root: SvgGroupNode?
        get() = mRoot
        set(root) {
            mRoot = root
        }

    fun logErrorLine(s: String, node: Node?, level: SvgLogLevel) {
        if (!Strings.isNullOrEmpty(s)) {
            if (node != null) {
                val position: SourcePosition = getPosition(node)
                mErrorLines.add(
                    (level.name + "@ line " + (position.startLine + 1)).toString() +
                            " " + s + "\n"
                )
            } else {
                mErrorLines.add(s)
            }
        }
    }

    val errorLog: String
        /**
         * @return Error log. Empty string if there are no errors.
         */
        get() {
            val errorBuilder = StringBuilder()
            if (!mErrorLines.isEmpty()) {
                errorBuilder.append("In $mFileName:\n")
            }
            for (log in mErrorLines) {
                errorBuilder.append(log)
            }
            return errorBuilder.toString()
        }

    /**
     * @return true when there is no error found when parsing the SVG file.
     */
    fun canConvertToVectorDrawable(): Boolean {
        return mErrorLines.isEmpty()
    }

    private fun getPosition(node: Node): SourcePosition {
        return PositionXmlParser.getPosition(node)
    }

    companion object {
        private val logger: Logger = Logger.getLogger(SvgTree::class.java.simpleName)
    }
}
