package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RowScope.KeyView(
    keyData: KeyData,
    isShifted: Boolean,
    isCapsLock: Boolean,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val currentOnClick by rememberUpdatedState(onClick)

    val bgColor = when {
        keyData.action is KeyAction.Shift && (isShifted || isCapsLock) -> ShiftActiveBackground
        keyData.style == KeyStyle.ACTION -> ActionKeyBackground
        keyData.style == KeyStyle.SPECIAL -> ActionKeyBackground
        else -> KeyBackground
    }

    val contentColor = when (keyData.style) {
        KeyStyle.SPECIAL -> KeyTextDim
        else -> KeyText
    }

    val interactionModifier = when (keyData.action) {
        is KeyAction.Backspace -> {
            Modifier.pointerInput(keyData.label) {
                awaitEachGesture {
                    awaitFirstDown()
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    currentOnClick()
                    val repeatJob = scope.launch {
                        delay(400L)
                        while (true) {
                            currentOnClick()
                            delay(50L)
                        }
                    }
                    waitForUpOrCancellation()
                    repeatJob.cancel()
                }
            }
        }
        else -> {
            Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                currentOnClick()
            }
        }
    }

    Box(
        modifier = Modifier
            .weight(keyData.widthWeight)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .then(interactionModifier)
            .defaultMinSize(minHeight = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        KeyContent(
            keyData = keyData,
            isShifted = isShifted,
            isCapsLock = isCapsLock,
            contentColor = contentColor
        )
    }
}

@Composable
private fun KeyContent(
    keyData: KeyData,
    isShifted: Boolean,
    isCapsLock: Boolean,
    contentColor: Color
) {
    when (keyData.action) {
        is KeyAction.Backspace -> {
            Icon(
                imageVector = Icons.Filled.Backspace,
                contentDescription = "Backspace",
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
        }
        is KeyAction.Enter -> {
            Icon(
                imageVector = Icons.Filled.KeyboardReturn,
                contentDescription = "Enter",
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
        }
        is KeyAction.Shift -> {
            Icon(
                imageVector = if (isCapsLock) Icons.Filled.KeyboardDoubleArrowUp
                              else Icons.Filled.KeyboardArrowUp,
                contentDescription = if (isCapsLock) "Caps Lock" else "Shift",
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
        }
        is KeyAction.Space -> {
            Text(
                text = "space",
                color = KeyTextDim,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
        is KeyAction.SwitchToSymbols, is KeyAction.SwitchToLetters -> {
            Text(
                text = keyData.label,
                color = KeyTextDim,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
        is KeyAction.Text -> {
            val displayLabel = if (isShifted || isCapsLock) {
                keyData.label.uppercase()
            } else {
                keyData.label
            }
            Text(
                text = displayLabel,
                color = contentColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
