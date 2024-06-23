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
        val translationX: Float,
        val translationY: Float,
        val clipPathData: List<Path>
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
        val trimPathOffset: Float
    )
    interface PathNode
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
}
