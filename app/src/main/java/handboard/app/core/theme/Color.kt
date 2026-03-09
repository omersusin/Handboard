package handboard.app.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Material You purple (Android <12 fallback)
val Purple10 = Color(0xFF21005D)
val Purple20 = Color(0xFF381E72)
val Purple30 = Color(0xFF4F378B)
val Purple40 = Color(0xFF6750A4)
val Purple80 = Color(0xFFD0BCFF)
val Purple90 = Color(0xFFEADDFF)
val Purple99 = Color(0xFFFFFBFE)

val PurpleGrey30 = Color(0xFF332D41)
val PurpleGrey50 = Color(0xFF605D66)
val PurpleGrey80 = Color(0xFFC9C5D0)
val PurpleGrey90 = Color(0xFFE6E0EC)

val Error30 = Color(0xFF93000A)
val Error40 = Color(0xFFBA1A1A)
val Error80 = Color(0xFFFFB4AB)
val Error90 = Color(0xFFFFDAD6)

data class KeyboardColors(
    val background: Color,
    val keyBackground: Color,
    val actionKeyBackground: Color,
    val shiftActive: Color,
    val keyText: Color,
    val keyTextDim: Color,
    val numberRow: Color,
    val suggestionBg: Color,
    val suggestionActiveBg: Color,
    val panelItemBg: Color,
    val divider: Color
)

val DarkKeyboardColors = KeyboardColors(
    background = Color(0xFF1B1B1F),
    keyBackground = Color(0xFF2D2D32),
    actionKeyBackground = Color(0xFF3C3C42),
    shiftActive = Color(0xFF6750A4),
    keyText = Color(0xFFFFFFFF),
    keyTextDim = Color(0xFF9E9EA6),
    numberRow = Color(0xFF232328),
    suggestionBg = Color(0xFF2D2D32),
    suggestionActiveBg = Color(0xFF3C3C42),
    panelItemBg = Color(0xFF2D2D32),
    divider = Color(0xFF3C3C42)
)

val LightKeyboardColors = KeyboardColors(
    background = Color(0xFFE8E8EE),
    keyBackground = Color(0xFFFFFFFF),
    actionKeyBackground = Color(0xFFD4D4DC),
    shiftActive = Color(0xFF6750A4),
    keyText = Color(0xFF1B1B1F),
    keyTextDim = Color(0xFF6B6B75),
    numberRow = Color(0xFFDDDDE4),
    suggestionBg = Color(0xFFFFFFFF),
    suggestionActiveBg = Color(0xFFD4D4DC),
    panelItemBg = Color(0xFFFFFFFF),
    divider = Color(0xFFD4D4DC)
)

// Legacy aliases — will be replaced by themed access
var KeyboardBackground = DarkKeyboardColors.background; private set
var KeyBackground = DarkKeyboardColors.keyBackground; private set
var ActionKeyBackground = DarkKeyboardColors.actionKeyBackground; private set
var ShiftActiveBackground = DarkKeyboardColors.shiftActive; private set
var KeyText = DarkKeyboardColors.keyText; private set
var KeyTextDim = DarkKeyboardColors.keyTextDim; private set
var NumberRowBackground = DarkKeyboardColors.numberRow; private set

fun applyKeyboardColors(isDark: Boolean) {
    val c = if (isDark) DarkKeyboardColors else LightKeyboardColors
    KeyboardBackground = c.background
    KeyBackground = c.keyBackground
    ActionKeyBackground = c.actionKeyBackground
    ShiftActiveBackground = c.shiftActive
    KeyText = c.keyText
    KeyTextDim = c.keyTextDim
    NumberRowBackground = c.numberRow
}
