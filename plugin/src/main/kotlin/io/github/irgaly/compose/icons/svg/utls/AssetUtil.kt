/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/sdk-common/src/main/java/com/android/ide/common/util/AssetUtil.java
 *
 * Copyright (C) 2011 The Android Open Source Project
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
package io.github.irgaly.compose.icons.svg.utls

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Paint
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp
import java.awt.image.ConvolveOp
import java.awt.image.Kernel
import java.awt.image.Raster
import java.awt.image.RescaleOp
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * A set of utility classes for manipulating [BufferedImage] objects and drawing them to
 * [Graphics2D] canvases.
 */
internal object AssetUtil {
    /**
     * Scales the given rectangle by the given scale factor.
     *
     * @param rect        The rectangle to scale.
     * @param scaleFactor The factor to scale by.
     * @return The scaled rectangle.
     */
    fun scaleRectangle(rect: Rectangle, scaleFactor: Float): Rectangle {
        return Rectangle(
            Math.round(rect.x * scaleFactor),
            Math.round(rect.y * scaleFactor),
            Math.round(rect.width * scaleFactor),
            Math.round(rect.height * scaleFactor)
        )
    }

    /**
     * Creates a new ARGB [BufferedImage] of the given width and height.
     *
     * @param width  The width of the new image.
     * @param height The height of the new image.
     * @return The newly created image.
     */
    fun newArgbBufferedImage(width: Int, height: Int): BufferedImage {
        return BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    }

    /**
     * Smoothly scales the given [BufferedImage] to the given width and height using the
     * [Image.SCALE_SMOOTH] algorithm (generally bicubic resampling or bilinear filtering).
     *
     * @param source The source image.
     * @param width  The destination width to scale to.
     * @param height The destination height to scale to.
     * @return A new, scaled image.
     */
    fun scaledImage(source: BufferedImage, width: Int, height: Int): BufferedImage {
        val scaledImage = source.getScaledInstance(width, height, Image.SCALE_SMOOTH)
        val scaledBufImage = BufferedImage(
            width, height,
            BufferedImage.TYPE_INT_ARGB
        )
        val g: Graphics = scaledBufImage.createGraphics()
        g.drawImage(scaledImage, 0, 0, null)
        g.dispose()
        return scaledBufImage
    }

    /**
     * Applies a gaussian blur of the given radius to the given [BufferedImage] using a kernel
     * convolution.
     *
     * @param source The source image.
     * @param radius The blur radius, in pixels.
     * @return A new, blurred image, or the source image if no blur is performed.
     */
    fun blurredImage(source: BufferedImage, radius: Double): BufferedImage {
        if (radius == 0.0) {
            return source
        }

        val r = ceil(radius).toInt()
        val rows = r * 2 + 1
        val kernelData = FloatArray(rows * rows)

        val sigma = radius / 3
        val sigma22 = 2 * sigma * sigma
        val sqrtPiSigma22 = sqrt(Math.PI * sigma22)
        val radius2 = radius * radius

        var total = 0.0
        var index = 0
        var distance2: Double

        var x: Int
        var y = -r
        while (y <= r) {
            x = -r
            while (x <= r) {
                distance2 = 1.0 * x * x + 1.0 * y * y
                if (distance2 > radius2) {
                    kernelData[index] = 0f
                } else {
                    kernelData[index] = (exp(-distance2 / sigma22) / sqrtPiSigma22).toFloat()
                }
                total += kernelData[index].toDouble()
                ++index
                x++
            }
            y++
        }

        index = 0
        while (index < kernelData.size) {
            kernelData[index] /= total.toFloat()
            index++
        }

        // We first pad the image so the kernel can operate at the edges.
        val paddedSource = paddedImage(source, r)
        val blurredPaddedImage = operatedImage(
            paddedSource, ConvolveOp(
                Kernel(rows, rows, kernelData), ConvolveOp.EDGE_ZERO_FILL, null
            )
        )
        return blurredPaddedImage.getSubimage(r, r, source.width, source.height)
    }

    /**
     * Inverts the alpha channel of the given [BufferedImage]. RGB data for the inverted area
     * are undefined, so it's generally best to fill the resulting image with a color.
     *
     * @param source The source image.
     * @return A new image with an alpha channel inverted from the original.
     */
    fun invertedAlphaImage(source: BufferedImage): BufferedImage {
        val scaleFactors = floatArrayOf(1f, 1f, 1f, -1f)
        val offsets = floatArrayOf(0f, 0f, 0f, 255f)

        return operatedImage(source, RescaleOp(scaleFactors, offsets, null))
    }

    /**
     * Applies a [BufferedImageOp] on the given [BufferedImage].
     *
     * @param source The source image.
     * @param op     The operation to perform.
     * @return A new image with the operation performed.
     */
    fun operatedImage(source: BufferedImage, op: BufferedImageOp?): BufferedImage {
        val newImage = newArgbBufferedImage(source.width, source.height)
        val g = newImage.graphics as Graphics2D
        g.drawImage(source, op, 0, 0)
        return newImage
    }

