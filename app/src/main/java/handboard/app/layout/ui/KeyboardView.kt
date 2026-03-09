package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import handboard.app.core.theme.NumberRowBackground
import handboard.app.emoji.EmojiView
import handboard.app.emoji.KaomojiView
import handboard.app.layout.KeyAction
import handboard.app.layout.KeyData
import handboard.app.layout.KeyStyle
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
    soundEnabled: Boolean = false,
    numberRowEnabled: Boolean = false,
    spacebarCursor: Boolean = true,
    clipboardEnabled: Boolean = false,
    clipboardHistory: ClipboardHistory? = null,
    suggestionBar: (@Composable () -> Unit)? = null,
    onTextInput: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onEmojiInput: (String) -> Unit = onTextInput,
    onCursorMove: (Int) -> Unit = {},
    onCursorHome: () -> Unit = {},
    onCursorEnd: () -> Unit = {},
    onSelectAll: () -> Unit = {},
    onCopy: () -> Unit = {},
    onCut: () -> Unit = {},
    onPaste: () -> Unit = {},
    onUndo: () -> Unit = {},
    onRedo: () -> Unit = {},
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

    // Number row data
    val numberRow = listOf("1","2","3","4","5","6","7","8","9","0").map {
        KeyData(it, KeyAction.Text(it), 1f, KeyStyle.NORMAL)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(KeyboardBackground)
    ) {
        // Suggestion bar (keyboard panel only)
        if (currentPanel == KeyboardPanel.KEYBOARD) {
            suggestionBar?.invoke()
        }

        // Toolbar
        LayoutToolbar(
            currentLayoutName = layoutSwitcher.currentLayoutName,
            currentPanel = currentPanel,
            clipboardEnabled = clipboardEnabled,
            onSwitchLayout = {
                layoutSwitcher.nextLayout()
                state.switchToLetters()
                currentPanel = KeyboardPanel.KEYBOARD
                scope.launch { preferencesManager.setSelectedLayout(layoutSwitcher.currentLayoutName) }
            },
            onSwitchPanel = { panel -> currentPanel = panel }
        )

        // Panel content
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
                        // Optional number row
                        if (numberRowEnabled && state.currentLayer == KeyboardLayer.LETTERS) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NumberRowBackground)
                                    .padding(vertical = 1.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                numberRow.forEach { keyData ->
                                    KeyView(
                                        modifier = Modifier.weight(1f),
                                        keyData = keyData,
                                        isShifted = false,
                                        isCapsLock = false,
                                        currentLayer = state.currentLayer,
                                        heightScale = heightScale * 0.8f,
                                        hapticEnabled = hapticEnabled,
                                        soundEnabled = soundEnabled,
                                        onClick = { onTextInput(keyData.label) }
                                    )
                                }
                            }
                        }

                        // Main key rows
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
                                        soundEnabled = soundEnabled,
                                        onCursorMove = if (spacebarCursor && keyData.action is KeyAction.Space) {
                                            { dir -> onCursorMove(dir) }
                                        } else null,
                                        onAltChar = { char -> onTextInput(char) },
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

            KeyboardPanel.KAOMOJI -> {
                KaomojiView(
                    heightScale = heightScale,
                    onKaomojiClick = { onTextInput(it) }
                )
            }

            KeyboardPanel.TEXT_EDITING -> {
                TextEditingBar(
                    onCursorLeft = { onCursorMove(-1) },
                    onCursorRight = { onCursorMove(1) },
                    onCursorHome = onCursorHome,
                    onCursorEnd = onCursorEnd,
                    onSelectAll = onSelectAll,
                    onCopy = onCopy,
                    onCut = onCut,
                    onPaste = onPaste,
                    onUndo = onUndo,
                    onRedo = onRedo,
                    onClose = { currentPanel = KeyboardPanel.KEYBOARD }
                )
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
