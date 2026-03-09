package handboard.app.prediction

import android.content.Context
import java.io.File

class PersonalDictionary(context: Context) {
    private val wordsFile = File(context.filesDir, "personal_words.txt")
    private val bigramsFile = File(context.filesDir, "personal_bigrams.txt")
    private val words = mutableMapOf<String, Int>()
    private val bigrams = HashMap<String, HashMap<String, Int>>()

    init {
        try {
            wordsFile.takeIf { it.exists() }?.forEachLine { line ->
                val parts = line.split('\t')
                if (parts.size >= 2) words[parts[0]] = parts[1].toIntOrNull() ?: 1
            }
        } catch (_: Exception) {}

        try {
            bigramsFile.takeIf { it.exists() }?.forEachLine { line ->
                val parts = line.split('\t')
                if (parts.size >= 3) {
                    bigrams.getOrPut(parts[0]) { HashMap() }[parts[1]] = parts[2].toIntOrNull() ?: 1
                }
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

    fun learnBigram(prev: String, current: String) {
        if (prev.length < 2 || current.length < 2) return
        val p = prev.lowercase()
        val c = current.lowercase()
        val count = (bigrams.getOrPut(p) { HashMap() }[c] ?: 0) + 1
        bigrams[p]!![c] = count
        try { bigramsFile.appendText("$p\t$c\t$count\n") } catch (_: Exception) {}
    }

    fun applyToTrie(trie: Trie) {
        words.forEach { (word, freq) -> trie.updateFrequency(word, freq) }
    }

    fun applyBigrams(bigramMap: HashMap<String, HashMap<String, Int>>) {
        bigrams.forEach { (prev, nexts) ->
            val target = bigramMap.getOrPut(prev) { HashMap() }
            nexts.forEach { (next, count) ->
                target[next] = (target[next] ?: 0) + count
            }
        }
    }

    fun getWords(): Map<String, Int> = words.toMap()

    fun clear() {
        words.clear()
        bigrams.clear()
        wordsFile.delete()
        bigramsFile.delete()
    }
}