    /**
     * Fills the given [BufferedImage] with a [Paint], preserving its alpha channel.
     *
     * @param source The source image.
     * @param paint  The paint to fill with.
     * @return A new, painted/filled image.
     */
    fun filledImage(source: BufferedImage, paint: Paint?): BufferedImage {
        val newImage = newArgbBufferedImage(source.width, source.height)
        val g = newImage.graphics as Graphics2D
        g.drawImage(source, 0, 0, null)
        g.composite = AlphaComposite.SrcAtop
        g.paint = paint
        g.fillRect(0, 0, source.width, source.height)
        return newImage
    }

    /**
     * Pads the given [BufferedImage] on all sides by the given padding amount.
     *
     * @param source  The source image.
     * @param padding The amount to pad on all sides, in pixels.
     * @return A new, padded image, or the source image if no padding is performed.
     */
    fun paddedImage(source: BufferedImage, padding: Int): BufferedImage {
        if (padding == 0) {
            return source
        }

        val newImage = newArgbBufferedImage(
            source.width + padding * 2, source.height + padding * 2
        )
        val g = newImage.graphics as Graphics2D
        g.drawImage(source, padding, padding, null)
        return newImage
    }

    /**
     * Trims the transparent pixels from the given [BufferedImage] (returns a sub-image).
     *
     * @param source The source image.
     * @return A new, trimmed image, or the source image if no trim is performed.
     */
    fun trimmedImage(source: BufferedImage): BufferedImage {
        val minAlpha = 1
        val srcWidth = source.width
        val srcHeight = source.height
        val raster: Raster = source.raster
        var l = srcWidth
        var t = srcHeight
        var r = 0
        var b = 0

        var alpha: Int
        var x: Int
        val pixel = IntArray(4)
        var y = 0
        while (y < srcHeight) {
            x = 0
            while (x < srcWidth) {
                raster.getPixel(x, y, pixel)
                alpha = pixel[3]
                if (alpha >= minAlpha) {
                    l = min(x.toDouble(), l.toDouble()).toInt()
                    t = min(y.toDouble(), t.toDouble()).toInt()
                    r = max(x.toDouble(), r.toDouble()).toInt()
                    b = max(y.toDouble(), b.toDouble()).toInt()
                }
                x++
            }
            y++
        }

        if (l > r || t > b) {
            // No pixels, couldn't trim
            return source
        }

        return source.getSubimage(l, t, r - l + 1, b - t + 1)
    }

    /**
     * Draws the given [BufferedImage] to the canvas, at the given coordinates, with the given
     * [Effect]s applied. Note that drawn effects may be outside the bounds of the source
     * image.
     *
     * @param g       The destination canvas.
     * @param source  The source image.
     * @param x       The x offset at which to draw the image.
     * @param y       The y offset at which to draw the image.
     * @param effects The list of effects to apply.
     */
    fun drawEffects(
        g: Graphics2D, source: BufferedImage, x: Int, y: Int,
        effects: Array<Effect?>
    ) {
        val shadowEffects: MutableList<ShadowEffect> = ArrayList()
        val fillEffects: MutableList<FillEffect> = ArrayList()

        for (effect in effects) {
            if (effect is ShadowEffect) {
                shadowEffects.add(effect)
            } else if (effect is FillEffect) {
                fillEffects.add(effect)
            }
        }

        val oldComposite = g.composite
        for (effect in shadowEffects) {
            if (effect.inner) {
                continue
            }

            // Outer shadow
            g.composite = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, effect.opacity.toFloat()
            )
            g.drawImage(
                filledImage(
                    blurredImage(source, effect.radius),
                    effect.color
                ),
                effect.xOffset.toInt(), effect.yOffset.toInt(), null
            )
        }
        g.composite = oldComposite

        // Inner shadow & fill effects.
        val imageRect = Rectangle(0, 0, source.width, source.height)
        val out = newArgbBufferedImage(imageRect.width, imageRect.height)
        val g2 = out.graphics as Graphics2D
        var fillOpacity = 1.0

        g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
        g2.drawImage(source, 0, 0, null)
        g2.composite = AlphaComposite.SrcAtop

        // Gradient fill
        for (effect in fillEffects) {
            g2.paint = effect.paint
            g2.fillRect(0, 0, imageRect.width, imageRect.height)
            fillOpacity = max(0.0, min(1.0, effect.opacity))
        }

