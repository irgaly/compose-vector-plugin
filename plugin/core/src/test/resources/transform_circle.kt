package io.github.irgaly.compose.vector.test.image

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.Suppress

@Suppress("RedundantVisibilityModifier")
public val transform_circle: ImageVector
    get() {
        if (_transform_circle != null) {
            return _transform_circle!!
        }
        _transform_circle = Builder("transform_circle", 500.dp, 500.dp, 500f, 500f).apply {
            val fill0 = SolidColor(Color(0xFF000000))
            val strokeLineWidth0 = 1f
            path(fill = fill0, stroke = SolidColor(Color.Red), strokeLineWidth = strokeLineWidth0) {
                moveTo(0f, 0f)
                lineTo(500f, 0f)
                lineTo(500f, 500f)
                lineTo(0f, 500f)
                lineTo(0f, 0f)
                close()
            }
            path(fill = SolidColor(Color.White), stroke = SolidColor(Color.Blue), strokeLineWidth = strokeLineWidth0) {
                moveTo(283.91f, 100f)
                curveTo(330.2522f, 155.2285f, 323.0484f, 200f, 267.8199f, 200f)
                curveTo(212.5914f, 200f, 130.2522f, 155.2285f, 83.91f, 100f)
                curveTo(37.5678f, 44.7715f, 44.7715f, 0f, 100f, 0f)
                curveTo(155.2285f, 0f, 237.5678f, 44.7715f, 283.91f, 100f)
                close()
            }
        }.build()
        return _transform_circle!!
    }

private var _transform_circle: ImageVector? = null

@Preview
@Composable
private fun transform_circlePreview() {
    Image(transform_circle, null)
}

@Preview(showBackground = true)
@Composable
private fun transform_circleBackgroundPreview() {
    Image(transform_circle, null)
}
