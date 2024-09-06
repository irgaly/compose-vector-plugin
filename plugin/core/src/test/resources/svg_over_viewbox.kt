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
public val svg_over_viewbox: ImageVector
    get() {
        if (_svg_over_viewbox != null) {
            return _svg_over_viewbox!!
        }
        _svg_over_viewbox = Builder("svg_over_viewbox", 100.dp, 100.dp, 100f, 100f).apply {
            val fill0 = SolidColor(Color(0xFF000000))
            val strokeLineWidth0 = 1f
            path(fill = fill0, stroke = SolidColor(Color.Red), strokeLineWidth = strokeLineWidth0) {
                moveTo(0f, 0f)
                lineTo(200f, 0f)
                lineTo(200f, 100f)
                lineTo(0f, 100f)
                lineTo(0f, 0f)
                close()
            }
        }.build()
        return _svg_over_viewbox!!
    }

private var _svg_over_viewbox: ImageVector? = null

@Preview
@Composable
private fun svg_over_viewboxPreview() {
    Image(svg_over_viewbox, null)
}

@Preview(showBackground = true)
@Composable
private fun svg_over_viewboxBackgroundPreview() {
    Image(svg_over_viewbox, null)
}
