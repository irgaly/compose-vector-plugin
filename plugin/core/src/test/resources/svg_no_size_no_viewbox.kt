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
public val svg_no_size_no_viewbox: ImageVector
    get() {
        if (_svg_no_size_no_viewbox != null) {
            return _svg_no_size_no_viewbox!!
        }
        _svg_no_size_no_viewbox = Builder("svg_no_size_no_viewbox", 300.dp, 150.dp, 300f,
                150f).apply {
            val fill0 = SolidColor(Color(0xFF000000))
            val strokeLineWidth0 = 1f
            path(fill = fill0, stroke = SolidColor(Color.Blue), strokeLineWidth =
                    strokeLineWidth0) {
                moveTo(200f, 100f)
                curveTo(200f, 155.2285f, 155.2285f, 200f, 100f, 200f)
                curveTo(44.7715f, 200f, 0f, 155.2285f, 0f, 100f)
                curveTo(0f, 44.7715f, 44.7715f, 0f, 100f, 0f)
                curveTo(155.2285f, 0f, 200f, 44.7715f, 200f, 100f)
                close()
            }
        }.build()
        return _svg_no_size_no_viewbox!!
    }

private var _svg_no_size_no_viewbox: ImageVector? = null

@Preview
@Composable
private fun svg_no_size_no_viewboxPreview() {
    Image(svg_no_size_no_viewbox, null)
}

@Preview(showBackground = true)
@Composable
private fun svg_no_size_no_viewboxBackgroundPreview() {
    Image(svg_no_size_no_viewbox, null)
}
