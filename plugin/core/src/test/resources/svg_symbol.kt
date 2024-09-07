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
public val svg_symbol: ImageVector
    get() {
        if (_svg_symbol != null) {
            return _svg_symbol!!
        }
        _svg_symbol = Builder("svg_symbol", 500.dp, 500.dp, 500f, 500f).apply {
            val fill0 = SolidColor(Color(0xFF000000))
            val strokeLineWidth0 = 1f
            path(fill = fill0, stroke = SolidColor(Color.Blue), strokeLineWidth =
                    strokeLineWidth0) {
                moveTo(0f, 0f)
                lineTo(500f, 0f)
                lineTo(500f, 500f)
                lineTo(0f, 500f)
                lineTo(0f, 0f)
                close()
            }
            group(name = "symbol", clipPathData = PathData {
                moveTo(50f, 50f)
                lineTo(150f, 50f)
                lineTo(150f, 150f)
                lineTo(50f, 150f)
                lineTo(50f, 50f)
                close()
            }) {
                path(fill = fill0, stroke = SolidColor(Color.Red), strokeLineWidth =
                        strokeLineWidth0) {
                    moveTo(50f, 50f)
                    lineTo(100f, 50f)
                    lineTo(100f, 100f)
                    lineTo(50f, 100f)
                    lineTo(50f, 50f)
                    close()
                }
            }
        }.build()
        return _svg_symbol!!
    }

private var _svg_symbol: ImageVector? = null

@Preview
@Composable
private fun svg_symbolPreview() {
    Image(svg_symbol, null)
}

@Preview(showBackground = true)
@Composable
private fun svg_symbolBackgroundPreview() {
    Image(svg_symbol, null)
}
