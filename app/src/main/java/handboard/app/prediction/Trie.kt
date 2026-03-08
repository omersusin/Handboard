package handboard.app.prediction

class TrieNode {
    val children = HashMap<Char, TrieNode>(8)
    var isWord = false
    var frequency = 0
}

class Trie {
    val root = TrieNode()

    fun insert(word: String, freq: Int) {
        var node = root
        for (ch in word.lowercase()) {
            node = node.children.getOrPut(ch) { TrieNode() }
        }
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
        node: TrieNode,
        current: StringBuilder,
        results: MutableList<Pair<String, Int>>,
        limit: Int
    ) {
        if (results.size >= limit) return
        if (node.isWord) {
            results.add(current.toString() to node.frequency)
        }
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
        node.isWord = true
        node.frequency += addFreq
    }
}
