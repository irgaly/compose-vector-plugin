/*
 * Forked from:
 * https://android.googlesource.com/platform//frameworks/support/+/7b4652f32f5867e5bbe53dcb20ec95c52f3fe979/compose/material/material/icons/generator/src/main/kotlin/androidx/compose/material/icons/generator/vector/FillType.kt
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

package io.github.irgaly.compose.icons.xml.vector

/**
 * Determines the winding rule that decides how the interior of a [VectorNode.Path] is calculated.
 *
 * This maps to [android.graphics.Path.FillType] used in the framework, and can be defined in XML
 * via `android:fillType`.
 */
internal enum class FillType {
    NonZero,
    EvenOdd
}
