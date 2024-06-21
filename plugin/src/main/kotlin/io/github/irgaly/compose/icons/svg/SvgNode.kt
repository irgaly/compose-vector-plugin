/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/sdk-common/src/main/java/com/android/ide/common/vectordrawable/SvgNode.java
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

import org.w3c.dom.Node
import java.io.IOException
import java.io.OutputStreamWriter

/**
 * Parent class for a SVG file's node, can be either group or leave element.
 */
internal abstract class SvgNode(
    svgTree: SvgTree,
    node: Node?,
    var name: String?
) {
    // Keep a reference to the tree in order to dump the error log.
    private val mSvgTree: SvgTree = svgTree

    // Use document node to get the line number for error reporting.
    val documentNode: Node? = node

    protected val tree: SvgTree
        get() = mSvgTree

    /**
     * dump the current node's debug info.
     */
    abstract fun dumpNode(indent: String)

    /**
     * Write the Node content into the VectorDrawable's XML file.
     */
    @Throws(IOException::class)
    abstract fun writeXML(writer: OutputStreamWriter)

    /**
     * @return true the node is a group node.
     */
    abstract val isGroupNode: Boolean

    /**
     * Transform the current Node with the transformation matrix.
     */
    abstract fun transform(a: Float, b: Float, c: Float, d: Float, e: Float, f: Float)
}
