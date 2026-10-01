package com.example.concretecalculator.data.model

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val defaultUnit: MeasurementUnit = MeasurementUnit.METER,
    val defaultWastePercent: Double = 10.0,
    val hapticFeedback: Boolean = true,
    val soundEffects: Boolean = false,
    val currency: String = "USD"
)
