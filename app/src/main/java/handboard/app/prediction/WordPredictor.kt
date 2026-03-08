package handboard.app.prediction

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class WordPredictor {

    private val trie = Trie()
    private val bigramMap = HashMap<String, HashMap<String, Int>>()
    private var lastWord = ""
    private var isLoaded = false

    fun loadDictionary(context: Context) {
        if (isLoaded) return
        try {
            val stream = context.assets.open("en_us.txt")
            BufferedReader(InputStreamReader(stream)).use { reader ->
                reader.forEachLine { line ->
                    val parts = line.split('\t')
                    if (parts.size >= 2) {
                        val word = parts[0].trim().lowercase()
                        val freq = parts[1].trim().toIntOrNull() ?: 1
                        if (word.isNotEmpty()) {
                            trie.insert(word, freq)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            loadFallbackDictionary()
        }
        isLoaded = true
    }

    private fun loadFallbackDictionary() {
        val words = listOf(
            "the" to 999, "be" to 998, "to" to 997, "of" to 996, "and" to 995,
            "a" to 994, "in" to 993, "that" to 992, "have" to 991, "i" to 990,
            "it" to 989, "for" to 988, "not" to 987, "on" to 986, "with" to 985,
            "he" to 984, "as" to 983, "you" to 982, "do" to 981, "at" to 980,
            "this" to 979, "but" to 978, "his" to 977, "by" to 976, "from" to 975,
            "they" to 974, "we" to 973, "say" to 972, "her" to 971, "she" to 970,
            "or" to 969, "an" to 968, "will" to 967, "my" to 966, "one" to 965,
            "all" to 964, "would" to 963, "there" to 962, "their" to 961, "what" to 960,
            "so" to 959, "up" to 958, "out" to 957, "if" to 956, "about" to 955,
            "who" to 954, "get" to 953, "which" to 952, "go" to 951, "me" to 950,
            "when" to 949, "make" to 948, "can" to 947, "like" to 946, "time" to 945,
            "no" to 944, "just" to 943, "him" to 942, "know" to 941, "take" to 940,
            "people" to 939, "into" to 938, "year" to 937, "your" to 936,
            "good" to 935, "some" to 934, "could" to 933, "them" to 932,
            "see" to 931, "other" to 930, "than" to 929, "then" to 928,
            "now" to 927, "look" to 926, "only" to 925, "come" to 924,
            "its" to 923, "over" to 922, "think" to 921, "also" to 920,
            "back" to 919, "after" to 918, "use" to 917, "two" to 916,
            "how" to 915, "our" to 914, "work" to 913, "first" to 912,
            "well" to 911, "way" to 910, "even" to 909, "new" to 908,
            "want" to 907, "because" to 906, "any" to 905, "these" to 904,
            "give" to 903, "day" to 902, "most" to 901, "us" to 900,
            "hello" to 899, "thanks" to 898, "thank" to 897, "please" to 896,
            "sorry" to 895, "yes" to 894, "okay" to 893, "right" to 892,
            "great" to 891, "need" to 890, "help" to 889, "here" to 888,
            "where" to 887, "why" to 886, "what" to 885, "sure" to 884,
            "today" to 883, "tomorrow" to 882, "yesterday" to 881,
            "morning" to 880, "night" to 879, "happy" to 878,
            "love" to 877, "home" to 876, "phone" to 875,
            "message" to 874, "send" to 873, "call" to 872, "text" to 871,
            "email" to 870, "write" to 869, "read" to 868, "play" to 867,
            "open" to 866, "close" to 865, "start" to 864, "stop" to 863,
            "find" to 862, "keep" to 861, "tell" to 860, "feel" to 859,
            "long" to 858, "never" to 857, "always" to 856, "still" to 855,
            "much" to 854, "should" to 853, "before" to 852, "must" to 851,
            "really" to 850, "world" to 849, "very" to 848, "going" to 847,
            "been" to 846, "away" to 845, "small" to 844, "big" to 843,
            "every" to 842, "another" to 841, "many" to 840, "again" to 839,
            "same" to 838, "last" to 837, "next" to 836, "both" to 835,
            "between" to 834, "each" to 833, "place" to 832, "old" to 831,
            "school" to 830, "head" to 829, "hand" to 828, "part" to 827,
            "point" to 826, "number" to 825, "name" to 824, "turn" to 823,
            "move" to 822, "live" to 821, "end" to 820, "show" to 819,
            "city" to 818, "set" to 817, "three" to 816, "left" to 815,
            "high" to 814, "house" to 813, "own" to 812, "man" to 811,
            "thing" to 810, "kind" to 809, "off" to 808, "run" to 807,
            "while" to 806, "line" to 805, "state" to 804, "mean" to 803,
            "try" to 802, "ask" to 801, "change" to 800, "few" to 799
        )
        words.forEach { (word, freq) -> trie.insert(word, freq) }
    }

    fun predict(input: String, maxSuggestions: Int = 3): List<String> {
        if (input.isBlank()) {
            return predictNextWord(maxSuggestions)
        }

        val prefix = input.lowercase().trim()
        if (prefix.isEmpty()) return emptyList()

        val results = trie.wordsWithPrefix(prefix, maxSuggestions + 2)
            .filter { it.first != prefix }
            .take(maxSuggestions)
            .map { it.first }

        return results
    }

    private fun predictNextWord(limit: Int): List<String> {
        if (lastWord.isEmpty()) return emptyList()
        val bigrams = bigramMap[lastWord.lowercase()] ?: return emptyList()
        return bigrams.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key }
    }

    fun learnWord(word: String) {
        if (word.length < 2) return
        val lower = word.lowercase()
        trie.updateFrequency(lower, 5)
    }

    fun learnBigram(prevWord: String, currentWord: String) {
        if (prevWord.length < 2 || currentWord.length < 2) return
        val prev = prevWord.lowercase()
        val curr = currentWord.lowercase()
        bigramMap
            .getOrPut(prev) { HashMap() }
            .merge(curr, 1) { old, _ -> old + 1 }
    }

    fun onWordCommitted(word: String) {
        val lower = word.lowercase().trim()
        if (lower.length >= 2) {
            learnWord(lower)
            if (lastWord.isNotEmpty()) {
                learnBigram(lastWord, lower)
            }
            lastWord = lower
        }
    }

    fun getCurrentWordFromText(text: String): String {
        if (text.isBlank()) return ""
        val trimmed = text.trimEnd()
        if (trimmed.isEmpty()) return ""
        if (text.endsWith(" ")) return ""
        val lastSpace = trimmed.lastIndexOf(' ')
        return if (lastSpace >= 0) trimmed.substring(lastSpace + 1) else trimmed
    }
}
