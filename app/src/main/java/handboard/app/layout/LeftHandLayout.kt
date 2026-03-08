package handboard.app.layout

object LeftHandLayout {

    private fun text(char: String, width: Float = 1f) = KeyData(
        label = char,
        action = KeyAction.Text(char),
        widthWeight = width
    )

    private fun action(label: String, action: KeyAction, width: Float = 1.3f) = KeyData(
        label = label,
        action = action,
        widthWeight = width,
        style = KeyStyle.ACTION
    )

    private fun special(label: String, action: KeyAction, width: Float = 1.3f) = KeyData(
        label = label,
        action = action,
        widthWeight = width,
        style = KeyStyle.SPECIAL
    )

    // Left Hand: 5 columns, QWERTY order preserved
    // Backspace & Enter on left side (left thumb reach)
    val layout = KeyboardLayout(
        name = "Left Hand",
        letterRows = listOf(
            listOf(text("q"), text("w"), text("e"), text("r"), text("t")),
            listOf(text("y"), text("u"), text("i"), text("o"), text("p")),
            listOf(text("a"), text("s"), text("d"), text("f"), text("g")),
            listOf(action("⌫", KeyAction.Backspace), text("h"), text("j"), text("k"), text("l")),
            listOf(text("z"), text("x"), text("c"), text("v"), action("⇧", KeyAction.Shift)),
            listOf(text(","), text("b"), text("n"), text("m"), special("123", KeyAction.SwitchToSymbols)),
            listOf(
                action("↵", KeyAction.Enter),
                text("."),
                special("space", KeyAction.Space, 3f)
            )
        ),
        symbolRows = listOf(
            listOf(text("1"), text("2"), text("3"), text("4"), text("5")),
            listOf(text("6"), text("7"), text("8"), text("9"), text("0")),
            listOf(text("@"), text("#"), text("\$"), text("%"), text("&")),
            listOf(text("("), text(")"), text("="), text("+"), text("-")),
            listOf(action("⌫", KeyAction.Backspace), text(":"), text("/"), text("?"), text("!")),
            listOf(text(","), text(";"), text("'"), text("\""), special("ABC", KeyAction.SwitchToLetters)),
            listOf(
                action("↵", KeyAction.Enter),
                text("."),
                special("space", KeyAction.Space, 3f)
            )
        )
    )
}
