package handboard.app.layout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class LayoutSwitcher(initialLayout: String = "QWERTY") {

    private val allNames = LayoutRegistry.getAllNames()

    var currentLayoutName by mutableStateOf(initialLayout)
        private set

    val currentLayout: KeyboardLayout
        get() = LayoutRegistry.get(currentLayoutName)

    fun setLayout(name: String) {
        if (name in allNames) {
            currentLayoutName = name
        }
    }

    fun nextLayout() {
        val index = allNames.indexOf(currentLayoutName)
        val next = (index + 1) % allNames.size
        currentLayoutName = allNames[next]
    }
}
