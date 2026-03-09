package handboard.app.layout.ui

import android.media.AudioManager
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
import handboard.app.layout.AltChars
import handboard.app.layout.KeyAction
import handboard.app.layout.KeyData
import handboard.app.layout.KeyStyle
import handboard.app.layout.KeyboardLayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun KeyView(
    modifier: Modifier = Modifier,
    keyData: KeyData,
    isShifted: Boolean,
    isCapsLock: Boolean,
    currentLayer: KeyboardLayer = KeyboardLayer.LETTERS,
    heightScale: Float = 1f,
    hapticEnabled: Boolean = true,
    soundEnabled: Boolean = false,
    onCursorMove: ((Int) -> Unit)? = null,
    onAltChar: ((String) -> Unit)? = null,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentOnClick by rememberUpdatedState(onClick)
    val currentHaptic by rememberUpdatedState(hapticEnabled)
    val currentSound by rememberUpdatedState(soundEnabled)
    val currentOnAltChar by rememberUpdatedState(onAltChar)

    var showAltPopup by remember { mutableStateOf(false) }

    val bgColor = when {
        keyData.action is KeyAction.Shift && (isShifted || isCapsLock) && currentLayer == KeyboardLayer.LETTERS -> ShiftActiveBackground
        keyData.style == KeyStyle.ACTION -> ActionKeyBackground
        keyData.style == KeyStyle.SPECIAL -> ActionKeyBackground
        else -> KeyBackground
    }

    val minH = (48 * heightScale).dp

    val description = when (keyData.action) {
        is KeyAction.Backspace -> "Backspace"
        is KeyAction.Enter -> "Enter"
        is KeyAction.Shift -> when (currentLayer) {
            KeyboardLayer.LETTERS -> if (isCapsLock) "Caps Lock on" else if (isShifted) "Shift on" else "Shift"
            else -> "More symbols"
        }
        is KeyAction.Space -> "Space"
        is KeyAction.SwitchToSymbols -> "Switch to symbols"
        is KeyAction.SwitchToLetters -> "Switch to letters"
        is KeyAction.Text -> if (isShifted || isCapsLock) keyData.label.uppercase() else keyData.label
    }

    fun playFeedback() {
        if (currentHaptic) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        if (currentSound) {
            try {
                val am = context.getSystemService(android.content.Context.AUDIO_SERVICE) as? AudioManager
                am?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, -1f)
            } catch (_: Exception) {}
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

    // Spacebar: horizontal drag for cursor control
    if (keyData.action is KeyAction.Space && onCursorMove != null) {
        var dragAccumulator by remember { mutableFloatStateOf(0f) }
        val threshold = 25f

        Box(
            modifier = baseModifier
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            dragAccumulator = 0f
                            playFeedback()
                        },
                        onDragEnd = {
                            if (abs(dragAccumulator) < threshold) {
                                currentOnClick()
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            dragAccumulator += dragAmount
                            if (abs(dragAccumulator) >= threshold) {
                                val direction = if (dragAccumulator > 0) 1 else -1
                                onCursorMove(direction)
                                dragAccumulator = 0f
                                if (currentHaptic) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    )
                }
                .defaultMinSize(minHeight = minH),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "space", color = KeyTextDim, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        return
    }

    // Backspace: hold to repeat
    if (keyData.action is KeyAction.Backspace) {
        Box(
            modifier = baseModifier
                .pointerInput(keyData.label) {
                    awaitEachGesture {
                        awaitFirstDown()
                        playFeedback()
                        currentOnClick()
                        val job = scope.launch {
                            delay(400L)
                            while (true) { currentOnClick(); delay(50L) }
                        }
                        waitForUpOrCancellation()
                        job.cancel()
                    }
                }
                .defaultMinSize(minHeight = minH),
            contentAlignment = Alignment.Center
        ) {
            BackspaceIcon(tint = KeyText, size = 22.dp)
        }
        return
    }

    // Text keys: long press for alt chars
    if (keyData.action is KeyAction.Text) {
        val altChars = remember(keyData.label) { AltChars.getAlts(keyData.label) }
        val hasAlts = altChars.isNotEmpty()

        Box(
            modifier = baseModifier
                .pointerInput(keyData.label) {
                    awaitEachGesture {
                        awaitFirstDown()
                        playFeedback()

                        var longPressJob: Job? = null
                        var wasLongPress = false

                        if (hasAlts) {
                            longPressJob = scope.launch {
                                delay(500L)
                                wasLongPress = true
                                if (currentHaptic) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showAltPopup = true
                            }
                        }

                        waitForUpOrCancellation()
                        longPressJob?.cancel()

                        if (!wasLongPress) {
                            currentOnClick()
                        }
                    }
                }
                .defaultMinSize(minHeight = minH),
            contentAlignment = Alignment.Center
        ) {
            val display = if ((isShifted || isCapsLock) && currentLayer == KeyboardLayer.LETTERS) {
                (keyData.action as KeyAction.Text).char.uppercase()
            } else (keyData.action as KeyAction.Text).char

            Text(text = display, color = KeyText, fontSize = 18.sp, fontWeight = FontWeight.Normal)

            // Alt chars indicator dot
            if (hasAlts) {
                Text(
                    text = "·",
                    color = KeyTextDim.copy(alpha = 0.4f),
                    fontSize = 8.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(end = 4.dp, top = 2.dp)
                )
            }

            // Alt chars popup
            if (showAltPopup) {
                AltCharsPopup(
                    chars = altChars,
                    isShifted = isShifted || isCapsLock,
                    onSelect = { char ->
                        currentOnAltChar?.invoke(char)
                    },
                    onDismiss = { showAltPopup = false }
                )
            }
        }
        return
    }

    // All other keys: simple click
    Box(
        modifier = baseModifier
            .clickable {
                playFeedback()
                currentOnClick()
            }
            .defaultMinSize(minHeight = minH),
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
        is KeyAction.Enter -> EnterIcon(tint = KeyText, size = 22.dp)
        is KeyAction.Shift -> {
            when (currentLayer) {
                KeyboardLayer.LETTERS -> {
                    if (isCapsLock) CapsLockIcon(tint = KeyText, size = 22.dp)
                    else if (isShifted) ShiftIcon(tint = KeyText, size = 22.dp, filled = true)
                    else ShiftIcon(tint = KeyText, size = 22.dp, filled = false)
                }
                KeyboardLayer.SYMBOLS -> Text(text = "=\\<", color = KeyTextDim, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                KeyboardLayer.SYMBOLS2 -> Text(text = "123", color = KeyTextDim, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
        is KeyAction.SwitchToSymbols, is KeyAction.SwitchToLetters -> {
            Text(text = label, color = KeyTextDim, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        else -> {
            Text(text = label, color = KeyText, fontSize = 18.sp, fontWeight = FontWeight.Normal)
        }
    }
}
