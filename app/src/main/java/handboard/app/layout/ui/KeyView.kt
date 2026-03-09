package handboard.app.layout.ui

import android.media.AudioManager
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
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
    val currentOnCursorMove by rememberUpdatedState(onCursorMove)

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
        is KeyAction.Shift -> if (isCapsLock) "Caps Lock" else if (isShifted) "Shift on" else "Shift"
        is KeyAction.Space -> "Space"
        is KeyAction.SwitchToSymbols -> "Symbols"
        is KeyAction.SwitchToLetters -> "Letters"
        is KeyAction.Text -> if (isShifted || isCapsLock) keyData.label.uppercase() else keyData.label
    }

    fun playFeedback() {
        if (currentHaptic) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        if (currentSound) {
            try {
                (context.getSystemService(android.content.Context.AUDIO_SERVICE) as? AudioManager)
                    ?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, -1f)
            } catch (_: Exception) {}
        }
    }

    val baseModifier = modifier
        .padding(2.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(bgColor)
        .semantics { contentDescription = description; role = Role.Button }

    // === SPACE BAR: tap = space, long-press + drag = cursor ===
    if (keyData.action is KeyAction.Space) {
        Box(
            modifier = baseModifier
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        playFeedback()

                        var totalDragX = 0f
                        var isDragging = false
                        var cursorMoved = false
                        val threshold = 30f // pixels — significant drag threshold

                        // Wait for movement or release
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Release) {
                                break
                            }
                            if (event.type == PointerEventType.Move) {
                                val change = event.changes.firstOrNull() ?: continue
                                val dx = change.position.x - down.position.x
                                totalDragX = dx

                                if (!isDragging && abs(totalDragX) > threshold) {
                                    isDragging = true
                                }

                                if (isDragging && abs(totalDragX) > threshold) {
                                    val dir = if (totalDragX > 0) 1 else -1
                                    currentOnCursorMove?.invoke(dir)
                                    cursorMoved = true
                                    totalDragX = 0f
                                    if (currentHaptic) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    // Reset down position tracking
                                }
                            }
                        }

                        // If no significant drag occurred, it's a tap → insert space
                        if (!cursorMoved) {
                            currentOnClick()
                        }
                    }
                }
                .defaultMinSize(minHeight = minH),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "space", color = KeyTextDim, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        return
    }

    // === BACKSPACE: hold to repeat ===
    if (keyData.action is KeyAction.Backspace) {
        Box(
            modifier = baseModifier
                .pointerInput(keyData.label) {
                    awaitEachGesture {
                        awaitFirstDown(); playFeedback(); currentOnClick()
                        val job = scope.launch { delay(400L); while (true) { currentOnClick(); delay(50L) } }
                        waitForUpOrCancellation(); job.cancel()
                    }
                }
                .defaultMinSize(minHeight = minH),
            contentAlignment = Alignment.Center
        ) { BackspaceIcon(tint = KeyText, size = 20.dp) }
        return
    }

    // === TEXT KEYS: long press for alt chars ===
    if (keyData.action is KeyAction.Text) {
        val altChars = remember(keyData.label) { AltChars.getAlts(keyData.label) }
        val hasAlts = altChars.isNotEmpty()

        Box(
            modifier = baseModifier
                .pointerInput(keyData.label) {
                    awaitEachGesture {
                        awaitFirstDown(); playFeedback()
                        var longJob: Job? = null; var wasLong = false
                        if (hasAlts) {
                            longJob = scope.launch {
                                delay(500L); wasLong = true
                                if (currentHaptic) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showAltPopup = true
                            }
                        }
                        waitForUpOrCancellation(); longJob?.cancel()
                        if (!wasLong) currentOnClick()
                    }
                }
                .defaultMinSize(minHeight = minH),
            contentAlignment = Alignment.Center
        ) {
            val display = if ((isShifted || isCapsLock) && currentLayer == KeyboardLayer.LETTERS) {
                (keyData.action as KeyAction.Text).char.uppercase()
            } else (keyData.action as KeyAction.Text).char
            Text(text = display, color = KeyText, fontSize = 18.sp, fontWeight = FontWeight.Normal)
            if (hasAlts) {
                Text("·", color = KeyTextDim.copy(alpha = 0.4f), fontSize = 8.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(end = 3.dp, top = 1.dp))
            }
            if (showAltPopup) {
                AltCharsPopup(chars = altChars, isShifted = isShifted || isCapsLock,
                    onSelect = { currentOnAltChar?.invoke(it) }, onDismiss = { showAltPopup = false })
            }
        }
        return
    }

    // === ALL OTHER KEYS: simple click ===
    Box(
        modifier = baseModifier.clickable { playFeedback(); currentOnClick() }.defaultMinSize(minHeight = minH),
        contentAlignment = Alignment.Center
    ) {
        when (keyData.action) {
            is KeyAction.Enter -> EnterIcon(tint = KeyText, size = 20.dp)
            is KeyAction.Shift -> {
                when (currentLayer) {
                    KeyboardLayer.LETTERS -> when {
                        isCapsLock -> CapsLockIcon(tint = KeyText, size = 20.dp)
                        isShifted -> ShiftIcon(tint = KeyText, size = 20.dp, filled = true)
                        else -> ShiftIcon(tint = KeyText, size = 20.dp, filled = false)
                    }
                    KeyboardLayer.SYMBOLS -> Text("=\\<", color = KeyTextDim, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    KeyboardLayer.SYMBOLS2 -> Text("123", color = KeyTextDim, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
            is KeyAction.SwitchToSymbols, is KeyAction.SwitchToLetters -> {
                Text(keyData.label, color = KeyTextDim, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            else -> Text(keyData.label, color = KeyText, fontSize = 16.sp)
        }
    }
}
