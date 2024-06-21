/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/sdk-common/src/main/java/com/android/ide/common/vectordrawable/PathBuilder.java
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

/**
 * Build a string for Svg file's path data.
 */
internal class PathBuilder {
    private val mPathData = StringBuilder()

    private fun booleanToString(flag: Boolean): String {
        return if (flag) "1" else "0"
    }

    fun absoluteMoveTo(x: Float, y: Float): PathBuilder {
        mPathData.append("M$x,$y")
        return this
    }

    fun relativeMoveTo(x: Float, y: Float): PathBuilder {
        mPathData.append("m$x,$y")
        return this
    }

    fun absoluteLineTo(x: Float, y: Float): PathBuilder {
        mPathData.append("L$x,$y")
        return this
    }

    fun relativeLineTo(x: Float, y: Float): PathBuilder {
        mPathData.append("l$x,$y")
        return this
    }

    fun absoluteVerticalTo(v: Float): PathBuilder {
        mPathData.append("V$v")
        return this
    }

    fun relativeVerticalTo(v: Float): PathBuilder {
        mPathData.append("v$v")
        return this
    }

    fun absoluteHorizontalTo(h: Float): PathBuilder {
        mPathData.append("H$h")
        return this
    }

    fun relativeHorizontalTo(h: Float): PathBuilder {
        mPathData.append("h$h")
        return this
    }

    fun absoluteArcTo(
        rx: Float, ry: Float, rotation: Boolean,
        largeArc: Boolean, sweep: Boolean, x: Float, y: Float
    ): PathBuilder {
        mPathData.append(
            "A" + rx + "," + ry + "," + booleanToString(rotation) + "," +
                    booleanToString(largeArc) + "," + booleanToString(sweep) + "," + x + "," + y
        )
        return this
    }

    fun relativeArcTo(
        rx: Float, ry: Float, rotation: Boolean,
        largeArc: Boolean, sweep: Boolean, x: Float, y: Float
    ): PathBuilder {
        mPathData.append(
            "a" + rx + "," + ry + "," + booleanToString(rotation) + "," +
                    booleanToString(largeArc) + "," + booleanToString(sweep) + "," + x + "," + y
        )
        return this
    }

    fun absoluteClose(): PathBuilder {
        mPathData.append("Z")
        return this
    }

    fun relativeClose(): PathBuilder {
        mPathData.append("z")
        return this
    }

    override fun toString(): String {
        return mPathData.toString()
    }
}
