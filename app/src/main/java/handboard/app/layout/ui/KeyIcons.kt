package handboard.app.layout.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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
fun ForwardArrowIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension; val stroke = s * 0.12f; val midY = s / 2f
        val tipX = s * 0.75f; val wingX = s * 0.45f; val wingSpread = s * 0.25f
        drawLine(tint, Offset(tipX, midY), Offset(wingX, midY - wingSpread), stroke, StrokeCap.Round)
        drawLine(tint, Offset(tipX, midY), Offset(wingX, midY + wingSpread), stroke, StrokeCap.Round)
        drawLine(tint, Offset(s * 0.22f, midY), Offset(tipX, midY), stroke, StrokeCap.Round)
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
        drawRoundRect(color = tint, topLeft = Offset(w * 0.18f, h * 0.18f), size = Size(w * 0.64f, h * 0.74f), cornerRadius = CornerRadius(w * 0.06f), style = stroke)
        drawRoundRect(color = tint, topLeft = Offset(w * 0.34f, h * 0.06f), size = Size(w * 0.32f, h * 0.18f), cornerRadius = CornerRadius(w * 0.04f), style = stroke)
        drawLine(tint, Offset(w * 0.30f, h * 0.45f), Offset(w * 0.70f, h * 0.45f), strokeWidth = sw * 0.6f)
        drawLine(tint, Offset(w * 0.30f, h * 0.58f), Offset(w * 0.70f, h * 0.58f), strokeWidth = sw * 0.6f)
        drawLine(tint, Offset(w * 0.30f, h * 0.71f), Offset(w * 0.55f, h * 0.71f), strokeWidth = sw * 0.6f)
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
fun KeyboardIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.08f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawRoundRect(color = tint, topLeft = Offset(w * 0.10f, h * 0.25f), size = Size(w * 0.80f, h * 0.50f), cornerRadius = CornerRadius(w * 0.08f), style = stroke)
        val keyW = w * 0.12f
        drawLine(tint, Offset(w * 0.25f, h * 0.45f), Offset(w * 0.25f + keyW, h * 0.45f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.44f, h * 0.45f), Offset(w * 0.44f + keyW, h * 0.45f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.63f, h * 0.45f), Offset(w * 0.63f + keyW, h * 0.45f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.35f, h * 0.60f), Offset(w * 0.65f, h * 0.60f), strokeWidth = sw)
    }
}

@Composable
fun TranslateIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.08f
        drawLine(tint, Offset(w * 0.35f, h * 0.25f), Offset(w * 0.15f, h * 0.75f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.35f, h * 0.25f), Offset(w * 0.55f, h * 0.75f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.25f, h * 0.55f), Offset(w * 0.45f, h * 0.55f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.65f, h * 0.35f), Offset(w * 0.85f, h * 0.35f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.75f, h * 0.25f), Offset(w * 0.75f, h * 0.75f), strokeWidth = sw)
        drawLine(tint, Offset(w * 0.65f, h * 0.75f), Offset(w * 0.85f, h * 0.75f), strokeWidth = sw)
    }
}

@Composable
fun ContentCopyIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width; val h = this.size.height; val sw = w * 0.08f
        val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val backRect = Rect(left = w * 0.30f, top = h * 0.30f, right = w * 0.82f, bottom = h * 0.82f)
        drawRoundRect(color = tint, topLeft = backRect.topLeft, size = backRect.size, cornerRadius = CornerRadius(w * 0.06f), style = Stroke(width = sw))
        val frontRect = Rect(left = w * 0.18f, top = h * 0.18f, right = w * 0.70f, bottom = h * 0.70f)
        drawRoundRect(color = Color.Transparent, topLeft = frontRect.topLeft, size = frontRect.size, cornerRadius = CornerRadius(w * 0.06f), style = Fill, blendMode = BlendMode.Clear)
        drawRoundRect(color = tint, topLeft = frontRect.topLeft, size = frontRect.size, cornerRadius = CornerRadius(w * 0.06f), style = Stroke(width = sw))
    }
}

