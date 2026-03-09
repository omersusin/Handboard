package handboard.app.layout

data class KeyboardLayout(
    val name: String,
    val letterRows: List<List<KeyData>>,
    val symbolRows: List<List<KeyData>>,
    val symbolRows2: List<List<KeyData>> = emptyList()
)
