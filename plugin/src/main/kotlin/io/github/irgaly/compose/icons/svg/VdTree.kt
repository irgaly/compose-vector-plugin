/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/sdk-common/src/main/java/com/android/ide/common/vectordrawable/VdTree.java
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
import java.awt.AlphaComposite
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.geom.Path2D
import java.awt.image.BufferedImage
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.math.min

/**
 * Used to represent the whole VectorDrawable XML file's tree.
 */
internal class VdTree {
    var mCurrentGroup: VdGroup =
        VdGroup()
    var mChildren: ArrayList<VdElement?>? = null

    var baseWidth: Float = 1f
    var baseHeight: Float = 1f
    var mPortWidth: Float = 1f
    var mPortHeight: Float = 1f
    var mRootAlpha: Float = 1f

    /**
     * Ensure there is at least one animation for every path in group (linking
     * them by names) Build the "current" path based on the first group
     */
    fun parseFinish() {
        mChildren = mCurrentGroup.children
    }

    fun add(pathOrGroup: VdElement?) {
        mCurrentGroup.add(pathOrGroup)
    }

    private fun drawInternal(g: Graphics, w: Int, h: Int) {
        val scaleX = w / mPortWidth
        val scaleY = h / mPortHeight
        val minScale = min(scaleX.toDouble(), scaleY.toDouble()).toFloat()

        if (mChildren == null) {
            logger.log(Level.FINE, "no pathes")
            return
        }
        (g as Graphics2D).scale(scaleX.toDouble(), scaleY.toDouble())

        var bounds: Rectangle? = null
        for (i in mChildren!!.indices) {
            // TODO: do things differently when it is a path or group!!
            val path: VdPath = mChildren!![i] as VdPath
            logger.log(
                Level.FINE, "mCurrentPaths[" + i + "]=" + path.name +
                        Integer.toHexString(path.mFillColor)
            )
            if (mChildren!![i] != null) {
                val r = drawPath(path, g, w, h, minScale)
                if (bounds == null) {
                    bounds = r
                } else {
                    bounds.add(r)
                }
            }
        }
        logger.log(Level.FINE, "Rectangle $bounds")
        logger.log(Level.FINE, "Port  $mPortWidth,$mPortHeight")
        val right = mPortWidth - bounds!!.maxX
        val bot = mPortHeight - bounds.maxY
        logger.log(Level.FINE, "x " + bounds.minX + ", " + right)
        logger.log(Level.FINE, "y " + bounds.minY + ", " + bot)
    }

    private fun drawPath(
        path: VdPath?,
        canvas: Graphics,
        w: Int,
        h: Int,
        scale: Float
    ): Rectangle {
        val path2d: Path2D = Path2D.Double()
        val g: Graphics2D = canvas as Graphics2D
        path!!.toPath(path2d)

        // TODO: Use AffineTransform to apply group's transformation info.
        val theta = Math.toRadians(path!!.mRotate.toDouble())
        g.rotate(theta, path!!.mRotateX.toDouble(), path!!.mRotateY.toDouble())
        if (path!!.mClip) {
            logger.log(Level.FINE, "CLIP")

            g.setColor(Color.RED)
            g.fill(path2d)
        }
        if (path!!.mFillColor != 0) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g.setColor(Color(path!!.mFillColor, true))
            g.fill(path2d)
        }
        if (path!!.mStrokeColor != 0) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g.setStroke(BasicStroke(path!!.mStrokeWidth))
            g.setColor(Color(path!!.mStrokeColor, true))
            g.draw(path2d)
        }

        g.rotate(-theta, path!!.mRotateX.toDouble(), path!!.mRotateY.toDouble())
        return path2d.getBounds()
    }

    /**
     * Draw the VdTree into an image.
     * If the root alpha is less than 1.0, then draw into a temporary image,
     * then draw into the result image applying alpha blending.
     */
    fun drawIntoImage(image: BufferedImage) {
        val gFinal: Graphics2D = image.getGraphics() as Graphics2D
        val width: Int = image.getWidth()
        val height: Int = image.getHeight()
        gFinal.setColor(Color(255, 255, 255, 0))
        gFinal.fillRect(0, 0, width, height)

        val rootAlpha = mRootAlpha
        if (rootAlpha < 1.0) {
            val alphaImage: BufferedImage = AssetUtil.newArgbBufferedImage(width, height)
            val gTemp: Graphics2D = alphaImage.getGraphics() as Graphics2D
            drawInternal(gTemp, width, height)
            gFinal.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, rootAlpha))
            gFinal.drawImage(alphaImage, 0, 0, null)
            gTemp.dispose()
        } else {
            drawInternal(gFinal, width, height)
        }
        gFinal.dispose()
    }

    companion object {
        private val logger: Logger = Logger.getLogger(VdTree::class.java.simpleName)
    }
}
