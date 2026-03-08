package handboard.app.layout

object QwertyLayout {

    private fun text(char: String, width: Float = 1f) = KeyData(
        label = char,
        action = KeyAction.Text(char),
        widthWeight = width
    )

    private fun action(label: String, action: KeyAction, width: Float = 1.5f) = KeyData(
        label = label,
        action = action,
        widthWeight = width,
        style = KeyStyle.ACTION
    )

    private fun special(label: String, action: KeyAction, width: Float = 1.5f) = KeyData(
        label = label,
        action = action,
        widthWeight = width,
        style = KeyStyle.SPECIAL
    )

    val layout = KeyboardLayout(
        name = "QWERTY",
        letterRows = listOf(
            // Row 1: q w e r t y u i o p
            "qwertyuiop".map { text(it.toString()) },
            // Row 2: a s d f g h j k l
            "asdfghjkl".map { text(it.toString()) },
            // Row 3: shift z x c v b n m backspace
            listOf(action("⇧", KeyAction.Shift)) +
                "zxcvbnm".map { text(it.toString()) } +
                listOf(action("⌫", KeyAction.Backspace)),
            // Row 4: 123 , space . enter
            listOf(
                special("123", KeyAction.SwitchToSymbols),
                text(","),
                special("space", KeyAction.Space, 4f),
                text("."),
                action("↵", KeyAction.Enter)
            )
        ),
        symbolRows = listOf(
            // Row 1: 1 2 3 4 5 6 7 8 9 0
            "1234567890".map { text(it.toString()) },
            // Row 2: symbols
            listOf("@", "#", "\$", "%", "&", "-", "+", "(", ")", "=")
                .map { text(it) },
            // Row 3: shift symbols backspace
            listOf(action("⇧", KeyAction.Shift)) +
                listOf("!", "\"", "'", ":", ";", "/", "?").map { text(it) } +
                listOf(action("⌫", KeyAction.Backspace)),
            // Row 4: ABC , space . enter
            listOf(
                special("ABC", KeyAction.SwitchToLetters),
                text(","),
                special("space", KeyAction.Space, 4f),
                text("."),
                action("↵", KeyAction.Enter)
            )
        )
    )
}
