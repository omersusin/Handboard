package handboard.app.prediction

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class WordPredictor {

    private val trie = Trie()
    private val bigramMap = HashMap<String, HashMap<String, Int>>()
    private var lastWord = ""
    private var isLoaded = false
    private var learnedFile: File? = null
    private var bigramFile: File? = null

    fun loadDictionary(context: Context) {
        if (isLoaded) return

        // Load built-in dictionary
        try {
            context.assets.open("en_us.txt").use { stream ->
                BufferedReader(InputStreamReader(stream)).forEachLine { line ->
                    val parts = line.split('\t')
                    if (parts.size >= 2) {
                        val word = parts[0].trim().lowercase()
                        val freq = parts[1].trim().toIntOrNull() ?: 1
                        if (word.isNotEmpty()) trie.insert(word, freq)
                    }
                }
            }
        } catch (_: Exception) { loadFallback() }

        // Load learned words
        learnedFile = File(context.filesDir, "learned_words.txt")
        bigramFile = File(context.filesDir, "learned_bigrams.txt")
        loadLearnedWords()
        loadLearnedBigrams()

        isLoaded = true
    }

    private fun loadFallback() {
        val words = listOf(
            "the" to 999,"be" to 998,"to" to 997,"of" to 996,"and" to 995,
            "a" to 994,"in" to 993,"that" to 992,"have" to 991,"i" to 990,
            "it" to 989,"for" to 988,"not" to 987,"on" to 986,"with" to 985,
            "he" to 984,"you" to 982,"do" to 981,"this" to 979,"but" to 978,
            "from" to 975,"they" to 974,"we" to 973,"she" to 970,"will" to 967,
            "my" to 966,"would" to 963,"there" to 962,"what" to 960,"about" to 955,
            "get" to 953,"make" to 948,"can" to 947,"like" to 946,"time" to 945,
            "just" to 943,"know" to 941,"take" to 940,"people" to 939,"good" to 935,
            "hello" to 899,"thanks" to 898,"please" to 896,"sorry" to 895,
            "yes" to 894,"okay" to 893,"need" to 890,"help" to 889
        )
        words.forEach { (w, f) -> trie.insert(w, f) }
    }

    private fun loadLearnedWords() {
        try {
            learnedFile?.takeIf { it.exists() }?.forEachLine { line ->
                val parts = line.split('\t')
                if (parts.size >= 2) {
                    trie.updateFrequency(parts[0], parts[1].toIntOrNull() ?: 5)
                }
            }
        } catch (_: Exception) { }
    }

    private fun loadLearnedBigrams() {
        try {
            bigramFile?.takeIf { it.exists() }?.forEachLine { line ->
                val parts = line.split('\t')
                if (parts.size >= 3) {
                    bigramMap.getOrPut(parts[0]) { HashMap() }[parts[1]] = parts[2].toIntOrNull() ?: 1
                }
            }
        } catch (_: Exception) { }
    }

    private fun saveLearnedWord(word: String, freq: Int) {
        try { learnedFile?.appendText("$word\t$freq\n") } catch (_: Exception) { }
    }

    private fun saveBigram(prev: String, curr: String, count: Int) {
        try { bigramFile?.appendText("$prev\t$curr\t$count\n") } catch (_: Exception) { }
    }

    fun predict(input: String, maxSuggestions: Int = 3): List<String> {
        if (input.isBlank()) return predictNextWord(maxSuggestions)
        val prefix = input.lowercase().trim()
        if (prefix.isEmpty()) return emptyList()
        return trie.wordsWithPrefix(prefix, maxSuggestions + 2)
            .filter { it.first != prefix }
            .take(maxSuggestions)
            .map { it.first }
    }

    private fun predictNextWord(limit: Int): List<String> {
        if (lastWord.isEmpty()) return emptyList()
        return bigramMap[lastWord.lowercase()]?.entries
            ?.sortedByDescending { it.value }
            ?.take(limit)
            ?.map { it.key } ?: emptyList()
    }

    fun onWordCommitted(word: String) {
        val lower = word.lowercase().trim()
        if (lower.length < 2) return
        trie.updateFrequency(lower, 5)
        saveLearnedWord(lower, 5)
        if (lastWord.isNotEmpty()) {
            val count = bigramMap.getOrPut(lastWord) { HashMap() }
                .merge(lower, 1) { old, _ -> old + 1 } ?: 1
            saveBigram(lastWord, lower, count)
        }
        lastWord = lower
    }

    fun getCurrentWord(textBeforeCursor: String): String {
        if (textBeforeCursor.isBlank() || textBeforeCursor.endsWith(" ")) return ""
        val trimmed = textBeforeCursor.trimEnd()
        val lastSpace = trimmed.lastIndexOf(' ')
        return if (lastSpace >= 0) trimmed.substring(lastSpace + 1) else trimmed
    }
}
