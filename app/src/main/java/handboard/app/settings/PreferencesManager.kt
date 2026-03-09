package handboard.app.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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
        val KEY_BOTTOM_PADDING = intPreferencesKey("bottom_padding")
        val KEY_CLIPBOARD_ENABLED = booleanPreferencesKey("clipboard_enabled")
        val KEY_FOLLOW_SYSTEM_THEME = booleanPreferencesKey("follow_system_theme")
        val KEY_NUMBER_ROW = booleanPreferencesKey("number_row_enabled")
        val KEY_AUTO_CAPITALIZE = booleanPreferencesKey("auto_capitalize")
        val KEY_SPACEBAR_CURSOR = booleanPreferencesKey("spacebar_cursor")
        val KEY_HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val KEY_LARGE_KEYS = booleanPreferencesKey("large_keys")
    }

    val keyboardHeight: Flow<Float> = context.dataStore.data.map { it[KEY_HEIGHT] ?: 1.0f }
    val keyboardWidth: Flow<Int> = context.dataStore.data.map { it[KEY_WIDTH] ?: 100 }
    val keyboardAlignment: Flow<Int> = context.dataStore.data.map { it[KEY_ALIGNMENT] ?: 1 }
    val selectedLayout: Flow<String> = context.dataStore.data.map { it[KEY_LAYOUT] ?: "QWERTY" }
    val hapticEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_HAPTIC] ?: true }
    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_SOUND] ?: false }
    val suggestionCount: Flow<Int> = context.dataStore.data.map { it[KEY_SUGGESTIONS] ?: 3 }
    val predictionsEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_PREDICTIONS] ?: true }
    val bottomPadding: Flow<Int> = context.dataStore.data.map { it[KEY_BOTTOM_PADDING] ?: 0 }
    val clipboardEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_CLIPBOARD_ENABLED] ?: false }
    val followSystemTheme: Flow<Boolean> = context.dataStore.data.map { it[KEY_FOLLOW_SYSTEM_THEME] ?: false }
    val numberRowEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_NUMBER_ROW] ?: false }
    val autoCapitalize: Flow<Boolean> = context.dataStore.data.map { it[KEY_AUTO_CAPITALIZE] ?: true }
    val spacebarCursor: Flow<Boolean> = context.dataStore.data.map { it[KEY_SPACEBAR_CURSOR] ?: true }
    val highContrast: Flow<Boolean> = context.dataStore.data.map { it[KEY_HIGH_CONTRAST] ?: false }
    val largeKeys: Flow<Boolean> = context.dataStore.data.map { it[KEY_LARGE_KEYS] ?: false }

    suspend fun setKeyboardHeight(v: Float) { context.dataStore.edit { it[KEY_HEIGHT] = v } }
    suspend fun setKeyboardWidth(v: Int) { context.dataStore.edit { it[KEY_WIDTH] = v } }
    suspend fun setKeyboardAlignment(v: Int) { context.dataStore.edit { it[KEY_ALIGNMENT] = v } }
    suspend fun setSelectedLayout(v: String) { context.dataStore.edit { it[KEY_LAYOUT] = v } }
    suspend fun setHapticEnabled(v: Boolean) { context.dataStore.edit { it[KEY_HAPTIC] = v } }
    suspend fun setSoundEnabled(v: Boolean) { context.dataStore.edit { it[KEY_SOUND] = v } }
    suspend fun setSuggestionCount(v: Int) { context.dataStore.edit { it[KEY_SUGGESTIONS] = v } }
    suspend fun setPredictionsEnabled(v: Boolean) { context.dataStore.edit { it[KEY_PREDICTIONS] = v } }
    suspend fun setBottomPadding(v: Int) { context.dataStore.edit { it[KEY_BOTTOM_PADDING] = v } }
    suspend fun setClipboardEnabled(v: Boolean) { context.dataStore.edit { it[KEY_CLIPBOARD_ENABLED] = v } }
    suspend fun setFollowSystemTheme(v: Boolean) { context.dataStore.edit { it[KEY_FOLLOW_SYSTEM_THEME] = v } }
    suspend fun setNumberRowEnabled(v: Boolean) { context.dataStore.edit { it[KEY_NUMBER_ROW] = v } }
    suspend fun setAutoCapitalize(v: Boolean) { context.dataStore.edit { it[KEY_AUTO_CAPITALIZE] = v } }
    suspend fun setSpacebarCursor(v: Boolean) { context.dataStore.edit { it[KEY_SPACEBAR_CURSOR] = v } }
    suspend fun setHighContrast(v: Boolean) { context.dataStore.edit { it[KEY_HIGH_CONTRAST] = v } }
    suspend fun setLargeKeys(v: Boolean) { context.dataStore.edit { it[KEY_LARGE_KEYS] = v } }
}
