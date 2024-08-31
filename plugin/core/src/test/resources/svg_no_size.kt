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
public val svg_no_size: ImageVector
    get() {
        if (_svg_no_size != null) {
            return _svg_no_size!!
        }
        _svg_no_size = Builder("svg_no_size", 500.dp, 500.dp, 500f, 500f).apply {
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
        return _svg_no_size!!
    }

private var _svg_no_size: ImageVector? = null

@Preview
@Composable
private fun svg_no_sizePreview() {
    Image(svg_no_size, null)
}

@Preview(showBackground = true)
@Composable
private fun svg_no_sizeBackgroundPreview() {
    Image(svg_no_size, null)
}
