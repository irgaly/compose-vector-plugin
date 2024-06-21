/*
 * Forked from:
 * https://android.googlesource.com/platform//frameworks/support/+/7b4652f32f5867e5bbe53dcb20ec95c52f3fe979/compose/material/material/icons/generator/src/main/kotlin/androidx/compose/material/icons/generator/IconTheme.kt
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

import java.util.Locale

/**
 * Enum representing the different themes for Material icons.
 *
 * @property themePackageName the lower case name used for package names and in xml files
 * @property themeClassName the CameCase name used for the theme objects
 */
internal enum class IconTheme(val themePackageName: String, val themeClassName: String) {
    Filled("filled", "Filled"),
    Outlined("outlined", "Outlined"),
    Rounded("rounded", "Rounded"),
    TwoTone("twotone", "TwoTone"),
    Sharp("sharp", "Sharp")
}

/**
 * Returns the matching [IconTheme] from [this] [IconTheme.themePackageName].
 */
internal fun String.toIconTheme() = requireNotNull(
    IconTheme.values().find {
        it.themePackageName == this
    }
) { "No matching theme found" }

/**
 * The ClassName representing this [IconTheme] object, so we can generate extension properties on
 * the object.
 *
 * @see [autoMirroredClassName]
 */
internal val IconTheme.className
    get() =
        PackageNames.MaterialIconsPackage.className("Icons", themeClassName)

/**
 * The ClassName representing this [IconTheme] object so we can generate extension properties on the
 * object when used for auto-mirrored icons.
 *
 * @see [className]
 */
internal val IconTheme.autoMirroredClassName
    get() =
        PackageNames.MaterialIconsPackage.className("Icons", AutoMirroredName, themeClassName)

internal const val AutoMirroredName = "AutoMirrored"
internal val AutoMirroredPackageName = AutoMirroredName.lowercase(Locale.ROOT)
