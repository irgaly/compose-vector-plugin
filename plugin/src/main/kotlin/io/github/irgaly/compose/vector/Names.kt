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
    val PathNode = ClassName(PackageNames.Vector, "PathNode")
    val PathFillType = ClassName(PackageNames.Graphics, "PathFillType")

}

internal object MemberNames {
    val Image = MemberName(PackageNames.Foundation, "Image")
    val EvenOdd = MemberName(ClassNames.PathFillType, "EvenOdd")
    val Dp = MemberName(PackageNames.Unit, "dp")
    val group = MemberName(PackageNames.Vector, "group")
    val path = MemberName(PackageNames.Vector, "path")

    internal object ImageVector {
        val Builder = MemberName(ClassNames.ImageVector, "Builder")
    }

    internal object PathNode {
        val ArcTo = MemberName(ClassNames.PathNode, "ArcTo")
        val Close = MemberName(ClassNames.PathNode, "Close")
        val CurveTo = MemberName(ClassNames.PathNode, "CurveTo")
        val HorizontalTo = MemberName(ClassNames.PathNode, "HorizontalTo")
        val LineTo = MemberName(ClassNames.PathNode, "LineTo")
        val MoveTo = MemberName(ClassNames.PathNode, "MoveTo")
        val QuadTo = MemberName(ClassNames.PathNode, "QuadTo")
        val ReflectiveCurveTo = MemberName(ClassNames.PathNode, "ReflectiveCurveTo")
        val ReflectiveQuadTo = MemberName(ClassNames.PathNode, "ReflectiveQuadTo")
        val RelativeArcTo = MemberName(ClassNames.PathNode, "RelativeArcTo")
        val RelativeCurveTo = MemberName(ClassNames.PathNode, "RelativeCurveTo")
        val RelativeHorizontalTo = MemberName(ClassNames.PathNode, "RelativeHorizontalTo")
        val RelativeLineTo = MemberName(ClassNames.PathNode, "RelativeLineTo")
        val RelativeMoveTo = MemberName(ClassNames.PathNode, "RelativeMoveTo")
        val RelativeQuadTo = MemberName(ClassNames.PathNode, "RelativeQuadTo")
        val RelativeReflectiveCurveTo = MemberName(ClassNames.PathNode, "RelativeReflectiveCurveTo")
        val RelativeReflectiveQuadTo = MemberName(ClassNames.PathNode, "RelativeVerticalTo")
        val RelativeVerticalTo = MemberName(ClassNames.PathNode, "RelativeVerticalTo")
        val VerticalTo = MemberName(ClassNames.PathNode, "VerticalTo")
    }
}
