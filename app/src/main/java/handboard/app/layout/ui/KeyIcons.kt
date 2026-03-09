package handboard.app.layout.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BackspaceIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.08f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val shape = Path().apply {
            moveTo(w * 0.32f, h * 0.15f); lineTo(w * 0.88f, h * 0.15f)
            lineTo(w * 0.88f, h * 0.85f); lineTo(w * 0.32f, h * 0.85f)
            lineTo(w * 0.08f, h * 0.50f); close()
        }
        drawPath(shape, tint, style = stroke)
        drawLine(tint, Offset(w * 0.48f, h * 0.35f), Offset(w * 0.72f, h * 0.65f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.72f, h * 0.35f), Offset(w * 0.48f, h * 0.65f), strokeWidth = sw)
    }
}

@Composable
fun EnterIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.08f
        drawLine(tint, Offset(w * 0.75f, h * 0.22f), Offset(w * 0.75f, h * 0.60f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.75f, h * 0.60f), Offset(w * 0.25f, h * 0.60f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.25f, h * 0.60f), Offset(w * 0.40f, h * 0.42f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.25f, h * 0.60f), Offset(w * 0.40f, h * 0.78f), strokeWidth = sw)
    }
}

@Composable
fun ShiftIcon(tint: Color, size: Dp = 20.dp, filled: Boolean = false) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height
        val stroke = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val arrow = Path().apply {
            moveTo(w * 0.50f, h * 0.10f); lineTo(w * 0.82f, h * 0.50f)
            lineTo(w * 0.62f, h * 0.50f); lineTo(w * 0.62f, h * 0.82f)
            lineTo(w * 0.38f, h * 0.82f); lineTo(w * 0.38f, h * 0.50f)
            lineTo(w * 0.18f, h * 0.50f); close()
        }
        if (filled) drawPath(arrow, tint) else drawPath(arrow, tint, style = stroke)
    }
}

@Composable
fun CapsLockIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height
        val arrow = Path().apply {
            moveTo(w * 0.50f, h * 0.06f); lineTo(w * 0.82f, h * 0.44f)
            lineTo(w * 0.62f, h * 0.44f); lineTo(w * 0.62f, h * 0.66f)
            lineTo(w * 0.38f, h * 0.66f); lineTo(w * 0.38f, h * 0.44f)
            lineTo(w * 0.18f, h * 0.44f); close()
        }
        drawPath(arrow, tint)
        drawLine(tint, Offset(w * 0.32f, h * 0.86f), Offset(w * 0.68f, h * 0.86f), strokeWidth = w * 0.10f)
    }
}

@Composable
fun BackArrowIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.09f
        drawLine(tint, Offset(w * 0.72f, h * 0.50f), Offset(w * 0.28f, h * 0.50f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.28f, h * 0.50f), Offset(w * 0.48f, h * 0.28f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.28f, h * 0.50f), Offset(w * 0.48f, h * 0.72f), strokeWidth = sw)
    }
}

@Composable
fun SearchIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.10f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round)
        drawCircle(tint, radius = w * 0.28f, center = Offset(w * 0.42f, h * 0.42f), style = stroke)
        drawLine(tint, Offset(w * 0.62f, h * 0.62f), Offset(w * 0.88f, h * 0.88f), strokeWidth = sw)
    }
}

@Composable
fun EditIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.09f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val pencil = Path().apply {
            moveTo(w * 0.72f, h * 0.10f); lineTo(w * 0.90f, h * 0.28f)
            lineTo(w * 0.35f, h * 0.82f); lineTo(w * 0.10f, h * 0.90f)
            lineTo(w * 0.18f, h * 0.65f); close()
        }
        drawPath(pencil, tint, style = stroke)
    }
}

@Composable
fun EmojiIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.08f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round)
        drawCircle(tint, radius = w * 0.40f, center = Offset(w / 2, h / 2), style = stroke)
        drawCircle(tint, radius = w * 0.05f, center = Offset(w * 0.36f, h * 0.40f))
        drawCircle(tint, radius = w * 0.05f, center = Offset(w * 0.64f, h * 0.40f))
        val smile = Path().apply {
            moveTo(w * 0.32f, h * 0.58f)
            quadraticBezierTo(w * 0.50f, h * 0.78f, w * 0.68f, h * 0.58f)
        }
        drawPath(smile, tint, style = stroke)
    }
}

@Composable
fun GlobeIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.07f
        val stroke = Stroke(width = sw)
        drawCircle(tint, radius = w * 0.40f, center = Offset(w / 2, h / 2), style = stroke)
        drawLine(tint, Offset(w * 0.10f, h * 0.50f), Offset(w * 0.90f, h * 0.50f), strokeWidth = sw)
        val vert = Path().apply {
            moveTo(w * 0.50f, h * 0.10f)
            cubicTo(w * 0.70f, h * 0.28f, w * 0.70f, h * 0.72f, w * 0.50f, h * 0.90f)
            cubicTo(w * 0.30f, h * 0.72f, w * 0.30f, h * 0.28f, w * 0.50f, h * 0.10f)
        }
        drawPath(vert, tint, style = stroke)
    }
}

