package handboard.app.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "handboard_prefs")

class PreferencesManager(private val context: Context) {
    companion object {
        val KEY_HEIGHT = floatPreferencesKey("keyboard_height")
        val KEY_WIDTH = intPreferencesKey("keyboard_width")
        val KEY_ALIGNMENT = intPreferencesKey("keyboard_alignment")
        val KEY_LAYOUT = stringPreferencesKey("selected_layout")
        val KEY_HAPTIC = booleanPreferencesKey("haptic_enabled")
        val KEY_SOUND = booleanPreferencesKey("sound_enabled")
        val KEY_SUGGESTIONS = intPreferencesKey("suggestion_count")
        val KEY_PREDICTIONS = booleanPreferencesKey("predictions_enabled")
        val KEY_AUTOCORRECT = booleanPreferencesKey("autocorrect_enabled")
        val KEY_BOTTOM_PADDING = intPreferencesKey("bottom_padding")
        
        // Panel Toggles
        val KEY_CLIPBOARD_ENABLED = booleanPreferencesKey("clipboard_enabled")
        val KEY_SEARCH_ENABLED = booleanPreferencesKey("search_enabled")
        val KEY_CURRENCY_ENABLED = booleanPreferencesKey("currency_enabled")
        val KEY_KAOMOJI_ENABLED = booleanPreferencesKey("kaomoji_enabled")
        val KEY_PHRASES_ENABLED = booleanPreferencesKey("phrases_enabled")
        val KEY_TRANSLATE_ENABLED = booleanPreferencesKey("translate_enabled")
        val KEY_TEXT_EDITING_ENABLED = booleanPreferencesKey("text_editing_enabled")
        val KEY_EMOJI_ENABLED = booleanPreferencesKey("emoji_enabled")

        val KEY_FOLLOW_SYSTEM_THEME = booleanPreferencesKey("follow_system_theme")
        val KEY_THEME_PREFERENCE = stringPreferencesKey("theme_preference")
        val KEY_NUMBER_ROW = booleanPreferencesKey("number_row_enabled")
        val KEY_AUTO_CAPITALIZE = booleanPreferencesKey("auto_capitalize")
        val KEY_SPACEBAR_CURSOR = booleanPreferencesKey("spacebar_cursor")
        val KEY_HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val KEY_LARGE_KEYS = booleanPreferencesKey("large_keys")
        val KEY_MULTILINGUAL = booleanPreferencesKey("multilingual_enabled")
        val KEY_ACTIVE_DICTS = stringSetPreferencesKey("active_dicts")
        val KEY_DICTIONARY = stringPreferencesKey("dictionary_id")
    }

