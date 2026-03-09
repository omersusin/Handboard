package handboard.app.prediction

import android.content.Context

class WordPredictor {
    private val trie = Trie()
    private val bigramMap = HashMap<String, HashMap<String, Int>>()
    private var lastWord = ""
    private var isLoaded = false
    private var personalDict: PersonalDictionary? = null
    private var dictManager: DictionaryManager? = null

    fun loadDictionary(context: Context, dictId: String = "en_us") {
        if (isLoaded) return
        dictManager = DictionaryManager(context)
        personalDict = PersonalDictionary(context)

        val dictList = dictManager?.getAvailable() ?: emptyList()
        val dict = dictList.find { it.id == dictId }
        
        if (dict != null) dictManager?.loadIntoTrie(dict, trie)
        dictManager?.loadBigrams(dictId, bigramMap)

        personalDict?.applyToTrie(trie)
        isLoaded = true
    }

    fun reloadDictionary(context: Context, dictId: String) {
        val dictList = dictManager?.getAvailable() ?: emptyList()
        val dict = dictList.find { it.id == dictId } ?: return
        dictManager?.loadIntoTrie(dict, trie)
        dictManager?.loadBigrams(dictId, bigramMap)
    }

    fun predict(input: String, maxSuggestions: Int = 3, autocorrect: Boolean = true): List<String> {
        if (input.isBlank()) return predictNextWord(maxSuggestions)
        val prefix = input.lowercase().trim()
        if (prefix.isEmpty()) return emptyList()

        val results = mutableListOf<String>()
        val prefixPairs: List<Pair<String, Int>> = trie.wordsWithPrefix(prefix, maxSuggestions + 3)
        val prefixStrings: List<String> = prefixPairs.filter { pair -> pair.first != prefix }.map { pair -> pair.first }
        results.addAll(prefixStrings)

        if (autocorrect && results.size < maxSuggestions && prefix.length >= 3) {
            val fuzzyPairs: List<Pair<String, Int>> = trie.fuzzySearch(prefix, maxSuggestions - results.size)
            val fuzzyStrings: List<String> = fuzzyPairs.map { pair -> pair.first }.filter { word -> word !in results }
            results.addAll(fuzzyStrings)
        }

        if (trie.search(prefix) && results.isEmpty()) return predictNextWord(maxSuggestions, prefix)
        return results.take(maxSuggestions)
    }

    private fun predictNextWord(limit: Int, overrideLastWord: String? = null): List<String> {
        val word = overrideLastWord ?: lastWord
        if (word.isEmpty()) return emptyList()
        val bigrams = bigramMap[word.lowercase()] ?: return emptyList()
        return bigrams.entries.sortedByDescending { it.value }.take(limit).map { it.key }
    }

    fun onWordCommitted(word: String) {
        val lower = word.lowercase().trim()
        if (lower.length < 2) return
        personalDict?.learnWord(lower)
        trie.updateFrequency(lower, 5)
        
        if (lastWord.isNotEmpty()) {
            val count = bigramMap.getOrPut(lastWord) { HashMap() }[lower] ?: 0
            bigramMap[lastWord]!![lower] = count + 1
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
