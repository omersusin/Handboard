package handboard.app.search

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchRepository {
    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val cache = LinkedHashMap<String, List<String>>(32, 0.75f, true)
    private var debounceJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun onQueryChanged(query: String) {
        debounceJob?.cancel()
        if (query.isBlank()) {
            _suggestions.value = emptyList()
            _isLoading.value = false
            return
        }
        debounceJob = scope.launch {
            delay(280)
            val key = query.lowercase().trim()
            cache[key]?.let { _suggestions.value = it; return@launch }
            _isLoading.value = true
            val results = GoogleSuggestApi.fetch(query)
            _suggestions.value = results
            if (results.isNotEmpty()) cache[key] = results
            _isLoading.value = false
        }
    }

    fun clear() { _suggestions.value = emptyList() }
    fun destroy() { scope.cancel() }
}
