package handboard.app.layout.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun KeyboardWrapper(
    widthFraction: Float,
    alignment: Int,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = when (alignment) {
            0 -> Alignment.CenterStart
            2 -> Alignment.CenterEnd
            else -> Alignment.Center
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth(widthFraction)) {
            content()
        }
    }
}