@Composable
fun ClipboardIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.08f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawRoundRect(tint, topLeft = Offset(w * 0.18f, h * 0.18f),
            size = Size(w * 0.64f, h * 0.74f), cornerRadius = CornerRadius(w * 0.06f), style = stroke)
        drawRoundRect(tint, topLeft = Offset(w * 0.34f, h * 0.06f),
            size = Size(w * 0.32f, h * 0.18f), cornerRadius = CornerRadius(w * 0.04f), style = stroke)
        drawLine(tint, Offset(w * 0.30f, h * 0.45f), Offset(w * 0.70f, h * 0.45f), strokeWidth = sw * 0.6f)
        drawLine(tint, Offset(w * 0.30f, h * 0.58f), Offset(w * 0.70f, h * 0.58f), strokeWidth = sw * 0.6f)
        drawLine(tint, Offset(w * 0.30f, h * 0.71f), Offset(w * 0.55f, h * 0.71f), strokeWidth = sw * 0.6f)
    }
}

@Composable
fun CopyIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.08f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawRoundRect(color = tint, topLeft = Offset(w * 0.15f, h * 0.15f), size = Size(w * 0.55f, h * 0.65f), cornerRadius = CornerRadius(w * 0.06f), style = stroke)
        drawRoundRect(color = tint, topLeft = Offset(w * 0.30f, h * 0.25f), size = Size(w * 0.55f, h * 0.65f), cornerRadius = CornerRadius(w * 0.06f), style = stroke)
    }
}

@Composable
fun KeyboardIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.08f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawRoundRect(color = tint, topLeft = Offset(w * 0.10f, h * 0.25f), size = Size(w * 0.80f, h * 0.50f), cornerRadius = CornerRadius(w * 0.08f), style = stroke)
        // Draw keys
        val keyW = w * 0.12f
        drawLine(tint, Offset(w * 0.25f, h * 0.45f), Offset(w * 0.25f + keyW, h * 0.45f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.44f, h * 0.45f), Offset(w * 0.44f + keyW, h * 0.45f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.63f, h * 0.45f), Offset(w * 0.63f + keyW, h * 0.45f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.35f, h * 0.60f), Offset(w * 0.65f, h * 0.60f), strokeWidth = sw) // Spacebar
    }
}

@Composable
fun TranslateIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.08f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        
        // 'A' letter left
        drawLine(tint, Offset(w * 0.35f, h * 0.25f), Offset(w * 0.15f, h * 0.75f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.35f, h * 0.25f), Offset(w * 0.55f, h * 0.75f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.25f, h * 0.55f), Offset(w * 0.45f, h * 0.55f), strokeWidth = sw)
        
        // Oriental character right
        drawLine(tint, Offset(w * 0.65f, h * 0.35f), Offset(w * 0.85f, h * 0.35f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.75f, h * 0.25f), Offset(w * 0.75f, h * 0.75f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.65f, h * 0.75f), Offset(w * 0.85f, h * 0.75f), strokeWidth = sw)
    }
}

@Composable
fun KaomojiIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.08f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round)
        val leftP = Path().apply { moveTo(w * 0.25f, h * 0.20f); quadraticBezierTo(w * 0.10f, h * 0.50f, w * 0.25f, h * 0.80f) }
        drawPath(leftP, tint, style = stroke)
        val rightP = Path().apply { moveTo(w * 0.75f, h * 0.20f); quadraticBezierTo(w * 0.90f, h * 0.50f, w * 0.75f, h * 0.80f) }
        drawPath(rightP, tint, style = stroke)
        drawCircle(tint, radius = w * 0.06f, center = Offset(w * 0.38f, h * 0.42f))
        drawCircle(tint, radius = w * 0.06f, center = Offset(w * 0.62f, h * 0.42f))
        val smile = Path().apply { moveTo(w * 0.35f, h * 0.60f); quadraticBezierTo(w * 0.50f, h * 0.75f, w * 0.65f, h * 0.60f) }
        drawPath(smile, tint, style = stroke)
    }
}

@Composable
fun CurrencyIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.08f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawCircle(tint, radius = w * 0.40f, center = Offset(w / 2, h / 2), style = stroke)
        // Dollar S
        val sPath = Path().apply {
            moveTo(w * 0.60f, h * 0.35f)
            quadraticBezierTo(w * 0.40f, h * 0.30f, w * 0.40f, h * 0.45f)
            quadraticBezierTo(w * 0.40f, h * 0.55f, w * 0.60f, h * 0.55f)
            quadraticBezierTo(w * 0.70f, h * 0.65f, w * 0.60f, h * 0.70f)
            quadraticBezierTo(w * 0.40f, h * 0.70f, w * 0.40f, h * 0.65f)
        }
        drawPath(sPath, tint, style = stroke)
        drawLine(tint, Offset(w * 0.50f, h * 0.20f), Offset(w * 0.50f, h * 0.80f), strokeWidth = sw * 0.8f)
    }
}
