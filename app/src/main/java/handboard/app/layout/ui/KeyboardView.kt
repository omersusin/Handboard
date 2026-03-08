package handboard.app.layout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import handboard.app.core.theme.KeyboardBackground
import handboard.app.layout.KeyAction
import handboard.app.layout.KeyData
import handboard.app.layout.KeyboardLayer
import handboard.app.layout.KeyboardLayout
import handboard.app.layout.KeyboardState

@Composable
fun KeyboardView(
    layout: KeyboardLayout,
    onTextInput: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit
) {
    val state = remember { KeyboardState() }

    val currentRows = when (state.currentLayer) {
        KeyboardLayer.LETTERS -> layout.letterRows
        KeyboardLayer.SYMBOLS -> layout.symbolRows
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(KeyboardBackground)
            .padding(horizontal = 3.dp, vertical = 6.dp)
    ) {
        currentRows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp)
            ) {
                row.forEach { keyData ->
                    KeyView(
                        keyData = keyData,
                        isShifted = state.shouldUpperCase,
                        onClick = {
                            handleKeyPress(
                                keyData = keyData,
                                state = state,
                                onTextInput = onTextInput,
                                onBackspace = onBackspace,
                                onEnter = onEnter
                            )
                        }
                    )
                }
            }
        }
    }
}

private fun handleKeyPress(
    keyData: KeyData,
    state: KeyboardState,
    onTextInput: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit
) {
    when (val action = keyData.action) {
        is KeyAction.Text -> {
            val text = if (state.shouldUpperCase) {
                action.char.uppercase()
            } else {
                action.char
            }
            onTextInput(text)
            state.onTextCommitted()
        }
        KeyAction.Space -> {
            onTextInput(" ")
            state.onTextCommitted()
        }
        KeyAction.Backspace -> onBackspace()
        KeyAction.Enter -> onEnter()
        KeyAction.Shift -> state.toggleShift()
        KeyAction.SwitchToSymbols -> state.switchToSymbols()
        KeyAction.SwitchToLetters -> state.switchToLetters()
    }
}
