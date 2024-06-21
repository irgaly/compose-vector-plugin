/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/sdk-common/src/main/java/com/android/ide/common/vectordrawable/VdNodeRender.java
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

import java.awt.geom.Path2D
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Given an array of VdPath.Node, generate a Path2D object.
 * In another word, this is the engine which converts the pathData into
 * a Path2D object, which is able to draw on Swing components.
 * The logic and math here are the same as PathParser.java in framework.
 */
internal object VdNodeRender {
    private val logger: Logger = Logger.getLogger(
        VdNodeRender::class.java
            .simpleName
    )

    fun creatPath(node: Array<VdPath.Node>, path: Path2D) {
        val current = FloatArray(6)
        var lastCmd = ' '
        for (i in node.indices) {
            addCommand(path, current, node[i].type, lastCmd, node[i].params)
            lastCmd = node[i].type
        }
    }

    private fun addCommand(
        path: Path2D, current: FloatArray, cmd: Char,
        lastCmd: Char, `val`: FloatArray
    ) {
        var lastCmd = lastCmd
        var incr = 2

        var cx = current[0]
        var cy = current[1]
        var cpx = current[2]
        var cpy = current[3]
        var loopX = current[4]
        var loopY = current[5]

        when (cmd) {
            'z', 'Z' -> {
                path.closePath()
                cx = loopX
                cy = loopY
                incr = 2
            }

            'm', 'M', 'l', 'L', 't', 'T' -> incr = 2
            'h', 'H', 'v', 'V' -> incr = 1
            'c', 'C' -> incr = 6
            's', 'S', 'q', 'Q' -> incr = 4
            'a', 'A' -> incr = 7
        }
        var k = 0
        while (k < `val`.size) {
            var reflectCtrl = false
            var tempReflectedX: Float
            var tempReflectedY: Float

            when (cmd) {
                'm' -> {
                    cx += `val`[k + 0]
                    cy += `val`[k + 1]
                    path.moveTo(cx.toDouble(), cy.toDouble())
                    loopX = cx
                    loopY = cy
                }

                'M' -> {
                    cx = `val`[k + 0]
                    cy = `val`[k + 1]
                    path.moveTo(cx.toDouble(), cy.toDouble())
                    loopX = cx
                    loopY = cy
                }

                'l' -> {
                    cx += `val`[k + 0]
                    cy += `val`[k + 1]
                    path.lineTo(cx.toDouble(), cy.toDouble())
                }

                'L' -> {
                    cx = `val`[k + 0]
                    cy = `val`[k + 1]
                    path.lineTo(cx.toDouble(), cy.toDouble())
                }

                'z', 'Z' -> {
                    path.closePath()
                    cx = loopX
                    cy = loopY
                }

                'h' -> {
                    cx += `val`[k + 0]
                    path.lineTo(cx.toDouble(), cy.toDouble())
                }

                'H' -> {
                    path.lineTo(`val`[k + 0].toDouble(), cy.toDouble())
                    cx = `val`[k + 0]
                }

                'v' -> {
                    cy += `val`[k + 0]
                    path.lineTo(cx.toDouble(), cy.toDouble())
                }

                'V' -> {
                    path.lineTo(cx.toDouble(), `val`[k + 0].toDouble())
                    cy = `val`[k + 0]
                }

                'c' -> {
                    path.curveTo(
                        (cx + `val`[k + 0]).toDouble(),
                        (cy + `val`[k + 1]).toDouble(),
                        (cx + `val`[k + 2]).toDouble(),
                        (cy + `val`[k + 3]).toDouble(),
                        (cx + `val`[k + 4]).toDouble(),
                        (cy + `val`[k + 5]).toDouble()
                    )
                    cpx = cx + `val`[k + 2]
                    cpy = cy + `val`[k + 3]
                    cx += `val`[k + 4]
                    cy += `val`[k + 5]
                }

                'C' -> {
                    path.curveTo(
                        `val`[k + 0].toDouble(),
                        `val`[k + 1].toDouble(),
                        `val`[k + 2].toDouble(),
                        `val`[k + 3].toDouble(),
                        `val`[k + 4].toDouble(),
                        `val`[k + 5].toDouble()
                    )
                    cx = `val`[k + 4]
                    cy = `val`[k + 5]
                    cpx = `val`[k + 2]
                    cpy = `val`[k + 3]
                }

                's' -> {
                    reflectCtrl =
                        (lastCmd == 'c' || lastCmd == 's' || lastCmd == 'C' || lastCmd == 'S')
                    path.curveTo(
                        (if (reflectCtrl) 2 * cx - cpx else cx).toDouble(),
                        (if (reflectCtrl) (2
                                * cy - cpy) else cy).toDouble(),
                        (cx + `val`[k + 0]).toDouble(),
                        (cy + `val`[k + 1]).toDouble(),
                        (cx
                                + `val`[k + 2]).toDouble(),
                        (cy + `val`[k + 3]).toDouble()
                    )

                    cpx = cx + `val`[k + 0]
                    cpy = cy + `val`[k + 1]
                    cx += `val`[k + 2]
                    cy += `val`[k + 3]
                }

                'S' -> {
                    reflectCtrl =
                        (lastCmd == 'c' || lastCmd == 's' || lastCmd == 'C' || lastCmd == 'S')
                    path.curveTo(
                        (if (reflectCtrl) 2 * cx - cpx else cx).toDouble(),
                        (if (reflectCtrl) (2
                                * cy - cpy) else cy).toDouble(),
                        `val`[k + 0].toDouble(),
                        `val`[k + 1].toDouble(),
                        `val`[k + 2].toDouble(),
                        `val`[k + 3].toDouble()
                    )
                    cpx = (`val`[k + 0])
                    cpy = (`val`[k + 1])
                    cx = `val`[k + 2]
                    cy = `val`[k + 3]
                }

                'q' -> {
                    path.quadTo(
                        (cx + `val`[k + 0]).toDouble(),
                        (cy + `val`[k + 1]).toDouble(),
                        (cx + `val`[k + 2]).toDouble(),
                        (cy + `val`[k + 3]).toDouble()
                    )
                    cpx = cx + `val`[k + 0]
                    cpy = cy + `val`[k + 1]
                    // Note that we have to update cpx first, since cx will be updated here.
                    cx += `val`[k + 2]
                    cy += `val`[k + 3]
                }

                'Q' -> {
                    path.quadTo(
                        `val`[k + 0].toDouble(),
                        `val`[k + 1].toDouble(),
                        `val`[k + 2].toDouble(),
                        `val`[k + 3].toDouble()
                    )
                    cx = `val`[k + 2]
                    cy = `val`[k + 3]
                    cpx = `val`[k + 0]
                    cpy = `val`[k + 1]
                }

                't' -> {
                    reflectCtrl =
                        (lastCmd == 'q' || lastCmd == 't' || lastCmd == 'Q' || lastCmd == 'T')
                    tempReflectedX = if (reflectCtrl) 2 * cx - cpx else cx
                    tempReflectedY = if (reflectCtrl) 2 * cy - cpy else cy
                    path.quadTo(
                        tempReflectedX.toDouble(),
                        tempReflectedY.toDouble(),
                        (cx + `val`[k + 0]).toDouble(),
                        (cy + `val`[k + 1]).toDouble()
                    )
                    cpx = tempReflectedX
                    cpy = tempReflectedY
                    cx += `val`[k + 0]
                    cy += `val`[k + 1]
                }

                'T' -> {
                    reflectCtrl =
                        (lastCmd == 'q' || lastCmd == 't' || lastCmd == 'Q' || lastCmd == 'T')
                    tempReflectedX = if (reflectCtrl) 2 * cx - cpx else cx
                    tempReflectedY = if (reflectCtrl) 2 * cy - cpy else cy
                    path.quadTo(
                        tempReflectedX.toDouble(),
                        tempReflectedY.toDouble(),
                        `val`[k + 0].toDouble(),
                        `val`[k + 1].toDouble()
                    )
                    cx = `val`[k + 0]
                    cy = `val`[k + 1]
                    cpx = tempReflectedX
                    cpy = tempReflectedY
                }

                'a' -> {
                    // (rx ry x-axis-rotation large-arc-flag sweep-flag x y)
                    drawArc(
                        path, cx, cy, `val`[k + 5] + cx, `val`[k + 6] + cy,
                        `val`[k + 0], `val`[k + 1], `val`[k + 2], `val`[k + 3] != 0f,
                        `val`[k + 4] != 0f
                    )
                    cx += `val`[k + 5]
                    cy += `val`[k + 6]
                    cpx = cx
                    cpy = cy
                }

                'A' -> {
                    drawArc(
                        path, cx, cy, `val`[k + 5], `val`[k + 6], `val`[k + 0],
                        `val`[k + 1], `val`[k + 2], `val`[k + 3] != 0f,
                        `val`[k + 4] != 0f
                    )
                    cx = `val`[k + 5]
                    cy = `val`[k + 6]
                    cpx = cx
                    cpy = cy
                }
            }
            lastCmd = cmd
            k += incr
        }
        current[0] = cx
        current[1] = cy
        current[2] = cpx
        current[3] = cpy
        current[4] = loopX
        current[5] = loopY
    }

