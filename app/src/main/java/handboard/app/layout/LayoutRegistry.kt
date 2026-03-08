package handboard.app.layout

object LayoutRegistry {

    private val layouts = mutableMapOf<String, KeyboardLayout>()

    fun register(layout: KeyboardLayout) {
        layouts[layout.name] = layout
    }

    fun get(name: String): KeyboardLayout {
        return layouts[name] ?: layouts.values.first()
    }

    fun getAll(): List<KeyboardLayout> {
        return layouts.values.toList()
    }

    fun getAllNames(): List<String> {
        return layouts.keys.toList()
    }

    init {
        register(QwertyLayout.layout)
        register(RightHandLayout.layout)
        register(LeftHandLayout.layout)
        register(ThumbLayout.layout)
    }
}
