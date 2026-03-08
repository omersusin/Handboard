package handboard.app.prediction

class WordPredictor {

    // Basic English frequency dictionary — will be replaced with AOSP dict later
    private val dictionary = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "I",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see",
        "other", "than", "then", "now", "look", "only", "come", "its", "over",
        "think", "also", "back", "after", "use", "two", "how", "our", "work",
        "first", "well", "way", "even", "new", "want", "because", "any", "these",
        "give", "day", "most", "us", "great", "between", "need", "large", "must",
        "big", "high", "such", "follow", "act", "why", "ask", "men", "change",
        "went", "light", "kind", "off", "always", "house", "world", "area",
        "money", "story", "fact", "month", "lot", "right", "study", "book",
        "eye", "job", "word", "though", "business", "issue", "side", "been",
        "long", "away", "small", "play", "run", "keep", "turn", "here", "last",
        "city", "place", "live", "where", "before", "should", "much", "home",
        "life", "old", "still", "try", "point", "form", "child", "few", "end",
        "open", "head", "school", "start", "might", "hand", "part", "move",
        "close", "show", "while", "let", "help", "down", "line", "same",
        "tell", "does", "set", "three", "own", "being", "state", "never",
        "left", "mean", "call", "name", "every", "found", "came", "each",
        "under", "real", "find", "read", "thing", "really", "number", "sure",
        "around", "another", "many", "those", "may", "next", "begin", "both",
        "love", "country", "family", "again", "put", "man", "woman", "group",
        "feel", "plan", "case", "early", "face", "order", "program", "problem",
        "system", "water", "power", "company", "service", "hand", "going",
        "thank", "hello", "okay", "yes", "please", "sorry", "want", "need",
        "today", "tomorrow", "yesterday", "morning", "night", "happy",
        "message", "phone", "email", "send", "call", "text", "write",
        "keyboard", "android", "app", "type", "touch", "press", "button"
    ).distinct()

    // Learned words from user
    private val learnedWords = mutableSetOf<String>()

    // Word frequency tracking
    private val wordFrequency = mutableMapOf<String, Int>()

    fun predict(input: String, maxSuggestions: Int = 3): List<String> {
        if (input.isBlank()) return emptyList()

        val prefix = input.lowercase().trim()

        // Combine dictionary and learned words
        val allWords = dictionary + learnedWords

        // Find matches
        val matches = allWords
            .filter { it.startsWith(prefix) && it != prefix }
            .distinct()
            .sortedByDescending { wordFrequency[it] ?: 0 }
            .take(maxSuggestions)

        return matches
    }

    fun learnWord(word: String) {
        if (word.length >= 2) {
            val lower = word.lowercase()
            learnedWords.add(lower)
            wordFrequency[lower] = (wordFrequency[lower] ?: 0) + 1
        }
    }

    fun getCurrentWordFromText(text: String): String {
        if (text.isBlank()) return ""
        val lastSpace = text.lastIndexOf(' ')
        return if (lastSpace >= 0) {
            text.substring(lastSpace + 1)
        } else {
            text
        }
    }
}
