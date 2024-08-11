/*
 * Forked from:
 * https://android.googlesource.com/platform//frameworks/support/+/7b4652f32f5867e5bbe53dcb20ec95c52f3fe979/compose/material/material/icons/generator/src/main/kotlin/androidx/compose/material/icons/generator/Names.kt
 *
 * Copyright 2020 The Android Open Source Project
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

package io.github.irgaly.compose.icons.xml

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

/**
 * Package names used for icon generation.
 */
internal enum class PackageNames(val packageName: String) {
    MaterialIconsPackage("androidx.compose.material.icons"),
    GraphicsPackage("androidx.compose.ui.graphics"),
    VectorPackage(GraphicsPackage.packageName + ".vector")
}

/**
 * [ClassName]s used for icon generation.
 */
internal object ClassNames {
    val Icons = PackageNames.MaterialIconsPackage.className("Icons")
    val ImageVector = PackageNames.VectorPackage.className("ImageVector")
    val PathFillType = PackageNames.GraphicsPackage.className("PathFillType", "Companion")
}

/**
 * [MemberName]s used for icon generation.
 */
internal object MemberNames {
    val MaterialIcon = MemberName(PackageNames.MaterialIconsPackage.packageName, "materialIcon")
    val MaterialPath = MemberName(PackageNames.MaterialIconsPackage.packageName, "materialPath")

    val EvenOdd = MemberName(ClassNames.PathFillType, "EvenOdd")
    val Group = MemberName(PackageNames.VectorPackage.packageName, "group")
}

/**
 * @return the [ClassName] of the given [classNames] inside this package.
 */
internal fun PackageNames.className(vararg classNames: String) = ClassName(this.packageName, *classNames)