        // Inner shadows
        for (effect in shadowEffects) {
            if (!effect.inner) {
                continue
            }

            val innerShadowImage = newArgbBufferedImage(
                imageRect.width, imageRect.height
            )
            val g3 = innerShadowImage.graphics as Graphics2D
            g3.drawImage(source, effect.xOffset.toInt(), effect.yOffset.toInt(), null)
            g2.composite = AlphaComposite.getInstance(
                AlphaComposite.SRC_ATOP, effect.opacity.toFloat()
            )
            g2.drawImage(
                filledImage(
                    blurredImage(invertedAlphaImage(innerShadowImage), effect.radius),
                    effect.color
                ),
                0, 0, null
            )
        }

        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fillOpacity.toFloat())
        g.drawImage(out, x, y, null)
        g.composite = oldComposite
    }

    /**
     * Draws the given [BufferedImage] to the canvas, centered, wholly contained within the
     * bounds defined by the destination rectangle, and with preserved aspect ratio.
     *
     * @param g       The destination canvas.
     * @param source  The source image.
     * @param dstRect The destination rectangle in the destination canvas into which to draw the
     * image.
     */
    fun drawCenterInside(g: Graphics2D, source: BufferedImage, dstRect: Rectangle) {
        val srcWidth = source.width
        val srcHeight = source.height
        if (srcWidth * 1.0 / srcHeight > dstRect.width * 1.0 / dstRect.height) {
            val scaledWidth = max(1.0, dstRect.width.toDouble()).toInt()
            val scaledHeight = max(1.0, (dstRect.width * srcHeight / srcWidth).toDouble())
                .toInt()
            val scaledImage: Image = scaledImage(source, scaledWidth, scaledHeight)
            g.drawImage(
                scaledImage,
                dstRect.x,
                dstRect.y + (dstRect.height - scaledHeight) / 2,
                dstRect.x + dstRect.width,
                dstRect.y + (dstRect.height - scaledHeight) / 2 + scaledHeight,
                0,
                0,
                0 + scaledWidth,
                0 + scaledHeight,
                null
            )
        } else {
            val scaledWidth = max(1.0, (dstRect.height * srcWidth / srcHeight).toDouble())
                .toInt()
            val scaledHeight = max(1.0, dstRect.height.toDouble()).toInt()
            val scaledImage: Image = scaledImage(source, scaledWidth, scaledHeight)
            g.drawImage(
                scaledImage,
                dstRect.x + (dstRect.width - scaledWidth) / 2,
                dstRect.y,
                dstRect.x + (dstRect.width - scaledWidth) / 2 + scaledWidth,
                dstRect.y + dstRect.height,
                0,
                0,
                0 + scaledWidth,
                0 + scaledHeight,
                null
            )
        }
    }

    /**
     * Draws the given [BufferedImage] to the canvas, centered and cropped to fill the
     * bounds defined by the destination rectangle, and with preserved aspect ratio.
     *
     * @param g       The destination canvas.
     * @param source  The source image.
     * @param dstRect The destination rectangle in the destination canvas into which to draw the
     * image.
     */
    fun drawCenterCrop(g: Graphics2D, source: BufferedImage, dstRect: Rectangle) {
        val srcWidth = source.width
        val srcHeight = source.height
        if (srcWidth * 1.0 / srcHeight > dstRect.width * 1.0 / dstRect.height) {
            val scaledWidth = dstRect.height * srcWidth / srcHeight
            val scaledHeight = dstRect.height
            val scaledImage: Image = scaledImage(source, scaledWidth, scaledHeight)
            g.drawImage(
                scaledImage,
                dstRect.x,
                dstRect.y,
                dstRect.x + dstRect.width,
                dstRect.y + dstRect.height,
                0 + (scaledWidth - dstRect.width) / 2,
                0,
                0 + (scaledWidth - dstRect.width) / 2 + dstRect.width,
                0 + dstRect.height,
                null
            )
        } else {
            val scaledWidth = dstRect.width
            val scaledHeight = dstRect.width * srcHeight / srcWidth
            val scaledImage: Image = scaledImage(source, scaledWidth, scaledHeight)
            g.drawImage(
                scaledImage,
                dstRect.x,
                dstRect.y,
                dstRect.x + dstRect.width,
                dstRect.y + dstRect.height,
                0,
                0 + (scaledHeight - dstRect.height) / 2,
                0 + dstRect.width,
                0 + (scaledHeight - dstRect.height) / 2 + dstRect.height,
                null
            )
        }
    }

    /**
     * An effect to apply in
     * [AssetUtil.drawEffects]
     */
    abstract class Effect

    /**
     * An inner or outer shadow.
     */
    class ShadowEffect(
        var xOffset: Double, var yOffset: Double, var radius: Double, var color: Color,
        var opacity: Double, var inner: Boolean
    ) : Effect()

    /**
     * A fill, defined by a paint.
     */
    class FillEffect : Effect {
        var paint: Paint
        var opacity: Double

        constructor(paint: Paint, opacity: Double) {
            this.paint = paint
            this.opacity = opacity
        }

        constructor(paint: Paint) {
            this.paint = paint
            this.opacity = 1.0
        }
    }
}