    private fun drawArc(
        p: Path2D, x0: Float, y0: Float, x1: Float,
        y1: Float, a: Float, b: Float, theta: Float, isMoreThanHalf: Boolean,
        isPositiveArc: Boolean
    ) {
        logger.log(
            Level.FINE, "(" + x0 + "," + y0 + ")-(" + x1 + "," + y1
                    + ") {" + a + " " + b + "}"
        )
        /* Convert rotation angle from degrees to radians */
        val thetaD = theta * Math.PI / 180.0f
        /* Pre-compute rotation matrix entries */
        val cosTheta = cos(thetaD)
        val sinTheta = sin(thetaD)
        /* Transform (x0, y0) and (x1, y1) into unit space */
        /* using (inverse) rotation, followed by (inverse) scale */
        val x0p = (x0 * cosTheta + y0 * sinTheta) / a
        val y0p = (-x0 * sinTheta + y0 * cosTheta) / b
        val x1p = (x1 * cosTheta + y1 * sinTheta) / a
        val y1p = (-x1 * sinTheta + y1 * cosTheta) / b
        logger.log(
            Level.FINE, "unit space (" + x0p + "," + y0p + ")-(" + x1p
                    + "," + y1p + ")"
        )
        /* Compute differences and averages */
        val dx = x0p - x1p
        val dy = y0p - y1p
        val xm = (x0p + x1p) / 2
        val ym = (y0p + y1p) / 2
        /* Solve for intersecting unit circles */
        val dsq = dx * dx + dy * dy
        if (dsq == 0.0) {
            logger.log(Level.FINE, " Points are coincident")
            return  /* Points are coincident */
        }
        val disc = 1.0 / dsq - 1.0 / 4.0
        if (disc < 0.0) {
            logger.log(Level.FINE, "Points are too far apart $dsq")
            val adjust = (sqrt(dsq) / 1.99999).toFloat()
            drawArc(
                p, x0, y0, x1, y1, a * adjust, b * adjust, theta,
                isMoreThanHalf, isPositiveArc
            )
            return  /* Points are too far apart */
        }
        val s = sqrt(disc)
        val sdx = s * dx
        val sdy = s * dy
        var cx: Double
        var cy: Double
        if (isMoreThanHalf == isPositiveArc) {
            cx = xm - sdy
            cy = ym + sdx
        } else {
            cx = xm + sdy
            cy = ym - sdx
        }

        val eta0 = atan2((y0p - cy), (x0p - cx))
        logger.log(
            Level.FINE, "eta0 = Math.atan2( " + (y0p - cy) + " , "
                    + (x0p - cx) + ") = " + Math.toDegrees(eta0)
        )

        val eta1 = atan2((y1p - cy), (x1p - cx))
        logger.log(
            Level.FINE, "eta1 = Math.atan2( " + (y1p - cy) + " , "
                    + (x1p - cx) + ") = " + Math.toDegrees(eta1)
        )
        var sweep = (eta1 - eta0)
        if (isPositiveArc != (sweep >= 0)) {
            if (sweep > 0) {
                sweep -= 2 * Math.PI
            } else {
                sweep += 2 * Math.PI
            }
        }

        cx *= a.toDouble()
        cy *= b.toDouble()
        val tcx = cx
        cx = cx * cosTheta - cy * sinTheta
        cy = tcx * sinTheta + cy * cosTheta
        logger.log(
            Level.FINE,
            "cx, cy, a, b, x0, y0, thetaD, eta0, sweep = " + cx + " , "
                    + cy + " , " + a + " , " + b + " , " + x0 + " , " + y0
                    + " , " + Math.toDegrees(thetaD) + " , "
                    + Math.toDegrees(eta0) + " , " + Math.toDegrees(sweep)
        )

        arcToBezier(
            p,
            cx,
            cy,
            a.toDouble(),
            b.toDouble(),
            x0.toDouble(),
            y0.toDouble(),
            thetaD,
            eta0,
            sweep
        )
    }

