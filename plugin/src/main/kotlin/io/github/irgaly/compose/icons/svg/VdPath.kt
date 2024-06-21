/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/sdk-common/src/main/java/com/android/ide/common/vectordrawable/VdPath.java
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
import kotlin.math.atan2
import kotlin.math.hypot

/**
 * Used to represent one VectorDrawble's path element.
 */
internal class VdPath : VdElement() {
    var mNode: Array<Node>? = null
    var mStrokeColor: Int = 0
    var mFillColor: Int = 0
    var mStrokeWidth: Float = 0f
    var mRotate: Float = 0f
    var mShiftX: Float = 0f
    var mShiftY: Float = 0f
    var mRotateX: Float = 0f
    var mRotateY: Float = 0f
    var trimPathStart: Float = 0f
    var trimPathEnd: Float = 1f
    var trimPathOffset: Float = 0f
    var mStrokeLineCap: Int = -1
    var mStrokeLineJoin: Int = -1
    var mStrokeMiterlimit: Float = -1f
    var mClip: Boolean = false
    var mStrokeOpacity: Float = Float.NaN
    var mFillOpacity: Float = Float.NaN
    var mTrimPathStart: Float = 0f
    var mTrimPathEnd: Float = 1f
    var mTrimPathOffset: Float = 0f

    fun toPath(path: Path2D) {
        path.reset()
        if (mNode != null) {
            VdNodeRender.creatPath(mNode!!, path)
        }
    }

    class Node {
        var type: Char
        var params: FloatArray

        constructor(type: Char, params: FloatArray) {
            this.type = type
            this.params = params
        }

        constructor(n: Node) {
            this.type = n.type
            this.params = n.params.copyOf(n.params.size)
        }

        fun transform(
            a: Float,
            b: Float,
            c: Float,
            d: Float,
            e: Float,
            f: Float,
            pre: FloatArray
        ) {
            var incr = 0
            val tempParams: FloatArray
            val origParams: FloatArray
            when (type) {
                'z', 'Z' -> return
                'M', 'L', 'T' -> {
                    incr = 2
                    pre[0] = params[params.size - 2]
                    pre[1] = params[params.size - 1]
                    var i = 0
                    while (i < params.size) {
                        matrix(a, b, c, d, e, f, i, i + 1)
                        i += incr
                    }
                }

                'm', 'l', 't' -> {
                    incr = 2
                    pre[0] += params[params.size - 2]
                    pre[1] += params[params.size - 1]
                    var i = 0
                    while (i < params.size) {
                        matrix(a, b, c, d, 0f, 0f, i, i + 1)
                        i += incr
                    }
                }

                'h' -> {
                    type = 'l'
                    pre[0] += params[params.size - 1]

                    tempParams = FloatArray(params.size * 2)
                    origParams = params
                    params = tempParams
                    var i = 0
                    while (i < params.size) {
                        params[i] = origParams[i / 2]
                        params[i + 1] = 0f
                        matrix(a, b, c, d, 0f, 0f, i, i + 1)
                        i += 2
                    }
                }

                'H' -> {
                    type = 'L'
                    pre[0] = params[params.size - 1]
                    tempParams = FloatArray(params.size * 2)
                    origParams = params
                    params = tempParams
                    var i = 0
                    while (i < params.size) {
                        params[i] = origParams[i / 2]
                        params[i + 1] = pre[1]
                        matrix(a, b, c, d, e, f, i, i + 1)
                        i += 2
                    }
                }

                'v' -> {
                    pre[1] += params[params.size - 1]
                    type = 'l'
                    tempParams = FloatArray(params.size * 2)
                    origParams = params
                    params = tempParams
                    var i = 0
                    while (i < params.size) {
                        params[i] = 0f
                        params[i + 1] = origParams[i / 2]
                        matrix(a, b, c, d, 0f, 0f, i, i + 1)
                        i += 2
                    }
                }

                'V' -> {
                    type = 'L'
                    pre[1] = params[params.size - 1]
                    tempParams = FloatArray(params.size * 2)
                    origParams = params
                    params = tempParams
                    var i = 0
                    while (i < params.size) {
                        params[i] = pre[0]
                        params[i + 1] = origParams[i / 2]
                        matrix(a, b, c, d, e, f, i, i + 1)
                        i += 2
                    }
                }

                'C', 'S', 'Q' -> {
                    pre[0] = params[params.size - 2]
                    pre[1] = params[params.size - 1]
                    var i = 0
                    while (i < params.size) {
                        matrix(a, b, c, d, e, f, i, i + 1)
                        i += 2
                    }
                }

                's', 'q', 'c' -> {
                    pre[0] += params[params.size - 2]
                    pre[1] += params[params.size - 1]
                    var i = 0
                    while (i < params.size) {
                        matrix(a, b, c, d, 0f, 0f, i, i + 1)
                        i += 2
                    }
                }

                'a' -> {
                    incr = 7
                    pre[0] += params[params.size - 2]
                    pre[1] += params[params.size - 1]
                    var i = 0
                    while (i < params.size) {
                        matrix(a, b, c, d, 0f, 0f, i, i + 1)
                        val ang = Math.toRadians(params[i + 2].toDouble())
                        params[i + 2] =
                            Math.toDegrees(ang + atan2(b.toDouble(), d.toDouble())).toFloat()
                        matrix(a, b, c, d, 0f, 0f, i + 5, i + 6)
                        i += incr
                    }
                }

                'A' -> {
                    incr = 7
                    pre[0] = params[params.size - 2]
                    pre[1] = params[params.size - 1]
                    var i = 0
                    while (i < params.size) {
                        matrix(a, b, c, d, e, f, i, i + 1)
                        val ang = Math.toRadians(params[i + 2].toDouble())
                        params[i + 2] =
                            Math.toDegrees(ang + atan2(b.toDouble(), d.toDouble())).toFloat()
                        matrix(a, b, c, d, e, f, i + 5, i + 6)
                        i += incr
                    }
                }
            }
        }

