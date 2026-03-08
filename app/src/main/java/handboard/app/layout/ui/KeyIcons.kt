package handboard.app.layout.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BackspaceIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(
            width = w * 0.09f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        // Backspace outline shape
        val shape = Path().apply {
            moveTo(w * 0.35f, h * 0.12f)
            lineTo(w * 0.88f, h * 0.12f)
            lineTo(w * 0.88f, h * 0.88f)
            lineTo(w * 0.35f, h * 0.88f)
            lineTo(w * 0.08f, h * 0.50f)
            close()
        }
        drawPath(shape, tint, style = stroke)

        // X inside
        val xStroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round)
        drawLine(tint, Offset(w * 0.48f, h * 0.32f), Offset(w * 0.74f, h * 0.68f), strokeWidth = xStroke.width)
        drawLine(tint, Offset(w * 0.74f, h * 0.32f), Offset(w * 0.48f, h * 0.68f), strokeWidth = xStroke.width)
    }
}

@Composable
fun EnterIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val sw = w * 0.09f

        // Vertical line going down from top-right
        drawLine(tint, Offset(w * 0.78f, h * 0.20f), Offset(w * 0.78f, h * 0.62f), strokeWidth = sw)
        // Horizontal line going left
        drawLine(tint, Offset(w * 0.78f, h * 0.62f), Offset(w * 0.22f, h * 0.62f), strokeWidth = sw)
        // Arrow head
        drawLine(tint, Offset(w * 0.22f, h * 0.62f), Offset(w * 0.38f, h * 0.42f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.22f, h * 0.62f), Offset(w * 0.38f, h * 0.80f), strokeWidth = sw)
    }
}

@Composable
fun ShiftIcon(tint: Color, size: Dp = 22.dp, filled: Boolean = false) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val sw = w * 0.09f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Arrow pointing up
        val arrow = Path().apply {
            moveTo(w * 0.50f, h * 0.08f)
            lineTo(w * 0.85f, h * 0.52f)
            lineTo(w * 0.64f, h * 0.52f)
            lineTo(w * 0.64f, h * 0.82f)
            lineTo(w * 0.36f, h * 0.82f)
            lineTo(w * 0.36f, h * 0.52f)
            lineTo(w * 0.15f, h * 0.52f)
            close()
        }

        if (filled) {
            drawPath(arrow, tint)
        } else {
            drawPath(arrow, tint, style = stroke)
        }
    }
}

@Composable
fun CapsLockIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val sw = w * 0.09f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Filled arrow pointing up (shorter)
        val arrow = Path().apply {
            moveTo(w * 0.50f, h * 0.05f)
            lineTo(w * 0.85f, h * 0.45f)
            lineTo(w * 0.64f, h * 0.45f)
            lineTo(w * 0.64f, h * 0.68f)
            lineTo(w * 0.36f, h * 0.68f)
            lineTo(w * 0.36f, h * 0.45f)
            lineTo(w * 0.15f, h * 0.45f)
            close()
        }
        drawPath(arrow, tint)

        // Underline for caps lock indicator
        drawLine(tint, Offset(w * 0.30f, h * 0.88f), Offset(w * 0.70f, h * 0.88f), strokeWidth = sw * 1.3f)
    }
}
