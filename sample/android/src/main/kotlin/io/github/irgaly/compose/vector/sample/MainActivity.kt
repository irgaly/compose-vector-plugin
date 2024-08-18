package io.github.irgaly.compose.vector.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.github.irgaly.compose.vector.sample.image.Icons
import io.github.irgaly.compose.vector.sample.image.Undo
import io.github.irgaly.compose.vector.sample.image.icons.Undo
import io.github.irgaly.compose.vector.sample.image.icons.automirrored.Undo

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()
        WindowInsetsControllerCompat(
            window,
            findViewById(android.R.id.content)
        ).isAppearanceLightStatusBars = true
        setContent {
            MaterialTheme {
                Column(
                    Modifier.fillMaxSize(),
                ) {
                    Text("Plugin Sample")
                    Image(Undo, contentDescription = null)
                    Image(Icons.Undo, contentDescription = null)
                    Image(Icons.AutoMirrored.Undo, contentDescription = null)
                }
            }
        }
    }
}
