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
public val path: ImageVector
    get() {
        if (_path != null) {
            return _path!!
        }
        _path = Builder("path", 300.dp, 400.dp, 300f, 400f).apply {
            path(stroke = SolidColor(Color.Blue), strokeLineWidth = 20f) {
                moveTo(0f, 0f)
                lineTo(100f, 250f)
                lineTo(200f, 0f)
            }
        }.build()
        return _path!!
    }

private var _path: ImageVector? = null

@Preview
@Composable
private fun pathPreview() {
    Image(path, null)
}

@Preview(showBackground = true)
@Composable
private fun pathBackgroundPreview() {
    Image(path, null)
}
