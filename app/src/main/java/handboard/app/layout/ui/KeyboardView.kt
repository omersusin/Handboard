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
import handboard.app.search.SearchPanel
import handboard.app.currency.CurrencyPanel
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
    clipboardEnabled: Boolean = false, searchEnabled: Boolean = true, currencyEnabled: Boolean = true,
    kaomojiEnabled: Boolean = true, phrasesEnabled: Boolean = true,
    clipboardHistory: ClipboardHistory? = null,
    suggestionBar: (@Composable () -> Unit)? = null,
    onTextInput: (String) -> Unit, onBackspace: () -> Unit, onEnter: () -> Unit,
    onEmojiInput: (String) -> Unit = onTextInput,
    onCursorMove: (Int) -> Unit = {}, onCursorHome: () -> Unit = {}, onCursorEnd: () -> Unit = {},
    onSelectAll: () -> Unit = {}, onCopy: () -> Unit = {}, onCut: () -> Unit = {},
    onPaste: () -> Unit = {}, onUndo: () -> Unit = {}, onRedo: () -> Unit = {},
    onPasteImage: (ClipboardItem) -> Unit = {},
    onDismissKeyboard: () -> Unit = {} // YENİ
) {
    val state = remember { KeyboardState() }
    val layout = layoutSwitcher.currentLayout
    var currentPanel by remember { mutableStateOf(KeyboardPanel.KEYBOARD) }
    val scope = rememberCoroutineScope()
    var panelQuery by remember { mutableStateOf("") } 

    val hasSymbolRows2 = layout.symbolRows2.isNotEmpty()
    val currentRows = when (state.currentLayer) {
        KeyboardLayer.LETTERS -> layout.letterRows; KeyboardLayer.SYMBOLS -> layout.symbolRows
        KeyboardLayer.SYMBOLS2 -> if (hasSymbolRows2) layout.symbolRows2 else layout.symbolRows
    }
    val numberRow = listOf("1","2","3","4","5","6","7","8","9","0").map { KeyData(it, KeyAction.Text(it), 1f, KeyStyle.NORMAL) }

    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)).background(KeyboardBackground)) {
        
        if (currentPanel == KeyboardPanel.KEYBOARD) suggestionBar?.invoke()

        val isInputPanel = currentPanel == KeyboardPanel.SEARCH || currentPanel == KeyboardPanel.TRANSLATE || currentPanel == KeyboardPanel.CURRENCY

        if (!isInputPanel) {
            LayoutToolbar(
                currentLayoutName = layoutSwitcher.currentLayoutName, currentPanel = currentPanel,
                searchEnabled = searchEnabled, currencyEnabled = currencyEnabled, clipboardEnabled = clipboardEnabled,
                kaomojiEnabled = kaomojiEnabled, phrasesEnabled = phrasesEnabled,
                onSwitchLayout = {
                    layoutSwitcher.nextLayout(); state.switchToLetters(); currentPanel = KeyboardPanel.KEYBOARD
                    scope.launch { preferencesManager.setSelectedLayout(layoutSwitcher.currentLayoutName) }
                },
                onSwitchPanel = { currentPanel = it; panelQuery = "" }
            )
        } else {
            when (currentPanel) {
                KeyboardPanel.SEARCH -> SearchPanel(query = panelQuery, onQueryChange = { panelQuery = it }, onTextCommit = { onTextInput(it); currentPanel = KeyboardPanel.KEYBOARD; panelQuery = "" }, onClose = { currentPanel = KeyboardPanel.KEYBOARD; panelQuery = "" }, onDismissKeyboard = onDismissKeyboard)
                KeyboardPanel.CURRENCY -> CurrencyPanel(query = panelQuery, onQueryChange = { panelQuery = it }, onResultCommit = { onTextInput(it); currentPanel = KeyboardPanel.KEYBOARD; panelQuery = "" }, onClose = { currentPanel = KeyboardPanel.KEYBOARD; panelQuery = "" })
                else -> {}
            }
        }

        if (currentPanel == KeyboardPanel.KEYBOARD || isInputPanel) {
            key(layoutSwitcher.currentLayoutName, state.currentLayer) {
                Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 4.dp).padding(bottom = 6.dp)) {
                    if (numberRowEnabled && state.currentLayer == KeyboardLayer.LETTERS) {
                        Row(modifier = Modifier.fillMaxWidth().background(NumberRowBackground).padding(vertical = 1.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            numberRow.forEach { kd -> KeyView(modifier = Modifier.weight(1f), keyData = kd, isShifted = false, isCapsLock = false, currentLayer = state.currentLayer, heightScale = heightScale * 0.8f, hapticEnabled = hapticEnabled, soundEnabled = soundEnabled, onClick = { if (isInputPanel) panelQuery += kd.label else onTextInput(kd.label) }) }
                        }
                    }

                    currentRows.forEach { row ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 1.dp)) {
                            row.forEach { kd ->
                                KeyView(
                                    modifier = Modifier.weight(kd.widthWeight), keyData = kd, isShifted = state.shouldUpperCase, isCapsLock = state.isCapsLock, currentLayer = state.currentLayer, heightScale = heightScale, hapticEnabled = hapticEnabled, soundEnabled = soundEnabled,
                                    onCursorMove = if (spacebarCursor && kd.action is KeyAction.Space && currentPanel == KeyboardPanel.KEYBOARD) { { dir -> onCursorMove(dir) } } else null,
                                    onAltChar = { if (isInputPanel) panelQuery += it else onTextInput(it) },
                                    onClick = {
                                        if (isInputPanel) {
                                            when (val act = kd.action) {
                                                is KeyAction.Text -> { val chr = if (state.shouldUpperCase && state.currentLayer == KeyboardLayer.LETTERS) act.char.uppercase() else act.char; panelQuery += chr }
                                                is KeyAction.Space -> panelQuery += " "
                                                is KeyAction.Backspace -> if (panelQuery.isNotEmpty()) panelQuery = panelQuery.dropLast(1)
                                                is KeyAction.Shift -> state.handleShiftPress(hasSymbolRows2)
                                                is KeyAction.SwitchToSymbols -> state.switchToSymbols()
                                                is KeyAction.SwitchToLetters -> state.switchToLetters()
                                                else -> {}
                                            }
                                        } else {
                                            when (val action = kd.action) {
                                                is KeyAction.Text -> { val text = if (state.shouldUpperCase && state.currentLayer == KeyboardLayer.LETTERS) action.char.uppercase() else action.char; onTextInput(text); state.onTextCommitted() }
                                                KeyAction.Space -> { onTextInput(" "); state.onTextCommitted() }
                                                KeyAction.Backspace -> onBackspace()
                                                KeyAction.Enter -> onEnter()
                                                KeyAction.Shift -> state.handleShiftPress(hasSymbolRows2)
                                                KeyAction.SwitchToSymbols -> state.switchToSymbols()
                                                KeyAction.SwitchToLetters -> state.switchToLetters()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        } else if (currentPanel == KeyboardPanel.EMOJI) EmojiView(heightScale = heightScale, onEmojiClick = { onEmojiInput(it) }, onBackspace = onBackspace)
        else if (currentPanel == KeyboardPanel.CLIPBOARD) { if (clipboardHistory != null) ClipboardView(clipboardHistory = clipboardHistory, heightScale = heightScale, onPasteText = { onTextInput(it) }, onPasteImage = { onPasteImage(it) }, onClearAll = { clipboardHistory.clearAll() }) }
        else if (currentPanel == KeyboardPanel.KAOMOJI) KaomojiView(heightScale = heightScale, onKaomojiClick = { onTextInput(it) })
        else if (currentPanel == KeyboardPanel.PHRASES) PhrasesPanel(onCommitText = { onTextInput(it); currentPanel = KeyboardPanel.KEYBOARD }, onClose = { currentPanel = KeyboardPanel.KEYBOARD })
        else if (currentPanel == KeyboardPanel.TEXT_EDITING) TextEditingBar(onCursorLeft = { onCursorMove(-1) }, onCursorRight = { onCursorMove(1) }, onCursorHome = onCursorHome, onCursorEnd = onCursorEnd, onSelectAll = onSelectAll, onCopy = onCopy, onCut = onCut, onPaste = onPaste, onUndo = onUndo, onRedo = onRedo, onClose = { currentPanel = KeyboardPanel.KEYBOARD })
    }
}
