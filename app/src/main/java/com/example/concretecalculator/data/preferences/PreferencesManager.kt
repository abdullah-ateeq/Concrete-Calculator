package com.example.concretecalculator.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.concretecalculator.data.model.AppSettings
import com.example.concretecalculator.data.model.MeasurementUnit
import com.example.concretecalculator.data.model.SavedCalculation
import com.example.concretecalculator.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "concrete_calculator_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        private val KEY_DEFAULT_UNIT = stringPreferencesKey("default_unit")
        private val KEY_DEFAULT_WASTE = doublePreferencesKey("default_waste")
        private val KEY_HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        private val KEY_SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        private val KEY_CURRENCY = stringPreferencesKey("currency")
        private val KEY_SAVED_HISTORY = stringPreferencesKey("saved_history_json")

        const val MAX_HISTORY_ITEMS = 50
    }

    val appSettingsFlow: Flow<AppSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeStr = preferences[KEY_THEME_MODE] ?: ThemeMode.SYSTEM.name
            val themeMode = try {
                ThemeMode.valueOf(themeStr)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            }

            val dynamicColor = preferences[KEY_DYNAMIC_COLOR] ?: true

            val unitStr = preferences[KEY_DEFAULT_UNIT] ?: MeasurementUnit.METER.name
            val defaultUnit = try {
                MeasurementUnit.valueOf(unitStr)
            } catch (e: Exception) {
                MeasurementUnit.METER
            }

            val defaultWaste = preferences[KEY_DEFAULT_WASTE] ?: 10.0
            val haptic = preferences[KEY_HAPTIC_FEEDBACK] ?: true
            val sound = preferences[KEY_SOUND_EFFECTS] ?: false
            val currency = preferences[KEY_CURRENCY] ?: "USD"

            AppSettings(
                themeMode = themeMode,
                dynamicColor = dynamicColor,
                defaultUnit = defaultUnit,
                defaultWastePercent = defaultWaste,
                hapticFeedback = haptic,
                soundEffects = sound,
                currency = currency
            )
        }

    val historyFlow: Flow<List<SavedCalculation>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val jsonStr = preferences[KEY_SAVED_HISTORY] ?: "[]"
            parseHistoryJson(jsonStr)
        }

    suspend fun saveCalculation(calculation: SavedCalculation) {
        context.dataStore.edit { preferences ->
            val existingJson = preferences[KEY_SAVED_HISTORY] ?: "[]"
            val currentList = parseHistoryJson(existingJson).toMutableList()

            // Prepend new calculation so newest is first
            currentList.add(0, calculation)

            // Limit to MAX_HISTORY_ITEMS (50) - FIFO: oldest removed
            val trimmedList = if (currentList.size > MAX_HISTORY_ITEMS) {
                currentList.subList(0, MAX_HISTORY_ITEMS)
            } else {
                currentList
            }

            preferences[KEY_SAVED_HISTORY] = serializeHistoryList(trimmedList)
        }
    }

    suspend fun deleteCalculation(id: String) {
        context.dataStore.edit { preferences ->
            val existingJson = preferences[KEY_SAVED_HISTORY] ?: "[]"
            val currentList = parseHistoryJson(existingJson).filterNot { it.id == id }
            preferences[KEY_SAVED_HISTORY] = serializeHistoryList(currentList)
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { preferences ->
            preferences[KEY_SAVED_HISTORY] = "[]"
        }
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = themeMode.name
        }
    }

    suspend fun updateDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun updateDefaultUnit(unit: MeasurementUnit) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DEFAULT_UNIT] = unit.name
        }
    }

    suspend fun updateDefaultWaste(wastePercent: Double) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DEFAULT_WASTE] = wastePercent
        }
    }

    suspend fun updateHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_HAPTIC_FEEDBACK] = enabled
        }
    }

    suspend fun updateSoundEffects(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SOUND_EFFECTS] = enabled
        }
    }

    suspend fun updateCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CURRENCY] = currency
        }
    }

    suspend fun resetSettings() {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = ThemeMode.SYSTEM.name
            preferences[KEY_DYNAMIC_COLOR] = true
            preferences[KEY_DEFAULT_UNIT] = MeasurementUnit.METER.name
            preferences[KEY_DEFAULT_WASTE] = 10.0
            preferences[KEY_HAPTIC_FEEDBACK] = true
            preferences[KEY_SOUND_EFFECTS] = false
            preferences[KEY_CURRENCY] = "USD"
        }
    }

    private fun parseHistoryJson(jsonStr: String): List<SavedCalculation> {
        val list = mutableListOf<SavedCalculation>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(SavedCalculation.fromJson(obj))
            }
        } catch (e: Exception) {
            // Return empty list on parse error
        }
        return list
    }

    private fun serializeHistoryList(list: List<SavedCalculation>): String {
        val jsonArray = JSONArray()
        list.forEach { item ->
            jsonArray.put(item.toJson())
        }
        return jsonArray.toString()
    }
}
