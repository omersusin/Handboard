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
        private val KEY_HEIGHT = floatPreferencesKey("keyboard_height")
        private val KEY_WIDTH = intPreferencesKey("keyboard_width")
        private val KEY_ALIGNMENT = intPreferencesKey("keyboard_alignment")
        private val KEY_LAYOUT = stringPreferencesKey("selected_layout")
        private val KEY_HAPTIC = booleanPreferencesKey("haptic_enabled")
        private val KEY_SUGGESTIONS = intPreferencesKey("suggestion_count")
        private val KEY_PREDICTIONS = booleanPreferencesKey("predictions_enabled")
        private val KEY_BOTTOM_PADDING = intPreferencesKey("bottom_padding")
        private val KEY_CLIPBOARD_ENABLED = booleanPreferencesKey("clipboard_enabled")
        private val KEY_FOLLOW_SYSTEM_THEME = booleanPreferencesKey("follow_system_theme")
    }

    val keyboardHeight: Flow<Float> = context.dataStore.data.map { it[KEY_HEIGHT] ?: 1.0f }
    val keyboardWidth: Flow<Int> = context.dataStore.data.map { it[KEY_WIDTH] ?: 100 }
    val keyboardAlignment: Flow<Int> = context.dataStore.data.map { it[KEY_ALIGNMENT] ?: 1 }
    val selectedLayout: Flow<String> = context.dataStore.data.map { it[KEY_LAYOUT] ?: "QWERTY" }
    val hapticEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_HAPTIC] ?: true }
    val suggestionCount: Flow<Int> = context.dataStore.data.map { it[KEY_SUGGESTIONS] ?: 3 }
    val predictionsEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_PREDICTIONS] ?: true }
    val bottomPadding: Flow<Int> = context.dataStore.data.map { it[KEY_BOTTOM_PADDING] ?: 0 }
    val clipboardEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_CLIPBOARD_ENABLED] ?: false }
    val followSystemTheme: Flow<Boolean> = context.dataStore.data.map { it[KEY_FOLLOW_SYSTEM_THEME] ?: false }

    suspend fun setKeyboardHeight(v: Float) { context.dataStore.edit { it[KEY_HEIGHT] = v } }
    suspend fun setKeyboardWidth(v: Int) { context.dataStore.edit { it[KEY_WIDTH] = v } }
    suspend fun setKeyboardAlignment(v: Int) { context.dataStore.edit { it[KEY_ALIGNMENT] = v } }
    suspend fun setSelectedLayout(v: String) { context.dataStore.edit { it[KEY_LAYOUT] = v } }
    suspend fun setHapticEnabled(v: Boolean) { context.dataStore.edit { it[KEY_HAPTIC] = v } }
    suspend fun setSuggestionCount(v: Int) { context.dataStore.edit { it[KEY_SUGGESTIONS] = v } }
    suspend fun setPredictionsEnabled(v: Boolean) { context.dataStore.edit { it[KEY_PREDICTIONS] = v } }
    suspend fun setBottomPadding(v: Int) { context.dataStore.edit { it[KEY_BOTTOM_PADDING] = v } }
    suspend fun setClipboardEnabled(v: Boolean) { context.dataStore.edit { it[KEY_CLIPBOARD_ENABLED] = v } }
    suspend fun setFollowSystemTheme(v: Boolean) { context.dataStore.edit { it[KEY_FOLLOW_SYSTEM_THEME] = v } }
}
