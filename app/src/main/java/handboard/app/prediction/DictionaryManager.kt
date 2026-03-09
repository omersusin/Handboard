package handboard.app.prediction

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

data class DictionaryInfo(val id: String, val name: String, val isAsset: Boolean, val file: File? = null)

class DictionaryManager(private val context: Context) {
    
    fun getAvailable(): List<DictionaryInfo> {
        val list = mutableListOf<DictionaryInfo>()
        
        // 1. Assets (Gömülü sözlükler)
        try {
            context.assets.list("")?.filter { it.endsWith(".txt") && !it.contains("bigram") }?.forEach { f ->
                val id = f.removeSuffix(".txt")
                val name = when (id) { "en_us" -> "English (US)"; "tr_tr" -> "Türkçe"; else -> id.uppercase() }
                list.add(DictionaryInfo(id, name, true))
            }
        } catch (_: Exception) {}

        // 2. Kullanıcı Yüklemeleri (Custom dicts)
        val dictDir = File(context.filesDir, "dictionaries")
        if (dictDir.exists()) {
            dictDir.listFiles()?.filter { it.extension == "txt" || it.extension == "dict" }?.forEach { f ->
                list.add(DictionaryInfo("ext_${f.nameWithoutExtension}", "Custom: ${f.nameWithoutExtension}", false, f))
            }
        }
        return list
    }

    fun loadIntoTrie(dictInfo: DictionaryInfo, trie: Trie) {
        try {
            val reader = if (dictInfo.isAsset) {
                BufferedReader(InputStreamReader(context.assets.open("${dictInfo.id}.txt")))
            } else {
                dictInfo.file?.bufferedReader() ?: return
            }

            reader.use { r ->
                r.forEachLine { line ->
                    val parts = line.split('\t')
                    if (parts.size >= 2) {
                        val word = parts[0].trim().lowercase()
                        val freq = parts[1].trim().toIntOrNull() ?: 1
                        if (word.isNotEmpty()) trie.insert(word, freq)
                    }
                }
            }
        } catch (_: Exception) {}
    }

    fun loadBigrams(dictId: String, bigramMap: HashMap<String, HashMap<String, Int>>) {
        if (dictId.startsWith("ext_")) return // Harici sözlüklerde bigram şimdilik yok
        try {
            BufferedReader(InputStreamReader(context.assets.open("${dictId}_bigrams.txt"))).use { r ->
                r.forEachLine { line ->
                    val parts = line.split('\t')
                    if (parts.size >= 3) {
                        bigramMap.getOrPut(parts[0].trim().lowercase()) { HashMap() }[parts[1].trim().lowercase()] = parts[2].trim().toIntOrNull() ?: 1
                    }
                }
            }
        } catch (_: Exception) {}
    }
}
