package handboard.app.prediction

import android.content.Context
import java.io.File

class PersonalDictionary(context: Context) {
    private val wordsFile = File(context.filesDir, "personal_words.txt")
    private val words = mutableMapOf<String, Int>()

    init {
        try {
            wordsFile.takeIf { it.exists() }?.forEachLine { line ->
                val parts = line.split('\t')
                if (parts.size >= 2) words[parts[0]] = parts[1].toIntOrNull() ?: 1
            }
        } catch (_: Exception) {}
    }

    fun learnWord(word: String) {
        if (word.length < 2) return
        val w = word.lowercase()
        val freq = (words[w] ?: 0) + 5
        words[w] = freq
        try { wordsFile.appendText("$w\t$freq\n") } catch (_: Exception) {}
    }

    fun applyToTrie(trie: Trie) {
        words.forEach { (word, freq) -> trie.updateFrequency(word, freq) }
    }

    fun getWords(): Map<String, Int> = words.toMap()

    fun clear() {
        words.clear()
        wordsFile.delete()
    }
}
