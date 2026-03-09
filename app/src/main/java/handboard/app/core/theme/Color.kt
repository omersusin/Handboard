package handboard.app.core.theme

import androidx.compose.ui.graphics.Color

// Material You default purple palette
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
    val numberRow: Color
)

val DarkKeyboardColors = KeyboardColors(
    background = Color(0xFF1B1B1F),
    keyBackground = Color(0xFF2D2D32),
    actionKeyBackground = Color(0xFF3C3C42),
    shiftActive = Color(0xFF6750A4),
    keyText = Color(0xFFFFFFFF),
    keyTextDim = Color(0xFF9E9EA6),
    numberRow = Color(0xFF232328)
)

val AmoledKeyboardColors = KeyboardColors(
    background = Color(0xFF000000),
    keyBackground = Color(0xFF121212),
    actionKeyBackground = Color(0xFF222222),
    shiftActive = Color(0xFF0055FF),
    keyText = Color(0xFFFFFFFF),
    keyTextDim = Color(0xFF888888),
    numberRow = Color(0xFF0A0A0A)
)

val LightKeyboardColors = KeyboardColors(
    background = Color(0xFFE8E8EE),
    keyBackground = Color(0xFFFFFFFF),
    actionKeyBackground = Color(0xFFD4D4DC),
    shiftActive = Color(0xFF6750A4),
    keyText = Color(0xFF1B1B1F),
    keyTextDim = Color(0xFF6B6B75),
    numberRow = Color(0xFFDDDDE4)
)

// Legacy Aliases 
var KeyboardBackground = DarkKeyboardColors.background; private set
var KeyBackground = DarkKeyboardColors.keyBackground; private set
var ActionKeyBackground = DarkKeyboardColors.actionKeyBackground; private set
var ShiftActiveBackground = DarkKeyboardColors.shiftActive; private set
var KeyText = DarkKeyboardColors.keyText; private set
var KeyTextDim = DarkKeyboardColors.keyTextDim; private set
var NumberRowBackground = DarkKeyboardColors.numberRow; private set

fun applyKeyboardTheme(themePref: String, isSystemDark: Boolean, dynamicPrimary: Color?) {
    val base = when (themePref) {
        "amoled" -> AmoledKeyboardColors
        "light" -> LightKeyboardColors
        "dark" -> DarkKeyboardColors
        else -> if (isSystemDark) DarkKeyboardColors else LightKeyboardColors
    }

    KeyboardBackground = base.background
    KeyBackground = base.keyBackground
    ActionKeyBackground = base.actionKeyBackground
    ShiftActiveBackground = dynamicPrimary ?: base.shiftActive 
    KeyText = base.keyText
    KeyTextDim = base.keyTextDim
    NumberRowBackground = base.numberRow
}
