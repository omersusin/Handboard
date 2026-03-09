package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    
    // Search specific states
    var searchQuery by remember { mutableStateOf("") }
    val searchEngine = remember(clipboardHistory) { SearchEngine(clipboardHistory) }
    val searchResults = remember { mutableStateListOf<SearchResult>() }

    LaunchedEffect(searchQuery) {
        searchResults.clear()
        if (searchQuery.isNotBlank()) searchResults.addAll(searchEngine.search(searchQuery))
    }

    val hasSymbolRows2 = layout.symbolRows2.isNotEmpty()
    val currentRows = when (state.currentLayer) {
        KeyboardLayer.LETTERS -> layout.letterRows
        KeyboardLayer.SYMBOLS -> layout.symbolRows
        KeyboardLayer.SYMBOLS2 -> if (hasSymbolRows2) layout.symbolRows2 else layout.symbolRows
    }

    val numberRow = listOf("1","2","3","4","5","6","7","8","9","0").map {
        KeyData(it, KeyAction.Text(it), 1f, KeyStyle.NORMAL)
    }

    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)).background(KeyboardBackground)) {
        
        if (currentPanel == KeyboardPanel.KEYBOARD) suggestionBar?.invoke()

        if (currentPanel != KeyboardPanel.SEARCH) {
            LayoutToolbar(
                currentLayoutName = layoutSwitcher.currentLayoutName, currentPanel = currentPanel, clipboardEnabled = clipboardEnabled,
                onSwitchLayout = {
                    layoutSwitcher.nextLayout()
                    state.switchToLetters()
                    currentPanel = KeyboardPanel.KEYBOARD
                    scope.launch { preferencesManager.setSelectedLayout(layoutSwitcher.currentLayoutName) }
                },
                onSwitchPanel = { currentPanel = it; if (it == KeyboardPanel.SEARCH) searchQuery = "" }
            )
        } else {
            SearchPanel(
                query = searchQuery, results = searchResults, heightScale = heightScale,
                onResultClick = { onTextInput(it); currentPanel = KeyboardPanel.KEYBOARD; searchQuery = "" },
                onClose = { currentPanel = KeyboardPanel.KEYBOARD; searchQuery = "" }
            )
        }

        // Render KEYS if we are in KEYBOARD or SEARCH mode
        if (currentPanel == KeyboardPanel.KEYBOARD || currentPanel == KeyboardPanel.SEARCH) {
            key(layoutSwitcher.currentLayoutName, state.currentLayer) {
                Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 4.dp).padding(bottom = 6.dp)) {
                    if (numberRowEnabled && state.currentLayer == KeyboardLayer.LETTERS) {
                        Row(modifier = Modifier.fillMaxWidth().background(NumberRowBackground).padding(vertical = 1.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            numberRow.forEach { kd ->
                                KeyView(modifier = Modifier.weight(1f), keyData = kd, isShifted = false, isCapsLock = false, currentLayer = state.currentLayer, heightScale = heightScale * 0.8f, hapticEnabled = hapticEnabled, soundEnabled = soundEnabled,
                                    onClick = { 
                                        if (currentPanel == KeyboardPanel.SEARCH) searchQuery += kd.label else onTextInput(kd.label)
                                    }
                                )
                            }
                        }
                    }

                    currentRows.forEach { row ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 1.dp)) {
                            row.forEach { kd ->
                                KeyView(
                                    modifier = Modifier.weight(kd.widthWeight), keyData = kd, isShifted = state.shouldUpperCase, isCapsLock = state.isCapsLock, currentLayer = state.currentLayer, heightScale = heightScale, hapticEnabled = hapticEnabled, soundEnabled = soundEnabled,
                                    onCursorMove = if (spacebarCursor && kd.action is KeyAction.Space) { { dir -> onCursorMove(dir) } } else null,
                                    onAltChar = { if (currentPanel == KeyboardPanel.SEARCH) searchQuery += it else onTextInput(it) },
                                    onClick = {
                                        if (currentPanel == KeyboardPanel.SEARCH) {
                                            when (val act = kd.action) {
                                                is KeyAction.Text -> searchQuery += if (state.shouldUpperCase && state.currentLayer == KeyboardLayer.LETTERS) act.char.uppercase() else act.char
                                                is KeyAction.Space -> searchQuery += " "
                                                is KeyAction.Backspace -> if (searchQuery.isNotEmpty()) searchQuery = searchQuery.dropLast(1)
                                                is KeyAction.Shift -> state.handleShiftPress(hasSymbolRows2)
                                                is KeyAction.SwitchToSymbols -> state.switchToSymbols()
                                                is KeyAction.SwitchToLetters -> state.switchToLetters()
                                                else -> {}
                                            }
                                        } else {
                                            handleKeyPress(kd, state, hasSymbolRows2, onTextInput, onBackspace, onEnter)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        } else if (currentPanel == KeyboardPanel.EMOJI) {
            EmojiView(heightScale = heightScale, onEmojiClick = { onEmojiInput(it) }, onBackspace = onBackspace)
        } else if (currentPanel == KeyboardPanel.CLIPBOARD) {
            if (clipboardHistory != null) ClipboardView(clipboardHistory = clipboardHistory, heightScale = heightScale, onPasteText = { onTextInput(it) }, onPasteImage = { onPasteImage(it) }, onClearAll = { clipboardHistory.clearAll() })
        } else if (currentPanel == KeyboardPanel.KAOMOJI) {
            KaomojiView(heightScale = heightScale, onKaomojiClick = { onTextInput(it) })
        } else if (currentPanel == KeyboardPanel.TEXT_EDITING) {
            TextEditingBar(onCursorLeft = { onCursorMove(-1) }, onCursorRight = { onCursorMove(1) }, onCursorHome = onCursorHome, onCursorEnd = onCursorEnd, onSelectAll = onSelectAll, onCopy = onCopy, onCut = onCut, onPaste = onPaste, onUndo = onUndo, onRedo = onRedo, onClose = { currentPanel = KeyboardPanel.KEYBOARD })
        }
    }
}

private fun handleKeyPress(keyData: KeyData, state: KeyboardState, hasSymbolRows2: Boolean, onTextInput: (String) -> Unit, onBackspace: () -> Unit, onEnter: () -> Unit) {
    when (val action = keyData.action) {
        is KeyAction.Text -> { val text = if (state.shouldUpperCase && state.currentLayer == KeyboardLayer.LETTERS) action.char.uppercase() else action.char; onTextInput(text); state.onTextCommitted() }
        KeyAction.Space -> { onTextInput(" "); state.onTextCommitted() }
        KeyAction.Backspace -> onBackspace()
        KeyAction.Enter -> onEnter()
        KeyAction.Shift -> state.handleShiftPress(hasSymbolRows2)
        KeyAction.SwitchToSymbols -> state.switchToSymbols()
        KeyAction.SwitchToLetters -> state.switchToLetters()
    }
}
