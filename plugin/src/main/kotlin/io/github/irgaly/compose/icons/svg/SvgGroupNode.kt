/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/sdk-common/src/main/java/com/android/ide/common/vectordrawable/SvgGroupNode.java
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
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Represent a SVG file's group element.
 */
internal class SvgGroupNode(
    svgTree: SvgTree,
    docNode: Node?,
    name: String?
) : SvgNode(svgTree, docNode, name) {
    private val mChildren: ArrayList<SvgNode> = ArrayList<SvgNode>()

    fun addChild(child: SvgNode) {
        mChildren.add(child)
    }

    override fun dumpNode(indent: String) {
        // Print the current group.
        logger.log(Level.FINE, indent + "current group is :" + name)

        // Then print all the children.
        for (node in mChildren) {
            node.dumpNode(indent + INDENT_LEVEL)
        }
    }

    override val isGroupNode: Boolean
        get() = true

    override fun transform(a: Float, b: Float, c: Float, d: Float, e: Float, f: Float) {
        for (p in mChildren) {
            p.transform(a, b, c, d, e, f)
        }
    }

    @Throws(IOException::class)
    override fun writeXML(writer: OutputStreamWriter) {
        for (node in mChildren) {
            node.writeXML(writer)
        }
    }

    companion object {
        private val logger: Logger = Logger.getLogger(SvgGroupNode::class.java.simpleName)
        private const val INDENT_LEVEL = "    "
    }
}
