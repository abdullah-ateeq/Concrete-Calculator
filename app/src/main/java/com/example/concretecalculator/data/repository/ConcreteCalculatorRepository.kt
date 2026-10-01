package com.example.concretecalculator.data.repository

import com.example.concretecalculator.data.model.AppSettings
import com.example.concretecalculator.data.model.MeasurementUnit
import com.example.concretecalculator.data.model.SavedCalculation
import com.example.concretecalculator.data.model.ThemeMode
import com.example.concretecalculator.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.Flow

class ConcreteCalculatorRepository(private val preferencesManager: PreferencesManager) {

    val appSettings: Flow<AppSettings> = preferencesManager.appSettingsFlow

    val calculationHistory: Flow<List<SavedCalculation>> = preferencesManager.historyFlow

    suspend fun saveCalculation(calculation: SavedCalculation) {
        preferencesManager.saveCalculation(calculation)
    }

    suspend fun deleteCalculation(id: String) {
        preferencesManager.deleteCalculation(id)
    }

    suspend fun clearHistory() {
        preferencesManager.clearHistory()
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        preferencesManager.updateThemeMode(themeMode)
    }

    suspend fun updateDynamicColor(enabled: Boolean) {
        preferencesManager.updateDynamicColor(enabled)
    }

    suspend fun updateDefaultUnit(unit: MeasurementUnit) {
        preferencesManager.updateDefaultUnit(unit)
    }

    suspend fun updateDefaultWaste(wastePercent: Double) {
        preferencesManager.updateDefaultWaste(wastePercent)
    }

    suspend fun updateHapticFeedback(enabled: Boolean) {
        preferencesManager.updateHapticFeedback(enabled)
    }

    suspend fun updateSoundEffects(enabled: Boolean) {
        preferencesManager.updateSoundEffects(enabled)
    }

    suspend fun updateCurrency(currency: String) {
        preferencesManager.updateCurrency(currency)
    }

    suspend fun resetSettings() {
        preferencesManager.resetSettings()
    }
}
