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
    val rootGroup: VectorNode.VectorGroup,
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
            val currentTransformationMatrix: Matrix = Matrix(1f, 0f, 0f, 1f, 0f, 0f),
            val clipPathData: List<PathNode> = emptyList(),
            val extra: Extra? = null,
            val referencedExtra: Extra? = null,
        ) : VectorNode {
            data class Extra(
                val id: String,
                val fill: Brush? = null,
                val fillAlpha: Float? = null,
                val stroke: Brush? = null,
                val strokeAlpha: Float? = null,
                val strokeLineWidth: Float? = null,
                val strokeLineCap: StrokeCap? = null,
                val strokeLineJoin: StrokeJoin? = null,
                val strokeLineMiter: Float? = null,
            )
        }

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
            val extraReference: ExtraReference? = null
        ) : VectorNode {
            data class ExtraReference(
                val fillId: String? = null,
                val fillAlphaId: String? = null,
                val strokeId: String? = null,
                val strokeAlphaId: String? = null,
                val strokeLineWidthId: String? = null,
                val strokeLineCapId: String? = null,
                val strokeLineJoinId: String? = null,
                val strokeLineMiterId: String? = null,
            )
        }
    }

    enum class PathFillType {
        EvenOdd, NonZero
    }

    sealed interface Brush {
        data class SolidColor(
            val color: Color,
        ) : Brush
        data class LinearGradient(
            val colorStops: List<Pair<Float, Color>>,
            val start: Pair<Float, Float>,
            val end: Pair<Float, Float>,
            val tileMode: TileMode,
        ) : Brush

        data class RadialGradient(
            val colorStops: List<Pair<Float, Color>>,
            val center: Pair<Float, Float>,
            val radius: Float,
            val tileMode: TileMode,
        ) : Brush
    }

    enum class StrokeCap {
        Butt, Round, Square
    }

    enum class StrokeJoin {
        Bevel, Miter, Round
    }

    enum class TileMode {
        Clamp, Decal, Mirror, Repeated
    }

    sealed interface Color
    data class RgbColor(
        val red: Int,
        val green: Int,
        val blue: Int,
        val alpha: Int = 0xFF,
    ) : Color {
        fun teHexString(prefix: String = ""): String {
            return "%s%02X%02X%02X%02X".format(prefix, alpha, red, green, blue)
        }
    }

    data object Transparent : Color
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
    data class Matrix(
        val a: Float,
        val b: Float,
        val c: Float,
        val d: Float,
        val e: Float,
        val f: Float,
    ) {
        companion object {
            val identityMatrix = Matrix(
                a = 1f,
                b = 0f,
                c = 0f,
                d = 1f,
                e = 0f,
                f = 0f,
            )
        }
    }
}
