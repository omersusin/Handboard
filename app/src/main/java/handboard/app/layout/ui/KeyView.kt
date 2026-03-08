package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
fun KeyView(
    modifier: Modifier = Modifier,
    keyData: KeyData,
    isShifted: Boolean,
    isCapsLock: Boolean,
    heightScale: Float = 1f,
    hapticEnabled: Boolean = true,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val currentOnClick by rememberUpdatedState(onClick)
    val currentHaptic by rememberUpdatedState(hapticEnabled)

    val bgColor = when {
        keyData.action is KeyAction.Shift && (isShifted || isCapsLock) -> ShiftActiveBackground
        keyData.style == KeyStyle.ACTION -> ActionKeyBackground
        keyData.style == KeyStyle.SPECIAL -> ActionKeyBackground
        else -> KeyBackground
    }

    val minKeyHeight = (48 * heightScale).dp

    val keyModifier = if (keyData.action is KeyAction.Backspace) {
        modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .pointerInput(keyData.label) {
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
            .defaultMinSize(minHeight = minKeyHeight)
    } else {
        modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable {
                if (currentHaptic) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                currentOnClick()
            }
            .defaultMinSize(minHeight = minKeyHeight)
    }

    Box(modifier = keyModifier, contentAlignment = Alignment.Center) {
        when (val action = keyData.action) {
            is KeyAction.Backspace -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = KeyText,
                    modifier = Modifier.size(22.dp)
                )
            }
            is KeyAction.Enter -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
                    contentDescription = "Enter",
                    tint = KeyText,
                    modifier = Modifier.size(22.dp)
                )
            }
            is KeyAction.Shift -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.ArrowUpward,
                        contentDescription = if (isCapsLock) "Caps Lock" else "Shift",
                        tint = KeyText,
                        modifier = Modifier.size(22.dp)
                    )
                    if (isCapsLock) {
                        HorizontalDivider(
                            modifier = Modifier.width(14.dp),
                            thickness = 2.dp,
                            color = KeyText
                        )
                    }
                }
            }
            is KeyAction.Space -> {
                Text(text = "space", color = KeyTextDim, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            is KeyAction.SwitchToSymbols -> {
                Text(text = keyData.label, color = KeyTextDim, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            is KeyAction.SwitchToLetters -> {
                Text(text = keyData.label, color = KeyTextDim, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            is KeyAction.Text -> {
                val displayLabel = if (isShifted || isCapsLock) action.char.uppercase() else action.char
                Text(text = displayLabel, color = KeyText, fontSize = 18.sp, fontWeight = FontWeight.Normal)
            }
        }
    }
}
