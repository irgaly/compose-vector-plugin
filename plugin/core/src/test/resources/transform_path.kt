package io.github.irgaly.compose.vector.test.image

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.Suppress

@Suppress("RedundantVisibilityModifier")
public val transform_path: ImageVector
    get() {
        if (_transform_path != null) {
            return _transform_path!!
        }
        _transform_path = Builder("transform_path", 150.dp, 100.dp, 150f, 100f).apply {
            group(translationX = 40f) {
                val strokeLineWidth0 = 1f
                path(fill = SolidColor(Color.White), strokeLineWidth = strokeLineWidth0) {
                    moveTo(-40f, 0f)
                    lineTo(110f, 0f)
                    lineTo(110f, 100f)
                    lineTo(-40f, 100f)
                    lineTo(-40f, 0f)
                    close()
                }
                group {
                    val fill1 = SolidColor(Color(0xFF808080))
                    path(name = "heart", fill = fill1, strokeLineWidth = strokeLineWidth0) {
                        moveTo(-19.3092f, 72.1117f)
                        curveTo(-24.7661f, 67.4836f, -20.412f, 62.1972f, -9.5684f, 60.2852f)
                        curveTo(1.2752f, 58.3732f, 14.5292f, 60.5548f, 20.0831f, 65.1658f)
                        curveTo(14.6262f, 60.5377f, 18.9803f, 55.2513f, 29.8239f, 53.3393f)
                        curveTo(40.6675f, 51.4273f, 53.9215f, 53.6089f, 59.4754f, 58.2199f)
                        quadTo(74.4754f, 70.8064f, 50.0831f, 90.3388f)
                        quadTo(-4.3092f, 84.6982f, -19.3092f, 72.1117f)
                        close()
                    }
                }
                group {
                    val stroke2 = SolidColor(Color.Red)
                    path(name = "heart", stroke = stroke2, strokeLineWidth = strokeLineWidth0) {
                        moveTo(10f, 30f)
                        arcTo(20f, 20f, 0f, false, true, 50f, 30f)
                        arcTo(20f, 20f, 0f, false, true, 90f, 30f)
                        quadTo(90f, 60f, 50f, 90f)
                        quadTo(10f, 60f, 10f, 30f)
                        close()
                    }
                }
            }
        }.build()
        return _transform_path!!
    }

private var _transform_path: ImageVector? = null

@Preview
@Composable
private fun transform_pathPreview() {
    Image(transform_path, null)
}

@Preview(showBackground = true)
@Composable
private fun transform_pathBackgroundPreview() {
    Image(transform_path, null)
}
