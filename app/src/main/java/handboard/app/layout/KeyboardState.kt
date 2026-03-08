package handboard.app.layout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class KeyboardLayer {
    LETTERS, SYMBOLS
}

class KeyboardState {
    var isShifted by mutableStateOf(false)
        private set
    var isCapsLock by mutableStateOf(false)
        private set
    var currentLayer by mutableStateOf(KeyboardLayer.LETTERS)
        private set

    val shouldUpperCase: Boolean get() = isShifted || isCapsLock

    fun toggleShift() {
        when {
            isCapsLock -> {
                isCapsLock = false
                isShifted = false
            }
            isShifted -> {
                isCapsLock = true
            }
            else -> {
                isShifted = true
            }
        }
    }

    fun onTextCommitted() {
        if (isShifted && !isCapsLock) {
            isShifted = false
        }
    }

    fun switchToSymbols() { currentLayer = KeyboardLayer.SYMBOLS }
    fun switchToLetters() { currentLayer = KeyboardLayer.LETTERS }
}
