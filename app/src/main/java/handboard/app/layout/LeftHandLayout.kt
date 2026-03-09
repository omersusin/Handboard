package handboard.app.layout

object LeftHandLayout {

    private fun t(c: String, w: Float = 1f) = KeyData(c, KeyAction.Text(c), w)
    private fun a(l: String, a: KeyAction, w: Float = 1.3f) = KeyData(l, a, w, KeyStyle.ACTION)
    private fun s(l: String, a: KeyAction, w: Float = 1.3f) = KeyData(l, a, w, KeyStyle.SPECIAL)

    val layout = KeyboardLayout(
        name = "Left Hand",
        letterRows = listOf(
            listOf(t("q"), t("w"), t("e"), t("r"), t("t")),
            listOf(t("y"), t("u"), t("i"), t("o"), t("p")),
            listOf(t("a"), t("s"), t("d"), t("f"), t("g")),
            listOf(a("⌫", KeyAction.Backspace), t("h"), t("j"), t("k"), t("l")),
            listOf(t("z"), t("x"), t("c"), t("v"), a("⇧", KeyAction.Shift)),
            listOf(t(","), t("b"), t("n"), t("m"), s("123", KeyAction.SwitchToSymbols)),
            listOf(a("↵", KeyAction.Enter), t("."), s("space", KeyAction.Space, 3f))
        ),
        symbolRows = listOf(
            listOf(t("1"), t("2"), t("3"), t("4"), t("5")),
            listOf(t("6"), t("7"), t("8"), t("9"), t("0")),
            listOf(t("@"), t("#"), t("\$"), t("%"), t("&")),
            listOf(t("("), t(")"), t("="), t("+"), t("-")),
            listOf(a("⌫", KeyAction.Backspace), t("/"), t("?"), t("!"), a("=\\<", KeyAction.Shift)),
            listOf(t(";"), t(":"), t("'"), t("\""), s("ABC", KeyAction.SwitchToLetters)),
            listOf(a("↵", KeyAction.Enter), t("."), s("space", KeyAction.Space, 3f))
        ),
        symbolRows2 = listOf(
            listOf(t("~"), t("`"), t("|"), t("\\"), t("^")),
            listOf(t("{"), t("}"), t("["), t("]"), t("°")),
            listOf(t("©"), t("®"), t("™"), t("€"), t("£")),
            listOf(t("±"), t("≠"), t("÷"), t("×"), t("¥")),
            listOf(a("⌫", KeyAction.Backspace), t("«"), t("»"), t("§"), a("123", KeyAction.Shift)),
            listOf(t("•"), t("¶"), t("¢"), t("≈"), s("ABC", KeyAction.SwitchToLetters)),
            listOf(a("↵", KeyAction.Enter), t("."), s("space", KeyAction.Space, 3f))
        )
    )
}
