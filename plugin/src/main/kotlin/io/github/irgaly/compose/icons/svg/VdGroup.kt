/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/sdk-common/src/main/java/com/android/ide/common/vectordrawable/VdGroup.java
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
 * Used to represent one VectorDrawble's group element.
 * TODO: Add group transformation here.
 */
internal class VdGroup : VdElement() {
    // Children can be either a {@link VdPath} or {@link VdGroup}
    private val mChildren: ArrayList<VdElement?> =
        ArrayList<VdElement?>()

    init {
        name = this.toString() // to ensure paths have unique names
    }

    fun add(pathOrGroup: VdElement?) {
        mChildren.add(pathOrGroup)
    }

    val children: ArrayList<VdElement?>
        get() = mChildren

    fun size(): Int {
        return mChildren.size
    }
}
