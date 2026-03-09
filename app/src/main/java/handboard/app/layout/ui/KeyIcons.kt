package handboard.app.layout.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        val w = this.size.width; val h = this.size.height
        val sw = w * 0.09f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val shape = Path().apply {
            moveTo(w*0.35f,h*0.12f); lineTo(w*0.88f,h*0.12f)
            lineTo(w*0.88f,h*0.88f); lineTo(w*0.35f,h*0.88f)
            lineTo(w*0.08f,h*0.50f); close()
        }
        drawPath(shape, tint, style = stroke)
        drawLine(tint, Offset(w*0.48f,h*0.32f), Offset(w*0.74f,h*0.68f), strokeWidth = sw)
        drawLine(tint, Offset(w*0.74f,h*0.32f), Offset(w*0.48f,h*0.68f), strokeWidth = sw)
    }
}

@Composable
fun EnterIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.09f
        drawLine(tint, Offset(w*0.78f,h*0.20f), Offset(w*0.78f,h*0.62f), strokeWidth = sw)
        drawLine(tint, Offset(w*0.78f,h*0.62f), Offset(w*0.22f,h*0.62f), strokeWidth = sw)
        drawLine(tint, Offset(w*0.22f,h*0.62f), Offset(w*0.38f,h*0.42f), strokeWidth = sw)
        drawLine(tint, Offset(w*0.22f,h*0.62f), Offset(w*0.38f,h*0.80f), strokeWidth = sw)
    }
}

@Composable
fun ShiftIcon(tint: Color, size: Dp = 22.dp, filled: Boolean = false) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height
        val stroke = Stroke(width = w*0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val arrow = Path().apply {
            moveTo(w*0.50f,h*0.08f); lineTo(w*0.85f,h*0.52f)
            lineTo(w*0.64f,h*0.52f); lineTo(w*0.64f,h*0.82f)
            lineTo(w*0.36f,h*0.82f); lineTo(w*0.36f,h*0.52f)
            lineTo(w*0.15f,h*0.52f); close()
        }
        if (filled) drawPath(arrow, tint) else drawPath(arrow, tint, style = stroke)
    }
}

@Composable
fun CapsLockIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height
        val arrow = Path().apply {
            moveTo(w*0.50f,h*0.05f); lineTo(w*0.85f,h*0.45f)
            lineTo(w*0.64f,h*0.45f); lineTo(w*0.64f,h*0.68f)
            lineTo(w*0.36f,h*0.68f); lineTo(w*0.36f,h*0.45f)
            lineTo(w*0.15f,h*0.45f); close()
        }
        drawPath(arrow, tint)
        drawLine(tint, Offset(w*0.30f,h*0.88f), Offset(w*0.70f,h*0.88f), strokeWidth = w*0.12f)
    }
}

@Composable
fun BackArrowIcon(tint: Color, size: Dp = 24.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.1f
        drawLine(tint, Offset(w*0.75f,h*0.50f), Offset(w*0.25f,h*0.50f), strokeWidth = sw)
        drawLine(tint, Offset(w*0.25f,h*0.50f), Offset(w*0.48f,h*0.25f), strokeWidth = sw)
        drawLine(tint, Offset(w*0.25f,h*0.50f), Offset(w*0.48f,h*0.75f), strokeWidth = sw)
    }
}

@Composable
fun ClipboardIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.08f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val board = Path().apply {
            moveTo(w*0.20f,h*0.15f); lineTo(w*0.80f,h*0.15f)
            lineTo(w*0.80f,h*0.92f); lineTo(w*0.20f,h*0.92f); close()
        }
        drawPath(board, tint, style = stroke)
        drawLine(tint, Offset(w*0.38f,h*0.05f), Offset(w*0.62f,h*0.05f), strokeWidth = sw*1.5f)
        drawLine(tint, Offset(w*0.38f,h*0.05f), Offset(w*0.38f,h*0.20f), strokeWidth = sw)
        drawLine(tint, Offset(w*0.62f,h*0.05f), Offset(w*0.62f,h*0.20f), strokeWidth = sw)
        drawLine(tint, Offset(w*0.32f,h*0.40f), Offset(w*0.68f,h*0.40f), strokeWidth = sw*0.7f)
        drawLine(tint, Offset(w*0.32f,h*0.55f), Offset(w*0.68f,h*0.55f), strokeWidth = sw*0.7f)
        drawLine(tint, Offset(w*0.32f,h*0.70f), Offset(w*0.55f,h*0.70f), strokeWidth = sw*0.7f)
    }
}

@Composable
fun GlobeIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.07f
        val stroke = Stroke(width = sw)
        drawCircle(tint, radius = w*0.42f, center = Offset(w/2, h/2), style = stroke)
        drawLine(tint, Offset(w*0.08f,h*0.50f), Offset(w*0.92f,h*0.50f), strokeWidth = sw)
        val vert = Path().apply {
            moveTo(w*0.50f,h*0.08f)
            cubicTo(w*0.72f,h*0.25f, w*0.72f,h*0.75f, w*0.50f,h*0.92f)
            cubicTo(w*0.28f,h*0.75f, w*0.28f,h*0.25f, w*0.50f,h*0.08f)
        }
        drawPath(vert, tint, style = stroke)
    }
}
