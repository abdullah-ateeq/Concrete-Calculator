package com.example.concretecalculator.data.model

import org.json.JSONObject
import java.util.UUID

data class ConcreteCalculationInput(
    val length: Double,
    val width: Double,
    val depth: Double,
    val lengthUnit: MeasurementUnit,
    val widthUnit: MeasurementUnit,
    val depthUnit: MeasurementUnit,
    val wastePercent: Double,
    val bagsPerCubicMeter: Double?,
    val bagSizeKg: Int?,
    val pricePerCubicMeter: Double?
)

data class ConcreteCalculationResult(
    val baseVolumeM3: Double,
    val volumeFt3: Double,
    val volumeLiters: Double,
    val wasteVolumeM3: Double,
    val requiredVolumeM3: Double,
    val estimatedBags: Int?,
    val estimatedCost: Double?
)

data class SavedCalculation(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val input: ConcreteCalculationInput,
    val result: ConcreteCalculationResult,
    val currency: String = "USD"
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("timestamp", timestamp)
        json.put("currency", currency)

        val inputJson = JSONObject()
        inputJson.put("length", input.length)
        inputJson.put("width", input.width)
        inputJson.put("depth", input.depth)
        inputJson.put("lengthUnit", input.lengthUnit.name)
        inputJson.put("widthUnit", input.widthUnit.name)
        inputJson.put("depthUnit", input.depthUnit.name)
        inputJson.put("wastePercent", input.wastePercent)
        if (input.bagsPerCubicMeter != null) {
            inputJson.put("bagsPerCubicMeter", input.bagsPerCubicMeter)
        }
        if (input.bagSizeKg != null) {
            inputJson.put("bagSizeKg", input.bagSizeKg)
        }
        if (input.pricePerCubicMeter != null) {
            inputJson.put("pricePerCubicMeter", input.pricePerCubicMeter)
        }
        json.put("input", inputJson)

        val resultJson = JSONObject()
        resultJson.put("baseVolumeM3", result.baseVolumeM3)
        resultJson.put("volumeFt3", result.volumeFt3)
        resultJson.put("volumeLiters", result.volumeLiters)
        resultJson.put("wasteVolumeM3", result.wasteVolumeM3)
        resultJson.put("requiredVolumeM3", result.requiredVolumeM3)
        if (result.estimatedBags != null) {
            resultJson.put("estimatedBags", result.estimatedBags)
        }
        if (result.estimatedCost != null) {
            resultJson.put("estimatedCost", result.estimatedCost)
        }
        json.put("result", resultJson)

        return json
    }

    companion object {
        fun fromJson(json: JSONObject): SavedCalculation {
            val id = json.optString("id", UUID.randomUUID().toString())
            val timestamp = json.optLong("timestamp", System.currentTimeMillis())
            val currency = json.optString("currency", "USD")

            val inputJson = json.getJSONObject("input")
            val input = ConcreteCalculationInput(
                length = inputJson.getDouble("length"),
                width = inputJson.getDouble("width"),
                depth = inputJson.getDouble("depth"),
                lengthUnit = MeasurementUnit.valueOf(inputJson.getString("lengthUnit")),
                widthUnit = MeasurementUnit.valueOf(inputJson.getString("widthUnit")),
                depthUnit = MeasurementUnit.valueOf(inputJson.getString("depthUnit")),
                wastePercent = inputJson.getDouble("wastePercent"),
                bagsPerCubicMeter = if (inputJson.has("bagsPerCubicMeter")) inputJson.getDouble("bagsPerCubicMeter") else null,
                bagSizeKg = if (inputJson.has("bagSizeKg")) inputJson.getInt("bagSizeKg") else null,
                pricePerCubicMeter = if (inputJson.has("pricePerCubicMeter")) inputJson.getDouble("pricePerCubicMeter") else null
            )

            val resultJson = json.getJSONObject("result")
            val result = ConcreteCalculationResult(
                baseVolumeM3 = resultJson.getDouble("baseVolumeM3"),
                volumeFt3 = resultJson.getDouble("volumeFt3"),
                volumeLiters = resultJson.getDouble("volumeLiters"),
                wasteVolumeM3 = resultJson.getDouble("wasteVolumeM3"),
                requiredVolumeM3 = resultJson.getDouble("requiredVolumeM3"),
                estimatedBags = if (resultJson.has("estimatedBags")) resultJson.getInt("estimatedBags") else null,
                estimatedCost = if (resultJson.has("estimatedCost")) resultJson.getDouble("estimatedCost") else null
            )

            return SavedCalculation(
                id = id,
                timestamp = timestamp,
                input = input,
                result = result,
                currency = currency
            )
        }
    }
}
