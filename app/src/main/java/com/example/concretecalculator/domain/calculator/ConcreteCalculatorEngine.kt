package com.example.concretecalculator.domain.calculator

import com.example.concretecalculator.data.model.ConcreteCalculationInput
import com.example.concretecalculator.data.model.ConcreteCalculationResult
import com.example.concretecalculator.data.model.MeasurementUnit
import com.example.concretecalculator.domain.validation.ValidationResult
import kotlin.math.ceil

object ConcreteCalculatorEngine {

    const val CUBIC_METER_TO_FEET_CUBED = 35.3146667
    const val CUBIC_METER_TO_LITERS = 1000.0

    /**
     * Centralized conversion to meters.
     * 1 mm = 0.001 m
     * 1 cm = 0.01 m
     * 1 m = 1 m
     * 1 inch = 0.0254 m
     * 1 foot = 0.3048 m
     */
    fun convertToMeters(value: Double, unit: MeasurementUnit): Double {
        return value * unit.metersMultiplier
    }

    /**
     * Convert meters back to the specified measurement unit.
     */
    fun convertFromMeters(meters: Double, unit: MeasurementUnit): Double {
        return if (unit.metersMultiplier > 0) meters / unit.metersMultiplier else meters
    }

    /**
     * Core calculation flow:
     * 1. Validate length, width, depth.
     * 2. Convert all dimensions to meters.
     * 3. Calculate cubic-meter volume (Length × Width × Depth).
     * 4. Calculate cubic-feet volume (1 m³ = 35.3146667 ft³).
     * 5. Calculate liters (1 m³ = 1000 L).
     * 6. Apply waste percentage (Required Volume = Volume × (1 + Waste / 100)).
     * 7. Calculate estimated bags if bagsPerCubicMeter is provided: ceil(Required Volume × Bags Per m³).
     * 8. Calculate estimated cost if pricePerCubicMeter is provided: Required Volume × Price Per m³.
     */
    fun calculateConcrete(input: ConcreteCalculationInput): ConcreteCalculationResult {
        require(input.length > 0) { "Length must be greater than zero." }
        require(input.width > 0) { "Width must be greater than zero." }
        require(input.depth > 0) { "Depth must be greater than zero." }
        require(input.wastePercent in 0.0..100.0) { "Waste must be between 0% and 100%." }

        val lengthMeters = convertToMeters(input.length, input.lengthUnit)
        val widthMeters = convertToMeters(input.width, input.widthUnit)
        val depthMeters = convertToMeters(input.depth, input.depthUnit)

        val baseVolumeM3 = lengthMeters * widthMeters * depthMeters
        val volumeFt3 = baseVolumeM3 * CUBIC_METER_TO_FEET_CUBED
        val volumeLiters = baseVolumeM3 * CUBIC_METER_TO_LITERS

        val wasteVolumeM3 = baseVolumeM3 * (input.wastePercent / 100.0)
        val requiredVolumeM3 = baseVolumeM3 * (1.0 + (input.wastePercent / 100.0))

        val estimatedBags = if (input.bagsPerCubicMeter != null && input.bagsPerCubicMeter > 0) {
            ceil(requiredVolumeM3 * input.bagsPerCubicMeter).toInt()
        } else {
            null
        }

        val estimatedCost = if (input.pricePerCubicMeter != null && input.pricePerCubicMeter >= 0) {
            requiredVolumeM3 * input.pricePerCubicMeter
        } else {
            null
        }

        return ConcreteCalculationResult(
            baseVolumeM3 = baseVolumeM3,
            volumeFt3 = volumeFt3,
            volumeLiters = volumeLiters,
            wasteVolumeM3 = wasteVolumeM3,
            requiredVolumeM3 = requiredVolumeM3,
            estimatedBags = estimatedBags,
            estimatedCost = estimatedCost
        )
    }

    // Validation Methods
    fun validateDimension(valueStr: String, fieldName: String): ValidationResult {
        val trimmed = valueStr.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult.error("Enter a valid $fieldName.")
        }
        val number = trimmed.toDoubleOrNull()
            ?: return ValidationResult.error("Enter a valid number for $fieldName.")

        return if (number <= 0.0) {
            ValidationResult.error("$fieldName must be greater than zero.")
        } else {
            ValidationResult.success()
        }
    }

    fun validateWaste(valueStr: String): ValidationResult {
        val trimmed = valueStr.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult.error("Enter waste percentage.")
        }
        val number = trimmed.toDoubleOrNull()
            ?: return ValidationResult.error("Enter a valid number for waste.")

        return if (number < 0.0) {
            ValidationResult.error("Waste cannot be negative.")
        } else if (number > 100.0) {
            ValidationResult.error("Waste must be between 0% and 100%.")
        } else {
            ValidationResult.success()
        }
    }

    fun validateBagsPerM3(valueStr: String): ValidationResult {
        val trimmed = valueStr.trim()
        if (trimmed.isEmpty()) {
            // Bags per m3 is optional
            return ValidationResult.success()
        }
        val number = trimmed.toDoubleOrNull()
            ?: return ValidationResult.error("Enter a valid number for bags per m³.")

        return if (number <= 0.0) {
            ValidationResult.error("Bags per m³ must be greater than zero.")
        } else {
            ValidationResult.success()
        }
    }

    fun validatePrice(valueStr: String): ValidationResult {
        val trimmed = valueStr.trim()
        if (trimmed.isEmpty()) {
            // Price is optional
            return ValidationResult.success()
        }
        val number = trimmed.toDoubleOrNull()
            ?: return ValidationResult.error("Enter a valid number for price.")

        return if (number < 0.0) {
            ValidationResult.error("Price cannot be negative.")
        } else {
            ValidationResult.success()
        }
    }
}
