package handboard.app.prediction

class TrieNode {
    val children = HashMap<Char, TrieNode>(8)
    var isWord = false
    var frequency = 0
}

class Trie {
    val root = TrieNode()
    private var wordCount = 0

    fun insert(word: String, freq: Int) {
        var node = root
        for (ch in word.lowercase()) {
            node = node.children.getOrPut(ch) { TrieNode() }
        }
        if (!node.isWord) wordCount++
        node.isWord = true
        node.frequency = maxOf(node.frequency, freq)
    }

    fun search(word: String): Boolean {
        var node = root
        for (ch in word.lowercase()) {
            node = node.children[ch] ?: return false
        }
        return node.isWord
    }

    fun getFrequency(word: String): Int {
        var node = root
        for (ch in word.lowercase()) {
            node = node.children[ch] ?: return 0
        }
        return if (node.isWord) node.frequency else 0
    }

    fun wordsWithPrefix(prefix: String, limit: Int = 10): List<Pair<String, Int>> {
        var node = root
        for (ch in prefix.lowercase()) {
            node = node.children[ch] ?: return emptyList()
        }
        val results = mutableListOf<Pair<String, Int>>()
        collectWords(node, StringBuilder(prefix.lowercase()), results, limit * 3)
        return results.sortedByDescending { it.second }.take(limit)
    }

    private fun collectWords(
        node: TrieNode, current: StringBuilder,
        results: MutableList<Pair<String, Int>>, limit: Int
    ) {
        if (results.size >= limit) return
        if (node.isWord) results.add(current.toString() to node.frequency)
        for ((ch, child) in node.children) {
            current.append(ch)
            collectWords(child, current, results, limit)
            current.deleteCharAt(current.length - 1)
        }
    }

    fun updateFrequency(word: String, addFreq: Int) {
        var node = root
        for (ch in word.lowercase()) {
            node = node.children.getOrPut(ch) { TrieNode() }
        }
        if (!node.isWord) wordCount++
        node.isWord = true
        node.frequency += addFreq
    }

    /** Find words within edit distance 1 (auto-correct) */
    fun fuzzySearch(word: String, maxResults: Int = 5): List<Pair<String, Int>> {
        if (word.length < 2) return emptyList()
        val target = word.lowercase()
        val results = mutableListOf<Pair<String, Int>>()

        // Generate candidates with edit distance 1
        val candidates = mutableSetOf<String>()

        // Deletions
        for (i in target.indices) {
            candidates.add(target.removeRange(i, i + 1))
        }
        // Substitutions
        for (i in target.indices) {
            for (c in 'a'..'z') {
                if (c != target[i]) {
                    candidates.add(target.substring(0, i) + c + target.substring(i + 1))
                }
            }
        }
        // Insertions
        for (i in 0..target.length) {
            for (c in 'a'..'z') {
                candidates.add(target.substring(0, i) + c + target.substring(i))
            }
        }
        // Transpositions
        for (i in 0 until target.length - 1) {
            val arr = target.toCharArray()
            val tmp = arr[i]; arr[i] = arr[i + 1]; arr[i + 1] = tmp
            candidates.add(String(arr))
        }

        // Check which candidates exist in trie
        for (candidate in candidates) {
            if (candidate != target && search(candidate)) {
                results.add(candidate to getFrequency(candidate))
            }
        }

        return results.sortedByDescending { it.second }.take(maxResults)
    }

    fun size(): Int = wordCount
}
