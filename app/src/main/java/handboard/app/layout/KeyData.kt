package handboard.app.layout

data class KeyData(
    val label: String,
    val action: KeyAction,
    val widthWeight: Float = 1f,
    val style: KeyStyle = KeyStyle.NORMAL
)

enum class KeyStyle {
    NORMAL,
    ACTION,
    SPECIAL
}
