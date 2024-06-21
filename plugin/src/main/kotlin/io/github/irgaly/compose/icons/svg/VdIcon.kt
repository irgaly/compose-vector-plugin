/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/sdk-common/src/main/java/com/android/ide/common/vectordrawable/VdIcon.java
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

import io.github.irgaly.compose.icons.svg.utls.AssetUtil
import java.awt.Component
import java.awt.Rectangle
import java.net.URL
import javax.swing.Icon
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.min

/**
 * VdIcon wrap every vector drawable from Material Library into an icon.
 * All of them are shown in a table for developer to pick.
 */
internal class VdIcon(url: URL) : Icon, Comparable<VdIcon> {
    private var mVdTree: VdTree? = null
    val name: String
    val uRL: URL

    init {
        setDynamicIcon(url)
        uRL = url
        val fileName = url.file
        name = fileName.substring(fileName.lastIndexOf("/") + 1)
    }

    fun setDynamicIcon(url: URL) {
        val p: VdParser =
            VdParser()
        try {
            mVdTree = p.parse(url.openStream(), null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        // We knew all the icons from Material library are square shape.
        val minSize = min(c.width.toDouble(), c.height.toDouble())
            .toInt()
        val image: BufferedImage = AssetUtil.newArgbBufferedImage(minSize, minSize)
        mVdTree!!.drawIntoImage(image)

        // Draw in the center of the component.
        val rect = Rectangle(0, 0, c.width, c.height)
        AssetUtil.drawCenterInside(g as Graphics2D, image, rect)
    }

    override fun getIconWidth(): Int {
        return (mVdTree?.mPortWidth ?: 0f).toInt()
    }

    override fun getIconHeight(): Int {
        return (mVdTree?.mPortHeight ?: 0f).toInt()
    }

    override fun compareTo(other: VdIcon): Int {
        return name.compareTo(other.name)
    }
}
