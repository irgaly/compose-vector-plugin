package io.github.irgaly.compose.vector.node

/**
 * ImageVector Node
 */
data class ImageVector(
    val name: String,
    /**
     * dp
     */
    val defaultWidth: Double,
    /**
     * dp
     */
    val defaultHeight: Double,
    val viewportWidth: Float,
    val viewportHeight: Float,
    val autoMirror: Boolean,
    val nodes: List<VectorNode>,
) {
    sealed interface VectorNode {
        data class VectorGroup(
            val nodes: List<VectorNode>,
            val name: String? = null,
            val rotate: Float? = null,
            val pivotX: Float? = null,
            val pivotY: Float? = null,
            val scaleX: Float? = null,
            val scaleY: Float? = null,
            val translationX: Float? = null,
            val translationY: Float? = null,
            val clipPathData: List<PathNode> = emptyList(),
        ) : VectorNode

        data class VectorPath(
            val pathData: List<PathNode>,
            val pathFillType: PathFillType? = null,
            val name: String? = null,
            val fill: Brush? = null,
            val fillAlpha: Float? = null,
            val stroke: Brush? = null,
            val strokeAlpha: Float? = null,
            val strokeLineWidth: Float? = null,
            val strokeLineCap: StrokeCap? = null,
            val strokeLineJoin: StrokeJoin? = null,
            val strokeLineMiter: Float? = null,
            val trimPathStart: Float? = null,
            val trimPathEnd: Float? = null,
            val trimPathOffset: Float? = null,
        ) : VectorNode
    }

    enum class PathFillType {
        EvenOdd, NonZero
    }

    sealed interface Brush {
        data class SolidColor(
            val color: Color,
        ) : Brush
    }

    enum class StrokeCap {
        Butt, Round, Square
    }

    enum class StrokeJoin {
        Bevel, Miter, Round
    }

    data class Color(val hex: String)
    sealed interface PathNode {
        data class ArcTo(
            val horizontalEllipseRadius: Float,
            val verticalEllipseRadius: Float,
            val theta: Float,
            val isMoreThanHalf: Boolean,
            val isPositiveArc: Boolean,
            val arcStartX: Float,
            val arcStartY: Float,
        ) : PathNode

        data object Close : PathNode
        data class CurveTo(
            val x1: Float,
            val y1: Float,
            val x2: Float,
            val y2: Float,
            val x3: Float,
            val y3: Float,
        ) : PathNode

        data class HorizontalTo(
            val x: Float,
        ) : PathNode

        data class LineTo(
            val x: Float,
            val y: Float,
        ) : PathNode

        data class MoveTo(
            val x: Float,
            val y: Float,
        ) : PathNode

        data class QuadTo(
            val x1: Float,
            val y1: Float,
            val x2: Float,
            val y2: Float,
        ) : PathNode

        data class ReflectiveCurveTo(
            val x1: Float,
            val y1: Float,
            val x2: Float,
            val y2: Float,
        ) : PathNode

        data class ReflectiveQuadTo(
            val x: Float,
            val y: Float,
        ) : PathNode

        data class RelativeArcTo(
            val horizontalEllipseRadius: Float,
            val verticalEllipseRadius: Float,
            val theta: Float,
            val isMoreThanHalf: Boolean,
            val isPositiveArc: Boolean,
            val arcStartDx: Float,
            val arcStartDy: Float,
        ) : PathNode

        data class RelativeCurveTo(
            val dx1: Float,
            val dy1: Float,
            val dx2: Float,
            val dy2: Float,
            val dx3: Float,
            val dy3: Float,
        ) : PathNode

        data class RelativeHorizontalTo(
            val dx: Float,
        ) : PathNode

        data class RelativeLineTo(
            val dx: Float,
            val dy: Float,
        ) : PathNode

        data class RelativeMoveTo(
            val dx: Float,
            val dy: Float,
        ) : PathNode

        data class RelativeQuadTo(
            val dx1: Float,
            val dy1: Float,
            val dx2: Float,
            val dy2: Float,
        ) : PathNode

        data class RelativeReflectiveCurveTo(
            val dx1: Float,
            val dy1: Float,
            val dx2: Float,
            val dy2: Float,
        ) : PathNode

        data class RelativeReflectiveQuadTo(
            val dx: Float,
            val dy: Float,
        ) : PathNode

        data class RelativeVerticalTo(
            val dy: Float,
        ) : PathNode

        data class VerticalTo(
            val y: Float,
        ) : PathNode
    }
}
