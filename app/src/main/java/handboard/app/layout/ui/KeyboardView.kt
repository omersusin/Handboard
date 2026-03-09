package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import handboard.app.clipboard.ClipboardHistory
import handboard.app.clipboard.ClipboardItem
import handboard.app.clipboard.ClipboardView
import handboard.app.core.theme.KeyboardBackground
import handboard.app.emoji.EmojiView
import handboard.app.layout.KeyAction
import handboard.app.layout.KeyData
import handboard.app.layout.KeyboardLayer
import handboard.app.layout.KeyboardState
import handboard.app.layout.LayoutSwitcher
import handboard.app.settings.PreferencesManager
import kotlinx.coroutines.launch

@Composable
fun KeyboardView(
    layoutSwitcher: LayoutSwitcher,
    preferencesManager: PreferencesManager,
    heightScale: Float = 1f,
    hapticEnabled: Boolean = true,
    clipboardHistory: ClipboardHistory? = null,
    suggestionBar: (@Composable () -> Unit)? = null,
    onTextInput: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onEmojiInput: (String) -> Unit = onTextInput,
    onPasteImage: (ClipboardItem) -> Unit = {}
) {
    val state = remember { KeyboardState() }
    val layout = layoutSwitcher.currentLayout
    var currentPanel by remember { mutableStateOf(KeyboardPanel.KEYBOARD) }
    val scope = rememberCoroutineScope()

    val hasSymbolRows2 = layout.symbolRows2.isNotEmpty()

    val currentRows = when (state.currentLayer) {
        KeyboardLayer.LETTERS -> layout.letterRows
        KeyboardLayer.SYMBOLS -> layout.symbolRows
        KeyboardLayer.SYMBOLS2 -> if (hasSymbolRows2) layout.symbolRows2 else layout.symbolRows
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(KeyboardBackground)
    ) {
        if (currentPanel == KeyboardPanel.KEYBOARD) {
            suggestionBar?.invoke()
        }

        LayoutToolbar(
            currentLayoutName = layoutSwitcher.currentLayoutName,
            currentPanel = currentPanel,
            onSwitchLayout = {
                layoutSwitcher.nextLayout()
                state.switchToLetters()
                currentPanel = KeyboardPanel.KEYBOARD
                // Persist layout selection (#7)
                scope.launch {
                    preferencesManager.setSelectedLayout(layoutSwitcher.currentLayoutName)
                }
            },
            onSwitchPanel = { panel -> currentPanel = panel }
        )

        when (currentPanel) {
            KeyboardPanel.KEYBOARD -> {
                key(layoutSwitcher.currentLayoutName, state.currentLayer) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 3.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        currentRows.forEach { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 1.dp)
                            ) {
                                row.forEach { keyData ->
                                    KeyView(
                                        modifier = Modifier.weight(keyData.widthWeight),
                                        keyData = keyData,
                                        isShifted = state.shouldUpperCase,
                                        isCapsLock = state.isCapsLock,
                                        currentLayer = state.currentLayer,
                                        heightScale = heightScale,
                                        hapticEnabled = hapticEnabled,
                                        onClick = {
                                            handleKeyPress(
                                                keyData, state, hasSymbolRows2,
                                                onTextInput, onBackspace, onEnter
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            KeyboardPanel.EMOJI -> {
                EmojiView(
                    heightScale = heightScale,
                    onEmojiClick = { onEmojiInput(it) },
                    onBackspace = onBackspace
                )
            }
            KeyboardPanel.CLIPBOARD -> {
                if (clipboardHistory != null) {
                    ClipboardView(
                        clipboardHistory = clipboardHistory,
                        heightScale = heightScale,
                        onPasteText = { onTextInput(it) },
                        onPasteImage = { onPasteImage(it) },
                        onClearAll = { clipboardHistory.clearAll() }
                    )
                }
            }
        }
    }
}

private fun handleKeyPress(
    keyData: KeyData,
    state: KeyboardState,
    hasSymbolRows2: Boolean,
    onTextInput: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit
) {
    when (val action = keyData.action) {
        is KeyAction.Text -> {
            val text = if (state.shouldUpperCase && state.currentLayer == KeyboardLayer.LETTERS) {
                action.char.uppercase()
            } else action.char
            onTextInput(text)
            state.onTextCommitted()
        }
        KeyAction.Space -> {
            onTextInput(" ")
            state.onTextCommitted()
        }
        KeyAction.Backspace -> onBackspace()
        KeyAction.Enter -> onEnter()
        KeyAction.Shift -> state.handleShiftPress(hasSymbolRows2)
        KeyAction.SwitchToSymbols -> state.switchToSymbols()
        KeyAction.SwitchToLetters -> state.switchToLetters()
    }
}
