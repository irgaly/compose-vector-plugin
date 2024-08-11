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
    val Geometory = "androidx.compose.ui.geometry"
}

internal object ClassNames {
    val Composable = ClassName(PackageNames.Runtime, "Composable")
    val Preview = ClassName(PackageNames.Preview, "Preview")
    val ImageVector = ClassName(PackageNames.Vector, "ImageVector")
    val PathFillType = ClassName(PackageNames.Graphics, "PathFillType")
    val BrushCompanion = ClassName(PackageNames.Graphics, "Brush", "Companion")
    val StrokeCap = ClassName(PackageNames.Graphics, "StrokeCap")
    val StrokeJoin = ClassName(PackageNames.Graphics, "StrokeJoin")
}

internal object MemberNames {
    val Image = MemberName(PackageNames.Foundation, "Image")
    val Dp = MemberName(PackageNames.Unit, "dp")
    val Color = MemberName(PackageNames.Graphics, "Color")
    val SolidColor = MemberName(PackageNames.Graphics, "SolidColor")
    val TileMode = MemberName(PackageNames.Graphics, "TileMode")
    val PathFillType = MemberName(ClassNames.PathFillType.packageName, ClassNames.PathFillType.simpleName)
    val StrokeCap = MemberName(ClassNames.StrokeCap.packageName, ClassNames.StrokeCap.simpleName)
    val StrokeJoin = MemberName(ClassNames.StrokeJoin.packageName, ClassNames.StrokeJoin.simpleName)
    val Offset = MemberName(PackageNames.Geometory, "Offset")

    internal object ImageVector {
        val Builder = MemberName(ClassNames.ImageVector, "Builder")
    }

    internal object Vector {
        val PathData = MemberName(PackageNames.Vector, "PathData")
        val Group = MemberName(PackageNames.Vector, "group")
        val Path = MemberName(PackageNames.Vector, "path")
    }

    internal object Brush {
        val LinearGradient = MemberName(ClassNames.BrushCompanion, "linearGradient")
        val RadialGradient = MemberName(ClassNames.BrushCompanion, "radialGradient")
    }
}
