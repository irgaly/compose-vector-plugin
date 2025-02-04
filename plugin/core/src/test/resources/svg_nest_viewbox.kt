package io.github.irgaly.compose.vector.test.image

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.Suppress

@Suppress("RedundantVisibilityModifier")
public val svg_nest_viewbox: ImageVector
    get() {
        if (_svg_nest_viewbox != null) {
            return _svg_nest_viewbox!!
        }
        _svg_nest_viewbox = Builder("svg_nest_viewbox", 500.dp, 500.dp, 500f, 500f).apply {
            val fill0 = SolidColor(Color(0xFF000000))
            val strokeLineWidth0 = 1f
            path(fill = fill0, stroke = SolidColor(Color.Blue), strokeLineWidth = strokeLineWidth0) {
                moveTo(0f, 0f)
                lineTo(500f, 0f)
                lineTo(500f, 500f)
                lineTo(0f, 500f)
                lineTo(0f, 0f)
                close()
            }
            group(clipPathData = PathData {
                moveTo(100f, 100f)
                lineTo(200f, 100f)
                lineTo(200f, 200f)
                lineTo(100f, 200f)
                lineTo(100f, 100f)
                close()
            }) {
                path(fill = fill0, stroke = SolidColor(Color.Red), strokeLineWidth = strokeLineWidth0) {
                    moveTo(100f, 100f)
                    lineTo(300f, 100f)
                    lineTo(300f, 200f)
                    lineTo(100f, 200f)
                    lineTo(100f, 100f)
                    close()
                }
            }
        }.build()
        return _svg_nest_viewbox!!
    }

private var _svg_nest_viewbox: ImageVector? = null

@Preview
@Composable
private fun svg_nest_viewboxPreview() {
    Image(svg_nest_viewbox, null)
}

@Preview(showBackground = true)
@Composable
private fun svg_nest_viewboxBackgroundPreview() {
    Image(svg_nest_viewbox, null)
}
