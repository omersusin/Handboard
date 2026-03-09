package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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
import handboard.app.layout.KeyboardLayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun KeyView(
    modifier: Modifier = Modifier,
    keyData: KeyData,
    isShifted: Boolean,
    isCapsLock: Boolean,
    currentLayer: KeyboardLayer = KeyboardLayer.LETTERS,
    heightScale: Float = 1f,
    hapticEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val currentOnClick by rememberUpdatedState(onClick)
    val currentHaptic by rememberUpdatedState(hapticEnabled)

    val bgColor = when {
        keyData.action is KeyAction.Shift && (isShifted || isCapsLock) && currentLayer == KeyboardLayer.LETTERS -> ShiftActiveBackground
        keyData.style == KeyStyle.ACTION -> ActionKeyBackground
        keyData.style == KeyStyle.SPECIAL -> ActionKeyBackground
        else -> KeyBackground
    }

    val minH = (48 * heightScale).dp

    // Accessibility description
    val description = when (keyData.action) {
        is KeyAction.Backspace -> "Backspace"
        is KeyAction.Enter -> "Enter"
        is KeyAction.Shift -> when (currentLayer) {
            KeyboardLayer.LETTERS -> if (isCapsLock) "Caps Lock on" else if (isShifted) "Shift on" else "Shift"
            KeyboardLayer.SYMBOLS -> "More symbols"
            KeyboardLayer.SYMBOLS2 -> "Back to symbols"
        }
        is KeyAction.Space -> "Space"
        is KeyAction.SwitchToSymbols -> "Switch to symbols"
        is KeyAction.SwitchToLetters -> "Switch to letters"
        is KeyAction.Text -> {
            val display = if (isShifted || isCapsLock) keyData.label.uppercase() else keyData.label
            display
        }
    }

    val baseModifier = modifier
        .padding(2.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(bgColor)
        .semantics {
            contentDescription = description
            role = Role.Button
        }

    val interactiveModifier = when (keyData.action) {
        is KeyAction.Backspace -> {
            baseModifier.pointerInput(keyData.label) {
                awaitEachGesture {
                    awaitFirstDown()
                    if (currentHaptic) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    currentOnClick()
                    val job = scope.launch {
                        delay(400L)
                        while (true) {
                            currentOnClick()
                            delay(50L)
                        }
                    }
                    waitForUpOrCancellation()
                    job.cancel()
                }
            }
        }
        else -> {
            baseModifier.clickable {
                if (currentHaptic) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                currentOnClick()
            }
        }
    }

    Box(
        modifier = interactiveModifier.defaultMinSize(minHeight = minH),
        contentAlignment = Alignment.Center
    ) {
        KeyContentRenderer(
            action = keyData.action,
            label = keyData.label,
            style = keyData.style,
            isShifted = isShifted,
            isCapsLock = isCapsLock,
            currentLayer = currentLayer
        )
    }
}

@Composable
private fun KeyContentRenderer(
    action: KeyAction,
    label: String,
    style: KeyStyle,
    isShifted: Boolean,
    isCapsLock: Boolean,
    currentLayer: KeyboardLayer
) {
    when (action) {
        is KeyAction.Backspace -> BackspaceIcon(tint = KeyText, size = 22.dp)
        is KeyAction.Enter -> EnterIcon(tint = KeyText, size = 22.dp)
        is KeyAction.Shift -> {
            when (currentLayer) {
                KeyboardLayer.LETTERS -> {
                    if (isCapsLock) CapsLockIcon(tint = KeyText, size = 22.dp)
                    else if (isShifted) ShiftIcon(tint = KeyText, size = 22.dp, filled = true)
                    else ShiftIcon(tint = KeyText, size = 22.dp, filled = false)
                }
                KeyboardLayer.SYMBOLS -> {
                    Text(text = "=\\<", color = KeyTextDim, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                KeyboardLayer.SYMBOLS2 -> {
                    Text(text = "123", color = KeyTextDim, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        is KeyAction.Space -> {
            Text(text = "space", color = KeyTextDim, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        is KeyAction.SwitchToSymbols, is KeyAction.SwitchToLetters -> {
            Text(text = label, color = KeyTextDim, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        is KeyAction.Text -> {
            val display = if ((isShifted || isCapsLock) && currentLayer == KeyboardLayer.LETTERS) {
                action.char.uppercase()
            } else action.char
            Text(text = display, color = KeyText, fontSize = 18.sp, fontWeight = FontWeight.Normal)
        }
    }
}
