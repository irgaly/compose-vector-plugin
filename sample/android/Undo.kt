import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview


@Preview
@Composable
private fun VectorPreview() {
    Image(Undo, null)
}

private var _Undo: ImageVector? = null

public val Undo: ImageVector
    get() {
        if (_Undo != null) {
            return _Undo!!
        }
        _Undo = ImageVector.Builder(
            name = "Undo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFE8EAED)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(280f, -200f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(284f)
                quadToRelative(63f, 0f, 109.5f, -40f)
                reflectiveQuadTo(720f, -420f)
                quadToRelative(0f, -60f, -46.5f, -100f)
                reflectiveQuadTo(564f, -560f)
                horizontalLineTo(312f)
                lineToRelative(104f, 104f)
                lineToRelative(-56f, 56f)
                lineToRelative(-200f, -200f)
                lineToRelative(200f, -200f)
                lineToRelative(56f, 56f)
                lineToRelative(-104f, 104f)
                horizontalLineToRelative(252f)
                quadToRelative(97f, 0f, 166.5f, 63f)
                reflectiveQuadTo(800f, -420f)
                quadToRelative(0f, 94f, -69.5f, 157f)
                reflectiveQuadTo(564f, -200f)
                horizontalLineTo(280f)
                close()
            }
        }.build()
        return _Undo!!
    }

