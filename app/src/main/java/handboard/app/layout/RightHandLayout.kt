package handboard.app.layout

object RightHandLayout {

    private fun t(c: String, w: Float = 1f) = KeyData(c, KeyAction.Text(c), w)
    private fun a(l: String, a: KeyAction, w: Float = 1.3f) = KeyData(l, a, w, KeyStyle.ACTION)
    private fun s(l: String, a: KeyAction, w: Float = 1.3f) = KeyData(l, a, w, KeyStyle.SPECIAL)

    val layout = KeyboardLayout(
        name = "Right Hand",
        letterRows = listOf(
            listOf(t("q"), t("w"), t("e"), t("r"), t("t")),
            listOf(t("y"), t("u"), t("i"), t("o"), t("p")),
            listOf(t("a"), t("s"), t("d"), t("f"), t("g")),
            listOf(t("h"), t("j"), t("k"), t("l"), a("⌫", KeyAction.Backspace)),
            listOf(a("⇧", KeyAction.Shift), t("z"), t("x"), t("c"), t("v")),
            listOf(s("123", KeyAction.SwitchToSymbols), t("b"), t("n"), t("m"), t(",")),
            listOf(s("space", KeyAction.Space, 3f), t("."), a("↵", KeyAction.Enter))
        ),
        symbolRows = listOf(
            listOf(t("1"), t("2"), t("3"), t("4"), t("5")),
            listOf(t("6"), t("7"), t("8"), t("9"), t("0")),
            listOf(t("@"), t("#"), t("\$"), t("%"), t("&")),
            listOf(t("-"), t("+"), t("="), t("("), t(")")),
            listOf(a("=\\<", KeyAction.Shift), t("!"), t("?"), t("/"), a("⌫", KeyAction.Backspace)),
            listOf(s("ABC", KeyAction.SwitchToLetters), t("\""), t("'"), t(":"), t(";")),
            listOf(s("space", KeyAction.Space, 3f), t("."), a("↵", KeyAction.Enter))
        ),
        symbolRows2 = listOf(
            listOf(t("~"), t("`"), t("|"), t("\\"), t("^")),
            listOf(t("{"), t("}"), t("["), t("]"), t("°")),
            listOf(t("©"), t("®"), t("™"), t("€"), t("£")),
            listOf(t("¥"), t("×"), t("÷"), t("±"), t("≠")),
            listOf(a("123", KeyAction.Shift), t("§"), t("¶"), t("«"), a("⌫", KeyAction.Backspace)),
            listOf(s("ABC", KeyAction.SwitchToLetters), t("»"), t("•"), t("¢"), t("≈")),
            listOf(s("space", KeyAction.Space, 3f), t("."), a("↵", KeyAction.Enter))
        )
    )
}
