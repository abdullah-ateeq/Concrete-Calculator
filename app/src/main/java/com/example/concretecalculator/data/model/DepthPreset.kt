package com.example.concretecalculator.data.model

data class DepthPreset(
    val label: String,
    val valueInMm: Double?
) {
    companion object {
        val presets = listOf(
            DepthPreset("50 mm", 50.0),
            DepthPreset("75 mm", 75.0),
            DepthPreset("100 mm", 100.0),
            DepthPreset("125 mm", 125.0),
            DepthPreset("150 mm", 150.0),
            DepthPreset("200 mm", 200.0),
            DepthPreset("250 mm", 250.0),
            DepthPreset("300 mm", 300.0),
            DepthPreset("Custom", null)
        )
    }
}
