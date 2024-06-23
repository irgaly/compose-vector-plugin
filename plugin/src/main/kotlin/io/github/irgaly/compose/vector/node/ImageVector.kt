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
    val nodes: List<Node>
) {
    interface Node
    data class Group(
        val name: String,
        val rotate: Float,
        val pivotX: Float,
        val pivotY: Float,
        val scaleX: Float,
        val scaleY: Float,
        val translationX: Float,
        val translationY: Float,
        val clipPathData: List<PathNode>,
        val nodes: List<Node>,
    ): Node
    data class Path(
        val pathData: List<PathNode>,
        val pathFillType: PathFillType,
        val name: String,
        val fill: Brush,
        val fillAlpha: Float,
        val stroke: Brush,
        val strokeAlpha: Float,
        val strokeLineWidth: Float,
        val strokeLineCap: StrokeCap,
        val strokeLineJoin: StrokeJoin,
        val strokeLineMiter: Float,
        val trimPathStart: Float,
        val trimPathEnd: Float,
        val trimPathOffset: Float,
    ) : Node
    enum class PathFillType {
        EvenOdd, NonZero
    }
    interface Brush
    data class SolidColor(
        val color: Color
    ): Brush
    enum class StrokeCap {
        Butt, Round, Square
    }
    enum class StrokeJoin {
        Beve1, Miter, Round
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

        object Close : PathNode
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
