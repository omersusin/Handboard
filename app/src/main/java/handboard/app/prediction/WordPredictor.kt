package handboard.app.prediction

import android.content.Context
import android.util.Log

class WordPredictor {
    private val trie = Trie()
    private val bigramMap = HashMap<String, HashMap<String, Int>>()
    private var lastWord = ""
    private var isLoaded = false
    private var personalDict: PersonalDictionary? = null
    private var dictManager: DictionaryManager? = null

    private var lastPrefix = ""
    private var lastResults = emptyList<String>()

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
        if (newTrie.size() == 0) loadFallback(newTrie)
        trie.root.children.clear()
        trie.root.children.putAll(newTrie.root.children)
        isLoaded = true
        lastPrefix = ""
        lastResults = emptyList()
    }

    private fun loadFallback(targetTrie: Trie) {
        val words = listOf("the" to 999,"be" to 998,"to" to 997,"and" to 995, "you" to 982,"hello" to 899,"thanks" to 898,"yes" to 894)
        words.forEach { (w, f) -> targetTrie.insert(w, f) }
    }

    fun predict(textBeforeCursor: String, maxSuggestions: Int = 3): List<String> {
        if (!isLoaded || trie.size() == 0) return emptyList()
        val prefix = extractCurrentWord(textBeforeCursor)

        if (prefix.isEmpty() || prefix.length > 30) return predictNextWord(maxSuggestions)
        if (prefix == lastPrefix && lastResults.isNotEmpty()) return lastResults.take(maxSuggestions)

        val results = trie.wordsWithPrefix(prefix, maxSuggestions + 3).map { it.first }.filter { it != prefix }
        lastPrefix = prefix
        lastResults = results

        if (trie.search(prefix) && results.isEmpty()) return predictNextWord(maxSuggestions, prefix)
        return results.take(maxSuggestions)
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
        lastPrefix = ""
    }

    private fun extractCurrentWord(text: String?): String {
        if (text.isNullOrEmpty()) return ""
        var endIndex = text.length
        if (!text[endIndex - 1].isLetter() && text[endIndex - 1] != '\'' && text[endIndex - 1] != '-') return ""
        var startIndex = endIndex - 1
        while (startIndex > 0) {
            val ch = text[startIndex - 1]
            if (ch.isLetter() || ch == '\'' || ch == '-') startIndex--
            else break
        }
        val word = text.substring(startIndex, endIndex)
        return if (word.length > 30) "" else word.lowercase()
    }

    fun getCurrentWord(textBeforeCursor: String): String = extractCurrentWord(textBeforeCursor)
    fun getDictionarySize(): Int = trie.size()
    fun getPersonalWords(): Map<String, Int> = personalDict?.getWords() ?: emptyMap()
    fun clearPersonalDictionary() { personalDict?.clear(); lastPrefix = "" }
}
