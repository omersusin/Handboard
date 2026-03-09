package handboard.app.prediction

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

data class DictionaryInfo(val id: String, val name: String, val assetFile: String?)

class DictionaryManager(private val context: Context) {
    private val availableDicts = mutableListOf<DictionaryInfo>()

    init {
        try {
            val assetsList = context.assets.list("") ?: emptyArray()
            assetsList.filter { it.endsWith(".txt") && !it.contains("bigram") }.forEach { file ->
                val id = file.removeSuffix(".txt")
                val name = when (id) {
                    "en_us" -> "English (US)"
                    "tr_tr" -> "Türkçe"
                    else -> id.uppercase()
                }
                availableDicts.add(DictionaryInfo(id, name, file))
            }
        } catch (_: Exception) {}
    }

    fun getAvailable(): List<DictionaryInfo> = availableDicts.toList()

    fun loadIntoTrie(dictInfo: DictionaryInfo, trie: Trie) {
        try {
            if (dictInfo.assetFile == null) return
            BufferedReader(InputStreamReader(context.assets.open(dictInfo.assetFile))).use { r ->
                r.forEachLine { line ->
                    val parts = line.split('\t')
                    if (parts.size >= 2) {
                        val word = parts[0].trim().lowercase()
                        val freq = parts[1].trim().toIntOrNull() ?: 1
                        if (word.isNotEmpty() && word.length <= 30) trie.insert(word, freq)
                    }
                }
            }
        } catch (_: Exception) {}
    }

    fun loadBigrams(dictId: String, bigramMap: HashMap<String, HashMap<String, Int>>) {
        try {
            BufferedReader(InputStreamReader(context.assets.open("${dictId}_bigrams.txt"))).use { r ->
                r.forEachLine { line ->
                    val parts = line.split('\t')
                    if (parts.size >= 3) {
                        val prev = parts[0].trim().lowercase()
                        val next = parts[1].trim().lowercase()
                        val freq = parts[2].trim().toIntOrNull() ?: 1
                        bigramMap.getOrPut(prev) { HashMap() }[next] = freq
                    }
                }
            }
        } catch (_: Exception) {}
    }
}
