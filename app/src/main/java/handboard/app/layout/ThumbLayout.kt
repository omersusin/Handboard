package handboard.app.layout

object ThumbLayout {

    private fun text(char: String, width: Float = 1f) = KeyData(
        label = char,
        action = KeyAction.Text(char),
        widthWeight = width
    )

    private fun action(label: String, action: KeyAction, width: Float = 1.4f) = KeyData(
        label = label,
        action = action,
        widthWeight = width,
        style = KeyStyle.ACTION
    )

    private fun special(label: String, action: KeyAction, width: Float = 1.4f) = KeyData(
        label = label,
        action = action,
        widthWeight = width,
        style = KeyStyle.SPECIAL
    )

    // Thumb layout: 7 columns, wider keys, more rows
    // Designed so all keys are reachable with one thumb
    val layout = KeyboardLayout(
        name = "Thumb",
        letterRows = listOf(
            listOf(text("q"), text("w"), text("e"), text("r"), text("t")),
            listOf(text("y"), text("u"), text("i"), text("o"), text("p")),
            listOf(text("a"), text("s"), text("d"), text("f"), text("g")),
            listOf(text("h"), text("j"), text("k"), text("l"), text("z")),
            listOf(action("⇧", KeyAction.Shift), text("x"), text("c"), text("v"), action("⌫", KeyAction.Backspace)),
            listOf(special("123", KeyAction.SwitchToSymbols), text("b"), text("n"), text("m"), text(",")),
            listOf(
                action("↵", KeyAction.Enter),
                special("space", KeyAction.Space, 3f),
                text(".")
            )
        ),
        symbolRows = listOf(
            listOf(text("1"), text("2"), text("3"), text("4"), text("5")),
            listOf(text("6"), text("7"), text("8"), text("9"), text("0")),
            listOf(text("@"), text("#"), text("\$"), text("%"), text("&")),
            listOf(text("-"), text("+"), text("="), text("("), text(")")),
            listOf(action("⇧", KeyAction.Shift), text("!"), text("?"), text("/"), action("⌫", KeyAction.Backspace)),
            listOf(special("ABC", KeyAction.SwitchToLetters), text("\""), text("'"), text(":"), text(";")),
            listOf(
                action("↵", KeyAction.Enter),
                special("space", KeyAction.Space, 3f),
                text(".")
            )
        )
    )
}
