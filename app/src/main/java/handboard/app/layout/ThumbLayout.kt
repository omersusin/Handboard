package handboard.app.layout

object ThumbLayout {

    private fun t(c: String, w: Float = 1f) = KeyData(c, KeyAction.Text(c), w)
    private fun a(l: String, a: KeyAction, w: Float = 1.4f) = KeyData(l, a, w, KeyStyle.ACTION)
    private fun s(l: String, a: KeyAction, w: Float = 1.4f) = KeyData(l, a, w, KeyStyle.SPECIAL)

    val layout = KeyboardLayout(
        name = "Thumb",
        letterRows = listOf(
            listOf(t("q"), t("w"), t("e"), t("r"), t("t")),
            listOf(t("y"), t("u"), t("i"), t("o"), t("p")),
            listOf(t("a"), t("s"), t("d"), t("f"), t("g")),
            listOf(t("h"), t("j"), t("k"), t("l"), t("z")),
            listOf(a("⇧", KeyAction.Shift), t("x"), t("c"), t("v"), a("⌫", KeyAction.Backspace)),
            listOf(s("123", KeyAction.SwitchToSymbols), t("b"), t("n"), t("m"), t(",")),
            listOf(a("↵", KeyAction.Enter), s("space", KeyAction.Space, 3f), t("."))
        ),
        symbolRows = listOf(
            listOf(t("1"), t("2"), t("3"), t("4"), t("5")),
            listOf(t("6"), t("7"), t("8"), t("9"), t("0")),
            listOf(t("@"), t("#"), t("\$"), t("%"), t("&")),
            listOf(t("-"), t("+"), t("="), t("("), t(")")),
            listOf(a("=\\<", KeyAction.Shift), t("!"), t("?"), t("/"), a("⌫", KeyAction.Backspace)),
            listOf(s("ABC", KeyAction.SwitchToLetters), t("\""), t("'"), t(":"), t(";")),
            listOf(a("↵", KeyAction.Enter), s("space", KeyAction.Space, 3f), t("."))
        ),
        symbolRows2 = listOf(
            listOf(t("~"), t("`"), t("|"), t("\\"), t("^")),
            listOf(t("{"), t("}"), t("["), t("]"), t("°")),
            listOf(t("©"), t("®"), t("™"), t("€"), t("£")),
            listOf(t("¥"), t("×"), t("÷"), t("±"), t("≠")),
            listOf(a("123", KeyAction.Shift), t("§"), t("¶"), t("«"), a("⌫", KeyAction.Backspace)),
            listOf(s("ABC", KeyAction.SwitchToLetters), t("»"), t("•"), t("¢"), t("≈")),
            listOf(a("↵", KeyAction.Enter), s("space", KeyAction.Space, 3f), t("."))
        )
    )
}