    /**
     * Converts an arc to cubic Bezier segments and records them in p.
     *
     * @param p The target for the cubic Bezier segments
     * @param cx The x coordinate center of the ellipse
     * @param cy The y coordinate center of the ellipse
     * @param a The radius of the ellipse in the horizontal direction
     * @param b The radius of the ellipse in the vertical direction
     * @param e1x E(eta1) x coordinate of the starting point of the arc
     * @param e1y E(eta2) y coordinate of the starting point of the arc
     * @param theta The angle that the ellipse bounding rectangle makes with the horizontal plane
     * @param start The start angle of the arc on the ellipse
     * @param sweep The angle (positive or negative) of the sweep of the arc on the ellipse
     */
    private fun arcToBezier(
        p: Path2D, cx: Double, cy: Double, a: Double,
        b: Double, e1x: Double, e1y: Double, theta: Double, start: Double,
        sweep: Double
    ) {
        // Taken from equations at:
        // http://spaceroots.org/documents/ellipse/node8.html
        // and http://www.spaceroots.org/documents/ellipse/node22.html

        // Maximum of 45 degrees per cubic Bezier segment

        var e1x = e1x
        var e1y = e1y
        val numSegments = abs(
            ceil(sweep * 4 / Math.PI)
                .toInt().toDouble()
        ).toInt()

        var eta1 = start
        val cosTheta = cos(theta)
        val sinTheta = sin(theta)
        val cosEta1 = cos(eta1)
        val sinEta1 = sin(eta1)
        var ep1x = (-a * cosTheta * sinEta1) - (b * sinTheta * cosEta1)
        var ep1y = (-a * sinTheta * sinEta1) + (b * cosTheta * cosEta1)

        val anglePerSegment = sweep / numSegments
        for (i in 0 until numSegments) {
            val eta2 = eta1 + anglePerSegment
            val sinEta2 = sin(eta2)
            val cosEta2 = cos(eta2)
            val e2x = (cx + (a * cosTheta * cosEta2)
                    - (b * sinTheta * sinEta2))
            val e2y = (cy + (a * sinTheta * cosEta2)
                    + (b * cosTheta * sinEta2))
            val ep2x = -a * cosTheta * sinEta2 - b * sinTheta * cosEta2
            val ep2y = -a * sinTheta * sinEta2 + b * cosTheta * cosEta2
            val tanDiff2 = tan((eta2 - eta1) / 2)
            val alpha = sin(eta2 - eta1) * (sqrt(4 + (3 * tanDiff2 * tanDiff2)) - 1) / 3
            val q1x = e1x + alpha * ep1x
            val q1y = e1y + alpha * ep1y
            val q2x = e2x - alpha * ep2x
            val q2y = e2y - alpha * ep2y

            p.curveTo(
                q1x.toFloat().toDouble(),
                q1y.toFloat().toDouble(),
                q2x.toFloat().toDouble(),
                q2y.toFloat()
                    .toDouble(),
                e2x.toFloat().toDouble(),
                e2y.toFloat().toDouble()
            )
            eta1 = eta2
            e1x = e2x
            e1y = e2y
            ep1x = ep2x
            ep1y = ep2y
        }
    }
}
