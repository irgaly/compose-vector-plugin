package io.github.irgaly.compose.vector.sample.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.irgaly.compose.vector.sample.library.image.Icons
import io.github.irgaly.compose.vector.sample.library.image.icons.Undo

@Composable
fun Sample() {
    Column {
        Image(
            Icons.Undo,
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun SamplePreview() {
    MaterialTheme {
        Sample()
    }
}

