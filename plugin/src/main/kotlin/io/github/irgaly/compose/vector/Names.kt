package io.github.irgaly.compose.vector

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

internal object PackageNames {
    val Runtime = "androidx.compose.runtime"
    val Preview = "androidx.compose.ui.tooling.preview"
    val Foundation = "androidx.compose.foundation"
    val Graphics = "androidx.compose.ui.graphics"
    val Vector = "androidx.compose.ui.graphics.vector"
    val Unit = "androidx.compose.ui.unit"
}

internal object ClassNames {
    val Composable = ClassName(PackageNames.Runtime, "Composable")
    val Preview = ClassName(PackageNames.Preview, "Preview")
    val ImageVector = ClassName(PackageNames.Vector, "ImageVector")
    val PathFillType = ClassName(PackageNames.Graphics, "PathFillType", "Companion")
}

internal object MemberNames {
    val Image = MemberName(PackageNames.Foundation, "Image")
    val ImageVectorBuilder = MemberName(ClassNames.ImageVector, "Builder")
    val EvenOdd = MemberName(ClassNames.PathFillType, "EvenOdd")
    val Dp = MemberName(PackageNames.Unit, "dp")
}
