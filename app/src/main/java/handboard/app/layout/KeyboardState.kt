package handboard.app.layout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class KeyboardLayer {
    LETTERS, SYMBOLS, SYMBOLS2
}

class KeyboardState {
    var isShifted by mutableStateOf(false)
        private set
    var isCapsLock by mutableStateOf(false)
        private set
    var currentLayer by mutableStateOf(KeyboardLayer.LETTERS)
        private set

    val shouldUpperCase: Boolean get() = isShifted || isCapsLock
    val isSymbolMode: Boolean get() = currentLayer == KeyboardLayer.SYMBOLS || currentLayer == KeyboardLayer.SYMBOLS2

    fun toggleShift() {
        when {
            isCapsLock -> { isCapsLock = false; isShifted = false }
            isShifted -> { isCapsLock = true }
            else -> { isShifted = true }
        }
    }

    fun onTextCommitted() {
        if (isShifted && !isCapsLock) isShifted = false
    }

    fun switchToSymbols() { currentLayer = KeyboardLayer.SYMBOLS }
    fun switchToSymbols2() { currentLayer = KeyboardLayer.SYMBOLS2 }
    fun switchToLetters() { currentLayer = KeyboardLayer.LETTERS }

    fun handleShiftPress(hasSymbolRows2: Boolean) {
        when (currentLayer) {
            KeyboardLayer.LETTERS -> toggleShift()
            KeyboardLayer.SYMBOLS -> if (hasSymbolRows2) switchToSymbols2()
            KeyboardLayer.SYMBOLS2 -> switchToSymbols()
        }
    }
}