    val keyboardHeight: Flow<Float> = context.dataStore.data.map { it[KEY_HEIGHT] ?: 1.0f }
    val keyboardWidth: Flow<Int> = context.dataStore.data.map { it[KEY_WIDTH] ?: 100 }
    val keyboardAlignment: Flow<Int> = context.dataStore.data.map { it[KEY_ALIGNMENT] ?: 1 }
    val selectedLayout: Flow<String> = context.dataStore.data.map { it[KEY_LAYOUT] ?: "QWERTY" }
    val hapticEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_HAPTIC] ?: true }
    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_SOUND] ?: false }
    val suggestionCount: Flow<Int> = context.dataStore.data.map { it[KEY_SUGGESTIONS] ?: 3 }
    val predictionsEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_PREDICTIONS] ?: true }
    val autocorrectEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_AUTOCORRECT] ?: true }
    val bottomPadding: Flow<Int> = context.dataStore.data.map { it[KEY_BOTTOM_PADDING] ?: 0 }
    
    // Panel Toggles
    val clipboardEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_CLIPBOARD_ENABLED] ?: true }
    val searchEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_SEARCH_ENABLED] ?: true }
    val currencyEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_CURRENCY_ENABLED] ?: true }
    val kaomojiEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_KAOMOJI_ENABLED] ?: true }
    val phrasesEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_PHRASES_ENABLED] ?: true }
    val translateEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_TRANSLATE_ENABLED] ?: true }
    val textEditingEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_TEXT_EDITING_ENABLED] ?: true }
    val emojiEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_EMOJI_ENABLED] ?: true }

    val followSystemTheme: Flow<Boolean> = context.dataStore.data.map { it[KEY_FOLLOW_SYSTEM_THEME] ?: false }
    val themePreference: Flow<String> = context.dataStore.data.map { it[KEY_THEME_PREFERENCE] ?: "system" }
    val numberRowEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_NUMBER_ROW] ?: false }
    val autoCapitalize: Flow<Boolean> = context.dataStore.data.map { it[KEY_AUTO_CAPITALIZE] ?: true }
    val spacebarCursor: Flow<Boolean> = context.dataStore.data.map { it[KEY_SPACEBAR_CURSOR] ?: true }
    val highContrast: Flow<Boolean> = context.dataStore.data.map { it[KEY_HIGH_CONTRAST] ?: false }
    val largeKeys: Flow<Boolean> = context.dataStore.data.map { it[KEY_LARGE_KEYS] ?: false }
    val multilingualEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_MULTILINGUAL] ?: false }
    val activeDicts: Flow<Set<String>> = context.dataStore.data.map { it[KEY_ACTIVE_DICTS] ?: setOf("en_us") }
    val dictionaryId: Flow<String> = context.dataStore.data.map { it[KEY_DICTIONARY] ?: "en_us" }

    suspend fun setKeyboardHeight(v: Float) { context.dataStore.edit { it[KEY_HEIGHT] = v } }
    suspend fun setKeyboardWidth(v: Int) { context.dataStore.edit { it[KEY_WIDTH] = v } }
    suspend fun setKeyboardAlignment(v: Int) { context.dataStore.edit { it[KEY_ALIGNMENT] = v } }
    suspend fun setSelectedLayout(v: String) { context.dataStore.edit { it[KEY_LAYOUT] = v } }
    suspend fun setHapticEnabled(v: Boolean) { context.dataStore.edit { it[KEY_HAPTIC] = v } }
    suspend fun setSoundEnabled(v: Boolean) { context.dataStore.edit { it[KEY_SOUND] = v } }
    suspend fun setSuggestionCount(v: Int) { context.dataStore.edit { it[KEY_SUGGESTIONS] = v } }
    suspend fun setPredictionsEnabled(v: Boolean) { context.dataStore.edit { it[KEY_PREDICTIONS] = v } }
    suspend fun setAutocorrectEnabled(v: Boolean) { context.dataStore.edit { it[KEY_AUTOCORRECT] = v } }
    suspend fun setBottomPadding(v: Int) { context.dataStore.edit { it[KEY_BOTTOM_PADDING] = v } }
    
    // Panel Setters
    suspend fun setClipboardEnabled(v: Boolean) { context.dataStore.edit { it[KEY_CLIPBOARD_ENABLED] = v } }
    suspend fun setSearchEnabled(v: Boolean) { context.dataStore.edit { it[KEY_SEARCH_ENABLED] = v } }
    suspend fun setCurrencyEnabled(v: Boolean) { context.dataStore.edit { it[KEY_CURRENCY_ENABLED] = v } }
    suspend fun setKaomojiEnabled(v: Boolean) { context.dataStore.edit { it[KEY_KAOMOJI_ENABLED] = v } }
    suspend fun setPhrasesEnabled(v: Boolean) { context.dataStore.edit { it[KEY_PHRASES_ENABLED] = v } }
    suspend fun setTranslateEnabled(v: Boolean) { context.dataStore.edit { it[KEY_TRANSLATE_ENABLED] = v } }
    suspend fun setTextEditingEnabled(v: Boolean) { context.dataStore.edit { it[KEY_TEXT_EDITING_ENABLED] = v } }
    suspend fun setEmojiEnabled(v: Boolean) { context.dataStore.edit { it[KEY_EMOJI_ENABLED] = v } }

    suspend fun setFollowSystemTheme(v: Boolean) { context.dataStore.edit { it[KEY_FOLLOW_SYSTEM_THEME] = v } }
    suspend fun setThemePreference(v: String) { context.dataStore.edit { it[KEY_THEME_PREFERENCE] = v } }
    suspend fun setNumberRowEnabled(v: Boolean) { context.dataStore.edit { it[KEY_NUMBER_ROW] = v } }
    suspend fun setAutoCapitalize(v: Boolean) { context.dataStore.edit { it[KEY_AUTO_CAPITALIZE] = v } }
    suspend fun setSpacebarCursor(v: Boolean) { context.dataStore.edit { it[KEY_SPACEBAR_CURSOR] = v } }
    suspend fun setHighContrast(v: Boolean) { context.dataStore.edit { it[KEY_HIGH_CONTRAST] = v } }
    suspend fun setLargeKeys(v: Boolean) { context.dataStore.edit { it[KEY_LARGE_KEYS] = v } }
    suspend fun setMultilingualEnabled(v: Boolean) { context.dataStore.edit { it[KEY_MULTILINGUAL] = v } }
    suspend fun setActiveDicts(v: Set<String>) { context.dataStore.edit { it[KEY_ACTIVE_DICTS] = v } }
    suspend fun setDictionaryId(v: String) { context.dataStore.edit { it[KEY_DICTIONARY] = v } }
}
