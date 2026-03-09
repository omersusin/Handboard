package handboard.app.layout

object QwertyLayout {

    private fun t(c: String, w: Float = 1f) = KeyData(c, KeyAction.Text(c), w)
    private fun a(l: String, a: KeyAction, w: Float = 1.5f) = KeyData(l, a, w, KeyStyle.ACTION)
    private fun s(l: String, a: KeyAction, w: Float = 1.5f) = KeyData(l, a, w, KeyStyle.SPECIAL)

    val layout = KeyboardLayout(
        name = "QWERTY",
        letterRows = listOf(
            "qwertyuiop".map { t(it.toString()) },
            "asdfghjkl".map { t(it.toString()) },
            listOf(a("⇧", KeyAction.Shift)) +
                "zxcvbnm".map { t(it.toString()) } +
                listOf(a("⌫", KeyAction.Backspace)),
            listOf(s("123", KeyAction.SwitchToSymbols), t(","),
                s("space", KeyAction.Space, 4f), t("."), a("↵", KeyAction.Enter))
        ),
        symbolRows = listOf(
            "1234567890".map { t(it.toString()) },
            listOf("@","#","\$","%","&","-","+","(",")","=").map { t(it) },
            listOf(a("=\\<", KeyAction.Shift)) +
                listOf("!","\"","'",":",";","/","?").map { t(it) } +
                listOf(a("⌫", KeyAction.Backspace)),
            listOf(s("ABC", KeyAction.SwitchToLetters), t(","),
                s("space", KeyAction.Space, 4f), t("."), a("↵", KeyAction.Enter))
        ),
        symbolRows2 = listOf(
            listOf("~","`","|","\\","^","{","}","[","]","°").map { t(it) },
            listOf("©","®","™","¶","§","×","÷","±","≠","≈").map { t(it) },
            listOf(a("123", KeyAction.Shift)) +
                listOf("€","£","¥","¢","«","»","•").map { t(it) } +
                listOf(a("⌫", KeyAction.Backspace)),
            listOf(s("ABC", KeyAction.SwitchToLetters), t(","),
                s("space", KeyAction.Space, 4f), t("."), a("↵", KeyAction.Enter))
        )
    )
}
