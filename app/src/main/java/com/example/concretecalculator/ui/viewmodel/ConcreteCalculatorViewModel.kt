package com.example.concretecalculator.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.concretecalculator.data.model.AppSettings
import com.example.concretecalculator.data.model.ConcreteCalculationInput
import com.example.concretecalculator.data.model.ConcreteCalculationResult
import com.example.concretecalculator.data.model.DepthPreset
import com.example.concretecalculator.data.model.MeasurementUnit
import com.example.concretecalculator.data.model.SavedCalculation
import com.example.concretecalculator.data.model.ThemeMode
import com.example.concretecalculator.data.preferences.PreferencesManager
import com.example.concretecalculator.data.repository.ConcreteCalculatorRepository
import com.example.concretecalculator.domain.calculator.ConcreteCalculatorEngine
import com.example.concretecalculator.ui.util.FeedbackController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val lengthInput: String = "",
    val lengthUnit: MeasurementUnit = MeasurementUnit.METER,
    val lengthError: String? = null,

    val widthInput: String = "",
    val widthUnit: MeasurementUnit = MeasurementUnit.METER,
    val widthError: String? = null,

    val depthInput: String = "",
    val depthUnit: MeasurementUnit = MeasurementUnit.MILLIMETER,
    val depthError: String? = null,
    val selectedDepthPreset: String = "Custom",

    val wastePercent: Double = 10.0,
    val isCustomWaste: Boolean = false,
    val customWasteInput: String = "",
    val wasteError: String? = null,

    // Optional Bag Estimate
    val selectedBagSizeKg: Int? = 40,
    val isCustomBagSize: Boolean = false,
    val customBagSizeInput: String = "",
    val bagsPerM3Input: String = "",
    val bagsPerM3Error: String? = null,

    // Optional Cost
    val pricePerM3Input: String = "",
    val pricePerM3Error: String? = null,

    // Live preview
    val liveBaseVolumeM3: Double? = null,
    val liveRequiredVolumeM3: Double? = null,

    // Result
    val activeInput: ConcreteCalculationInput? = null,
    val activeResult: ConcreteCalculationResult? = null,
    val isCurrentResultSaved: Boolean = false,
    val activeCalculationCurrency: String = "USD"
)

class ConcreteCalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ConcreteCalculatorRepository(PreferencesManager(application))
    val feedbackController = FeedbackController(application)

    val appSettings: StateFlow<AppSettings> = repository.appSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    val calculationHistory: StateFlow<List<SavedCalculation>> = repository.calculationHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        // Observe settings changes to set defaults on initial launch if empty
        viewModelScope.launch {
            appSettings.collect { settings ->
                if (_uiState.value.lengthInput.isEmpty() &&
                    _uiState.value.widthInput.isEmpty() &&
                    _uiState.value.depthInput.isEmpty()
                ) {
                    _uiState.update { current ->
                        current.copy(
                            lengthUnit = settings.defaultUnit,
                            widthUnit = settings.defaultUnit,
                            depthUnit = if (settings.defaultUnit == MeasurementUnit.METER) MeasurementUnit.MILLIMETER else settings.defaultUnit,
                            wastePercent = settings.defaultWastePercent,
                            activeCalculationCurrency = settings.currency
                        )
                    }
                }
            }
        }
    }

    // Input handlers
    fun onLengthChange(value: String) {
        _uiState.update { it.copy(lengthInput = value, lengthError = null) }
        recalculateLivePreview()
    }

    fun onLengthUnitChange(unit: MeasurementUnit) {
        _uiState.update { it.copy(lengthUnit = unit) }
        recalculateLivePreview()
    }

    fun onWidthChange(value: String) {
        _uiState.update { it.copy(widthInput = value, widthError = null) }
        recalculateLivePreview()
    }

    fun onWidthUnitChange(unit: MeasurementUnit) {
        _uiState.update { it.copy(widthUnit = unit) }
        recalculateLivePreview()
    }

    fun onDepthChange(value: String) {
        _uiState.update {
            it.copy(
                depthInput = value,
                depthError = null,
                selectedDepthPreset = "Custom"
            )
        }
        recalculateLivePreview()
    }

    fun onDepthUnitChange(unit: MeasurementUnit) {
        _uiState.update { it.copy(depthUnit = unit, selectedDepthPreset = "Custom") }
        recalculateLivePreview()
    }

    fun onDepthPresetSelected(preset: DepthPreset) {
        if (preset.valueInMm != null) {
            // Preset value is in mm. Convert to current depth unit for user convenience.
            val meters = ConcreteCalculatorEngine.convertToMeters(preset.valueInMm, MeasurementUnit.MILLIMETER)
            val inCurrentUnit = ConcreteCalculatorEngine.convertFromMeters(meters, _uiState.value.depthUnit)
            val formatted = if (inCurrentUnit % 1.0 == 0.0) inCurrentUnit.toInt().toString() else "%.2f".format(inCurrentUnit)

            _uiState.update {
                it.copy(
                    depthInput = formatted,
                    depthError = null,
                    selectedDepthPreset = preset.label
                )
            }
        } else {
            _uiState.update { it.copy(selectedDepthPreset = "Custom") }
        }
        recalculateLivePreview()
    }

    fun onWastePercentSelected(waste: Double) {
        _uiState.update {
            it.copy(
                wastePercent = waste,
                isCustomWaste = false,
                customWasteInput = "",
                wasteError = null
            )
        }
        recalculateLivePreview()
    }

    fun onCustomWasteSelected() {
        _uiState.update {
            it.copy(
                isCustomWaste = true,
                customWasteInput = if (it.wastePercent > 0) it.wastePercent.toString() else ""
            )
        }
    }

    fun onCustomWasteChange(value: String) {
        val num = value.toDoubleOrNull()
        _uiState.update {
            it.copy(
                customWasteInput = value,
                wastePercent = num ?: it.wastePercent,
                wasteError = null
            )
        }
        recalculateLivePreview()
    }

    fun onBagSizeSelected(sizeKg: Int?) {
        _uiState.update {
            it.copy(
                selectedBagSizeKg = sizeKg,
                isCustomBagSize = (sizeKg == null),
                customBagSizeInput = ""
            )
        }
    }

    fun onCustomBagSizeChange(value: String) {
        val num = value.toIntOrNull()
        _uiState.update {
            it.copy(
                customBagSizeInput = value,
                selectedBagSizeKg = num
            )
        }
    }

    fun onBagsPerM3Change(value: String) {
        _uiState.update { it.copy(bagsPerM3Input = value, bagsPerM3Error = null) }
    }

    fun onPricePerM3Change(value: String) {
        _uiState.update { it.copy(pricePerM3Input = value, pricePerM3Error = null) }
    }

    private fun recalculateLivePreview() {
        val state = _uiState.value
        val length = state.lengthInput.toDoubleOrNull()
        val width = state.widthInput.toDoubleOrNull()
        val depth = state.depthInput.toDoubleOrNull()

        if (length != null && length > 0 &&
            width != null && width > 0 &&
            depth != null && depth > 0
        ) {
            val lengthM = ConcreteCalculatorEngine.convertToMeters(length, state.lengthUnit)
            val widthM = ConcreteCalculatorEngine.convertToMeters(width, state.widthUnit)
            val depthM = ConcreteCalculatorEngine.convertToMeters(depth, state.depthUnit)
            val baseVol = lengthM * widthM * depthM
            val reqVol = baseVol * (1.0 + (state.wastePercent / 100.0))
            _uiState.update {
                it.copy(
                    liveBaseVolumeM3 = baseVol,
                    liveRequiredVolumeM3 = reqVol
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    liveBaseVolumeM3 = null,
                    liveRequiredVolumeM3 = null
                )
            }
        }
    }

    fun calculate(onSuccess: () -> Unit) {
        val state = _uiState.value
        val settings = appSettings.value

        val lengthVal = ConcreteCalculatorEngine.validateDimension(state.lengthInput, "Length")
        val widthVal = ConcreteCalculatorEngine.validateDimension(state.widthInput, "Width")
        val depthVal = ConcreteCalculatorEngine.validateDimension(state.depthInput, "Depth")

        val wasteVal = if (state.isCustomWaste) {
            ConcreteCalculatorEngine.validateWaste(state.customWasteInput)
        } else {
            ConcreteCalculatorEngine.validateWaste(state.wastePercent.toString())
        }

        val bagsVal = ConcreteCalculatorEngine.validateBagsPerM3(state.bagsPerM3Input)
        val priceVal = ConcreteCalculatorEngine.validatePrice(state.pricePerM3Input)

        val hasError = !lengthVal.isValid || !widthVal.isValid || !depthVal.isValid ||
                !wasteVal.isValid || !bagsVal.isValid || !priceVal.isValid

        if (hasError) {
            _uiState.update {
                it.copy(
                    lengthError = lengthVal.errorMessage,
                    widthError = widthVal.errorMessage,
                    depthError = depthVal.errorMessage,
                    wasteError = wasteVal.errorMessage,
                    bagsPerM3Error = bagsVal.errorMessage,
                    pricePerM3Error = priceVal.errorMessage
                )
            }
            feedbackController.performWarningHaptic(settings.hapticFeedback)
            return
        }

        // All inputs are valid
        val length = state.lengthInput.toDouble()
        val width = state.widthInput.toDouble()
        val depth = state.depthInput.toDouble()
        val waste = if (state.isCustomWaste) state.customWasteInput.toDouble() else state.wastePercent
        val bagsPerM3 = state.bagsPerM3Input.trim().toDoubleOrNull()
        val pricePerM3 = state.pricePerM3Input.trim().toDoubleOrNull()

        val input = ConcreteCalculationInput(
            length = length,
            width = width,
            depth = depth,
            lengthUnit = state.lengthUnit,
            widthUnit = state.widthUnit,
            depthUnit = state.depthUnit,
            wastePercent = waste,
            bagsPerCubicMeter = bagsPerM3,
            bagSizeKg = state.selectedBagSizeKg,
            pricePerCubicMeter = pricePerM3
        )

        val result = ConcreteCalculatorEngine.calculateConcrete(input)

        _uiState.update {
            it.copy(
                activeInput = input,
                activeResult = result,
                isCurrentResultSaved = false,
                activeCalculationCurrency = settings.currency,
                lengthError = null,
                widthError = null,
                depthError = null,
                wasteError = null,
                bagsPerM3Error = null,
                pricePerM3Error = null
            )
        }

        feedbackController.performLightHaptic(hapticsEnabled = settings.hapticFeedback)
        feedbackController.playSubtleSound(soundEnabled = settings.soundEffects)
        onSuccess()
    }

    fun saveCurrentCalculation(onSaved: () -> Unit = {}) {
        val state = _uiState.value
        val input = state.activeInput ?: return
        val result = state.activeResult ?: return
        val settings = appSettings.value

        viewModelScope.launch {
            val calculation = SavedCalculation(
                input = input,
                result = result,
                currency = state.activeCalculationCurrency
            )
            repository.saveCalculation(calculation)
            _uiState.update { it.copy(isCurrentResultSaved = true) }
            feedbackController.performLightHaptic(hapticsEnabled = settings.hapticFeedback)
            feedbackController.playSubtleSound(soundEnabled = settings.soundEffects)
            onSaved()
        }
    }

    fun resetCalculator() {
        val settings = appSettings.value
        _uiState.update {
            CalculatorUiState(
                lengthUnit = settings.defaultUnit,
                widthUnit = settings.defaultUnit,
                depthUnit = if (settings.defaultUnit == MeasurementUnit.METER) MeasurementUnit.MILLIMETER else settings.defaultUnit,
                wastePercent = settings.defaultWastePercent,
                activeCalculationCurrency = settings.currency
            )
        }
        feedbackController.performLightHaptic(hapticsEnabled = settings.hapticFeedback)
    }

    fun loadCalculationIntoResult(saved: SavedCalculation) {
        _uiState.update {
            it.copy(
                activeInput = saved.input,
                activeResult = saved.result,
                activeCalculationCurrency = saved.currency,
                isCurrentResultSaved = true
            )
        }
    }

    fun deleteHistoryItem(id: String) {
        viewModelScope.launch {
            repository.deleteCalculation(id)
            feedbackController.performLightHaptic(hapticsEnabled = appSettings.value.hapticFeedback)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            feedbackController.performLightHaptic(hapticsEnabled = appSettings.value.hapticFeedback)
        }
    }

    // Settings
    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            repository.updateThemeMode(themeMode)
        }
    }

    fun updateDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateDynamicColor(enabled)
        }
    }

    fun updateDefaultUnit(unit: MeasurementUnit) {
        viewModelScope.launch {
            repository.updateDefaultUnit(unit)
        }
    }

    fun updateDefaultWaste(wastePercent: Double) {
        viewModelScope.launch {
            repository.updateDefaultWaste(wastePercent)
        }
    }

    fun updateHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateHapticFeedback(enabled)
        }
    }

    fun updateSoundEffects(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSoundEffects(enabled)
        }
    }

    fun updateCurrency(currency: String) {
        viewModelScope.launch {
            repository.updateCurrency(currency)
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            repository.resetSettings()
            feedbackController.performLightHaptic(hapticsEnabled = appSettings.value.hapticFeedback)
        }
    }
}