        fun matrix(
            a: Float,
            b: Float,
            c: Float,
            d: Float,
            e: Float,
            f: Float,
            offx: Int,
            offy: Int
        ) {
            val inx = if ((offx < 0)) 1f else params[offx]
            val iny = if ((offy < 0)) 1f else params[offy]
            val x = inx * a + iny * c + e
            val y = inx * b + iny * d + f
            if (offx >= 0) {
                params[offx] = x
            }
            if (offy >= 0) {
                params[offy] = y
            }
        }

        companion object {
            fun NodeListToString(nodes: Array<Node>): String {
                var s = ""
                for (i in nodes.indices) {
                    val n = nodes[i]
                    s += n.type
                    val len = n.params.size
                    for (j in 0 until len) {
                        if (j > 0) {
                            s += if (((j and 1) == 1)) "," else " "
                        }
                        // To avoid trailing zeros like 17.0, use this trick
                        val value = n.params[j]
                        s += if (value == value.toLong().toFloat()) {
                            value.toLong().toString()
                        } else {
                            value.toString()
                        }
                    }
                }
                return s
            }

            fun transform(
                a: Float,
                b: Float,
                c: Float,
                d: Float,
                e: Float,
                f: Float,
                nodes: Array<Node>?
            ) {
                val pre = FloatArray(2)
                for (i in nodes!!.indices) {
                    nodes[i].transform(a, b, c, d, e, f, pre)
                }
            }
        }
    }

    init {
        name = this.toString() // to ensure paths have unique names
    }

    /**
     * TODO: support rotation attribute for stroke width
     */
    fun transform(a: Float, b: Float, c: Float, d: Float, e: Float, f: Float) {
        mStrokeWidth *= hypot(a + b, c + d)
        Node.transform(a, b, c, d, e, f, mNode)
    }
}
