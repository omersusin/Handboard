package handboard.app.prediction

import android.content.Context
import java.util.LinkedHashSet

class WordPredictor {
    var trie = Trie()
        private set
    private val bigramMap = HashMap<String, HashMap<String, Int>>()
    private var lastWord = ""
    private var personalDict: PersonalDictionary? = null
    private var dictManager: DictionaryManager? = null

    companion object {
        private const val MAX_RESULTS = 10
        private const val MAX_WORD_LENGTH = 30
        private const val ALPHABET = "abcçdefgğhıijklmnoöpqrsştuüvwxyz"
    }

    fun loadDictionaries(context: Context, dictIds: Set<String>) {
        val newTrie = Trie()
        bigramMap.clear()
        dictManager = DictionaryManager(context)
        dictIds.forEach { dictId ->
            val dict = dictManager?.getAvailable()?.find { it.id == dictId }
            if (dict != null) dictManager?.loadIntoTrie(dict, newTrie)
            dictManager?.loadBigrams(dictId, bigramMap)
        }
        personalDict = PersonalDictionary(context)
        personalDict?.applyToTrie(newTrie)
        personalDict?.applyBigrams(bigramMap)
        if (newTrie.size() == 0) {
            val words = listOf("the" to 999,"be" to 998,"to" to 997,"and" to 995, "you" to 982,"hello" to 899,"thanks" to 898,"yes" to 894)
            words.forEach { (w, f) -> newTrie.insert(w, f) }
        }
        trie = newTrie
    }

    // Prefix First, Fast Fuzzy Second
    fun predict(input: String, maxSuggestions: Int = 3, autocorrect: Boolean = true): List<String> {
        if (input.isBlank()) return predictNextWord(maxSuggestions)
        val prefix = input.lowercase().trim()
        if (prefix.isEmpty() || prefix.length > MAX_WORD_LENGTH) return emptyList()

        val results = mutableListOf<String>()
        val prefixResults = trie.wordsWithPrefix(prefix, maxSuggestions + 5).map { it.first }.filter { it != prefix }
        results.addAll(prefixResults)

        if (results.size >= maxSuggestions) return results.take(maxSuggestions)

        if (autocorrect && prefix.length >= 3 && results.size < 5) {
            val candidates = generateVariants(prefix)
            for (variant in candidates) {
                if (trie.search(variant) && variant !in results) {
                    results.add(variant)
                }
                if (results.size >= maxSuggestions) break
            }
        }
        if (trie.search(prefix) && results.isEmpty()) return predictNextWord(maxSuggestions, prefix)
        return results.take(maxSuggestions)
    }

    private fun generateVariants(word: String): Set<String> {
        val variants = LinkedHashSet<String>()
        for (i in word.indices) variants.add(word.removeRange(i, i + 1))
        for (i in word.indices) {
            for (c in ALPHABET) if (c != word[i]) variants.add(word.substring(0, i) + c + word.substring(i + 1))
        }
        for (c in ALPHABET) variants.add(word + c)
        for (i in 0 until word.length - 1) {
            val chars = word.toCharArray()
            val temp = chars[i]; chars[i] = chars[i + 1]; chars[i + 1] = temp
            variants.add(String(chars))
        }
        variants.remove(word)
        return variants
    }

    private fun predictNextWord(limit: Int, overrideLastWord: String? = null): List<String> {
        val word = overrideLastWord ?: lastWord
        if (word.isEmpty()) return emptyList()
        return bigramMap[word.lowercase()]?.entries?.sortedByDescending { it.value }?.take(limit)?.map { it.key } ?: emptyList()
    }

    fun onWordCommitted(word: String) {
        val lower = word.lowercase().trim()
        if (lower.length < 2) return
        personalDict?.learnWord(lower)
        trie.updateFrequency(lower, 5)
        if (lastWord.isNotEmpty()) {
            val count = bigramMap.getOrPut(lastWord) { HashMap() }[lower] ?: 0
            bigramMap[lastWord]!![lower] = count + 1
            personalDict?.learnBigram(lastWord, lower)
        }
        lastWord = lower
    }

    fun getCurrentWord(textBeforeCursor: String): String {
        if (textBeforeCursor.isBlank() || textBeforeCursor.endsWith(" ")) return ""
        val trimmed = textBeforeCursor.trimEnd()
        val lastSpace = trimmed.lastIndexOf(' ')
        return if (lastSpace >= 0) trimmed.substring(lastSpace + 1) else trimmed
    }

    fun getDictionarySize(): Int = trie.size()
    fun getPersonalWords(): Map<String, Int> = personalDict?.getWords() ?: emptyMap()
    fun clearPersonalDictionary() = personalDict?.clear()
}
