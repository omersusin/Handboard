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

    // Left hand: mirror of right hand layout
    val layout = KeyboardLayout(
        name = "Left Hand",
        letterRows = listOf(
            listOf(text("y"), text("t"), text("r"), text("e"), text("w"), text("q")),
            listOf(text("s"), text("a"), text("p"), text("o"), text("i"), text("u")),
            listOf(text("k"), text("j"), text("h"), text("g"), text("f"), text("d")),
            listOf(action("⌫", KeyAction.Backspace), text("c"), text("x"), text("z"), text("l"), action("⇧", KeyAction.Shift)),
            listOf(text(","), text("m"), text("n"), text("b"), text("v"), special("123", KeyAction.SwitchToSymbols)),
            listOf(
                action("↵", KeyAction.Enter, 1.5f),
                text("."),
                special("space", KeyAction.Space, 3.5f)
            )
        ),
        symbolRows = listOf(
            listOf(text("6"), text("5"), text("4"), text("3"), text("2"), text("1")),
            listOf(text("#"), text("@"), text("0"), text("9"), text("8"), text("7")),
            listOf(text("="), text("+"), text("-"), text("&"), text("%"), text("\$")),
            listOf(action("⌫", KeyAction.Backspace), text(")"), text("("), text("?"), text("!"), action("⇧", KeyAction.Shift)),
            listOf(text("/"), text(";"), text(":"), text("'"), text("\""), special("ABC", KeyAction.SwitchToLetters)),
            listOf(
                action("↵", KeyAction.Enter, 1.5f),
                text("."),
                special("space", KeyAction.Space, 3.5f)
            )
        )
    )
}
