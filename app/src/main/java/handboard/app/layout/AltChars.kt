package handboard.app.layout

object AltChars {

    val map = mapOf(
        "a" to listOf("à","á","â","ã","ä","å","æ","ā"),
        "b" to listOf("ß"),
        "c" to listOf("ç","ć","č"),
        "d" to listOf("ð","ď"),
        "e" to listOf("è","é","ê","ë","ē","ę","ě"),
        "f" to listOf("ƒ"),
        "g" to listOf("ğ","ģ"),
        "h" to listOf("ħ"),
        "i" to listOf("ì","í","î","ï","ı","ī"),
        "j" to listOf("ĵ"),
        "k" to listOf("ķ"),
        "l" to listOf("ł","ļ","ľ"),
        "m" to listOf("µ"),
        "n" to listOf("ñ","ń","ň","ŋ"),
        "o" to listOf("ò","ó","ô","õ","ö","ø","ō","œ"),
        "p" to listOf("þ"),
        "r" to listOf("ř","ŕ"),
        "s" to listOf("ş","š","ś","ș"),
        "t" to listOf("ţ","ť","ț"),
        "u" to listOf("ù","ú","û","ü","ū","ů"),
        "w" to listOf("ŵ"),
        "y" to listOf("ý","ÿ","ŷ"),
        "z" to listOf("ž","ź","ż"),
        "0" to listOf("°","∅"),
        "1" to listOf("¹","½","⅓","¼"),
        "2" to listOf("²","⅔"),
        "3" to listOf("³","¾"),
        "4" to listOf("⁴"),
        "5" to listOf("⅕"),
        "." to listOf("…","·","•"),
        "," to listOf("‚","„"),
        "!" to listOf("¡","‼"),
        "?" to listOf("¿","⁇"),
        "-" to listOf("–","—","~"),
        "'" to listOf("'","'","‹","›"),
        "\"" to listOf(""",""","«","»"),
        "/" to listOf("\\","|"),
        "@" to listOf("#","№"),
        "$" to listOf("€","£","¥","₹","₺","¢"),
        "&" to listOf("§"),
        "%" to listOf("‰"),
        "+" to listOf("±"),
        "=" to listOf("≠","≈","≤","≥")
    )

    fun getAlts(key: String): List<String> {
        return map[key.lowercase()] ?: emptyList()
    }
}
