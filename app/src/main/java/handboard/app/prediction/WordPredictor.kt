package handboard.app.prediction

import android.content.Context

class WordPredictor {
    var trie = Trie()
        private set
    private val bigramMap = HashMap<String, HashMap<String, Int>>()
    private var lastWord = ""
    private var personalDict: PersonalDictionary? = null
    private var dictManager: DictionaryManager? = null

    fun loadDictionaries(context: Context, dictIds: Set<String>) {
        val newTrie = Trie()
        bigramMap.clear()
        
        dictManager = DictionaryManager(context)
        
        // Loop over selected dictionaries and dump all words into one Trie
        dictIds.forEach { dictId ->
            val dict = dictManager?.getAvailable()?.find { it.id == dictId }
            if (dict != null) dictManager?.loadIntoTrie(dict, newTrie)
            dictManager?.loadBigrams(dictId, bigramMap)
        }

        personalDict = PersonalDictionary(context)
        personalDict?.applyToTrie(newTrie)
        personalDict?.applyBigrams(bigramMap)

        if (newTrie.size() == 0) loadFallback(newTrie)

        // Atomic swap of the engine to prevent crashing during typing
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
        results.addAll(trie.wordsWithPrefix(prefix, maxSuggestions + 3).filter { it.first != prefix }.map { it.first })

        if (autocorrect && results.size < maxSuggestions && prefix.length >= 3) {
            val fuzzy = trie.fuzzySearch(prefix, maxSuggestions - results.size).map { it.first }.filter { it !in results }
            results.addAll(fuzzy)
        }

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
            bigramMap.getOrPut(lastWord) { HashMap() }.merge(lower, 1) { old, _ -> old + 1 }
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
