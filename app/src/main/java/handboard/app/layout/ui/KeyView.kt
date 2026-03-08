package handboard.app.layout.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import handboard.app.core.theme.ActionKeyBackground
import handboard.app.core.theme.KeyBackground
import handboard.app.core.theme.KeyText
import handboard.app.core.theme.KeyTextDim
import handboard.app.core.theme.ShiftActiveBackground
import handboard.app.layout.KeyAction
import handboard.app.layout.KeyData
import handboard.app.layout.KeyStyle

@Composable
fun RowScope.KeyView(
    keyData: KeyData,
    isShifted: Boolean,
    onClick: () -> Unit,
    onLongPressStart: (() -> Unit)? = null,
    onLongPressEnd: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 50),
        label = "keyScale"
    )

    val bgColor = when {
        keyData.action is KeyAction.Shift && isShifted -> ShiftActiveBackground
        isPressed -> ShiftActiveBackground.copy(alpha = 0.5f)
        keyData.style == KeyStyle.ACTION -> ActionKeyBackground
        keyData.style == KeyStyle.SPECIAL -> ActionKeyBackground
        else -> KeyBackground
    }

    val textColor = when (keyData.style) {
        KeyStyle.SPECIAL -> KeyTextDim
        else -> KeyText
    }

    val displayLabel = when {
        keyData.action is KeyAction.Text && isShifted ->
            keyData.label.uppercase()
        else -> keyData.label
    }

    val fontSize = when {
        keyData.action is KeyAction.Space -> 13.sp
        keyData.style == KeyStyle.SPECIAL -> 13.sp
        keyData.style == KeyStyle.ACTION -> 18.sp
        else -> 18.sp
    }

    val isBackspace = keyData.action is KeyAction.Backspace

    Box(
        modifier = Modifier
            .weight(keyData.widthWeight)
            .padding(2.dp)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if (isBackspace) onLongPressStart?.invoke()
                        tryAwaitRelease()
                        isPressed = false
                        if (isBackspace) onLongPressEnd?.invoke()
                    },
                    onTap = {
                        if (!isBackspace) onClick()
                    }
                )
            }
            .defaultMinSize(minHeight = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayLabel,
            color = textColor,
            fontSize = fontSize,
            fontWeight = if (keyData.style == KeyStyle.NORMAL) FontWeight.Normal else FontWeight.Medium
        )
    }
}