@Composable
fun CurrencyIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension; val stroke = s * 0.10f; val center = Offset(s / 2f, s / 2f); val radius = s * 0.38f
        drawCircle(tint, radius, center, style = Stroke(stroke))
        val dollarStroke = stroke * 0.9f
        drawArc(color = tint, startAngle = -30f, sweepAngle = -180f, useCenter = false, topLeft = Offset(center.x - s * 0.10f, center.y - s * 0.16f), size = Size(s * 0.20f, s * 0.16f), style = Stroke(dollarStroke, cap = StrokeCap.Round))
        drawArc(color = tint, startAngle = 150f, sweepAngle = -180f, useCenter = false, topLeft = Offset(center.x - s * 0.10f, center.y), size = Size(s * 0.20f, s * 0.16f), style = Stroke(dollarStroke, cap = StrokeCap.Round))
        drawLine(tint, Offset(center.x, center.y - s * 0.22f), Offset(center.x, center.y + s * 0.22f), dollarStroke, StrokeCap.Round)
    }
}

@Composable
fun RefreshIcon(tint: Color, size: Dp = 16.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension; val stroke = s * 0.11f; val center = Offset(s / 2f, s / 2f); val radius = s * 0.32f
        drawArc(color = tint, startAngle = -90f, sweepAngle = 270f, useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius), size = Size(radius * 2, radius * 2), style = Stroke(width = stroke, cap = StrokeCap.Round))
        val arrowTipAngle = ((-90f + 270f) * PI / 180f).toFloat()
        val tipX = center.x + radius * cos(arrowTipAngle); val tipY = center.y + radius * sin(arrowTipAngle); val arrowLen = s * 0.14f
        val path = Path().apply { moveTo(tipX, tipY); lineTo(tipX + arrowLen, tipY - arrowLen * 0.1f); lineTo(tipX + arrowLen * 0.1f, tipY - arrowLen); close() }
        drawPath(path, tint)
    }
}

@Composable
fun ShareIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension; val stroke = s * 0.09f; val dotRadius = s * 0.07f
        val topRight = Offset(s * 0.75f, s * 0.22f); val midLeft = Offset(s * 0.25f, s * 0.50f); val botRight = Offset(s * 0.75f, s * 0.78f)
        drawLine(tint, midLeft, topRight, stroke, StrokeCap.Round)
        drawLine(tint, midLeft, botRight, stroke, StrokeCap.Round)
        drawCircle(tint, dotRadius, topRight); drawCircle(tint, dotRadius, midLeft); drawCircle(tint, dotRadius, botRight)
    }
}

@Composable
fun SwapHorizIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension; val stroke = s * 0.10f; val arrowHead = s * 0.14f
        val topY = s * 0.36f
        drawLine(tint, Offset(s * 0.20f, topY), Offset(s * 0.80f, topY), stroke, StrokeCap.Round)
        drawLine(tint, Offset(s * 0.80f, topY), Offset(s * 0.80f - arrowHead, topY - arrowHead), stroke, StrokeCap.Round)
        drawLine(tint, Offset(s * 0.80f, topY), Offset(s * 0.80f - arrowHead, topY + arrowHead), stroke, StrokeCap.Round)
        val botY = s * 0.64f
        drawLine(tint, Offset(s * 0.80f, botY), Offset(s * 0.20f, botY), stroke, StrokeCap.Round)
        drawLine(tint, Offset(s * 0.20f, botY), Offset(s * 0.20f + arrowHead, botY - arrowHead), stroke, StrokeCap.Round)
        drawLine(tint, Offset(s * 0.20f, botY), Offset(s * 0.20f + arrowHead, botY + arrowHead), stroke, StrokeCap.Round)
    }
}

@Composable
fun TravelExploreIcon(tint: Color, size: Dp = 16.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension; val stroke = s * 0.09f; val center = Offset(s * 0.44f, s * 0.44f); val globeR = s * 0.30f
        drawCircle(tint, globeR, center, style = Stroke(stroke))
        drawLine(tint, Offset(center.x - globeR, center.y), Offset(center.x + globeR, center.y), stroke * 0.7f, StrokeCap.Round)
        drawOval(color = tint, topLeft = Offset(center.x - globeR * 0.4f, center.y - globeR), size = Size(globeR * 0.8f, globeR * 2f), style = Stroke(stroke * 0.7f))
        val magCenter = Offset(s * 0.72f, s * 0.72f); val magR = s * 0.12f
        drawCircle(tint, magR, magCenter, style = Stroke(stroke))
        drawLine(tint, Offset(magCenter.x + magR * 0.707f, magCenter.y + magR * 0.707f), Offset(s * 0.90f, s * 0.90f), stroke, StrokeCap.Round)
    }
}
