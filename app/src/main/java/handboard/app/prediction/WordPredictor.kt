package handboard.app.prediction

import android.content.Context

class WordPredictor {
    var trie = Trie()
        private set
    private val bigramMap = HashMap<String, HashMap<String, Int>>()
    private var lastWord = ""
    private var personalDict: PersonalDictionary? = null
    private var dictManager: DictionaryManager? = null

    // Birden fazla dili aynı anda yükler (Multilingual Typing)
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

        trie = newTrie
    }

    private fun loadFallback(targetTrie: Trie) {
        val words = listOf("the" to 999,"be" to 998,"to" to 997,"and" to 995, "you" to 982,"hello" to 899,"thanks" to 898,"yes" to 894)
        words.forEach { (w, f) -> targetTrie.insert(w, f) }
    }

    fun predict(input: String, maxSuggestions: Int = 3, autocorrect: Boolean = true): List<String> {
        if (input.isBlank()) return predictNextWord(maxSuggestions)
        val prefix = input.lowercase().trim()
        if (prefix.isEmpty()) return emptyList()

        val results = mutableListOf<String>()
        val prefixPairs = trie.wordsWithPrefix(prefix, maxSuggestions + 3)
        results.addAll(prefixPairs.filter { it.first != prefix }.map { it.first })

        if (autocorrect && results.size < maxSuggestions && prefix.length >= 3) {
            val fuzzyPairs = trie.fuzzySearch(prefix, maxSuggestions - results.size)
            results.addAll(fuzzyPairs.map { it.first }.filter { it !in results })
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
