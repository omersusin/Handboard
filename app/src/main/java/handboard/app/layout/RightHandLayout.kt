package handboard.app.layout

object RightHandLayout {

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

    // Right hand: keys grouped toward right side
    // 6 columns, compact, thumb-reachable on right
    val layout = KeyboardLayout(
        name = "Right Hand",
        letterRows = listOf(
            listOf(text("q"), text("w"), text("e"), text("r"), text("t"), text("y")),
            listOf(text("u"), text("i"), text("o"), text("p"), text("a"), text("s")),
            listOf(text("d"), text("f"), text("g"), text("h"), text("j"), text("k")),
            listOf(action("⇧", KeyAction.Shift), text("l"), text("z"), text("x"), text("c"), action("⌫", KeyAction.Backspace)),
            listOf(special("123", KeyAction.SwitchToSymbols), text("v"), text("b"), text("n"), text("m"), text(",")),
            listOf(
                special("space", KeyAction.Space, 3.5f),
                text("."),
                action("↵", KeyAction.Enter, 1.5f)
            )
        ),
        symbolRows = listOf(
            listOf(text("1"), text("2"), text("3"), text("4"), text("5"), text("6")),
            listOf(text("7"), text("8"), text("9"), text("0"), text("@"), text("#")),
            listOf(text("\$"), text("%"), text("&"), text("-"), text("+"), text("=")),
            listOf(action("⇧", KeyAction.Shift), text("!"), text("?"), text("("), text(")"), action("⌫", KeyAction.Backspace)),
            listOf(special("ABC", KeyAction.SwitchToLetters), text("\""), text("'"), text(":"), text(";"), text("/")),
            listOf(
                special("space", KeyAction.Space, 3.5f),
                text("."),
                action("↵", KeyAction.Enter, 1.5f)
            )
        )
    )
}
