package com.example.concretecalculator

import com.example.concretecalculator.data.model.ConcreteCalculationInput
import com.example.concretecalculator.data.model.MeasurementUnit
import com.example.concretecalculator.domain.calculator.ConcreteCalculatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ConcreteCalculatorEngineTest {

    private val delta = 0.0001

    // 1. Millimeter conversion: 1000 mm = 1.0 m
    @Test
    fun testMillimeterConversion() {
        val meters = ConcreteCalculatorEngine.convertToMeters(1000.0, MeasurementUnit.MILLIMETER)
        assertEquals(1.0, meters, delta)
        val metersSmall = ConcreteCalculatorEngine.convertToMeters(150.0, MeasurementUnit.MILLIMETER)
        assertEquals(0.15, metersSmall, delta)
    }

    // 2. Centimeter conversion: 100 cm = 1.0 m
    @Test
    fun testCentimeterConversion() {
        val meters = ConcreteCalculatorEngine.convertToMeters(100.0, MeasurementUnit.CENTIMETER)
        assertEquals(1.0, meters, delta)
        val metersVal = ConcreteCalculatorEngine.convertToMeters(25.5, MeasurementUnit.CENTIMETER)
        assertEquals(0.255, metersVal, delta)
    }

    // 3. Meter conversion: 5 m = 5.0 m
    @Test
    fun testMeterConversion() {
        val meters = ConcreteCalculatorEngine.convertToMeters(5.0, MeasurementUnit.METER)
        assertEquals(5.0, meters, delta)
    }

    // 4. Inch conversion: 1 inch = 0.0254 m, 12 inches = 0.3048 m
    @Test
    fun testInchConversion() {
        val meters = ConcreteCalculatorEngine.convertToMeters(1.0, MeasurementUnit.INCH)
        assertEquals(0.0254, meters, delta)
        val metersTwelve = ConcreteCalculatorEngine.convertToMeters(12.0, MeasurementUnit.INCH)
        assertEquals(0.3048, metersTwelve, delta)
    }

    // 5. Foot conversion: 1 foot = 0.3048 m, 10 feet = 3.048 m
    @Test
    fun testFootConversion() {
        val meters = ConcreteCalculatorEngine.convertToMeters(1.0, MeasurementUnit.FOOT)
        assertEquals(0.3048, meters, delta)
        val metersTen = ConcreteCalculatorEngine.convertToMeters(10.0, MeasurementUnit.FOOT)
        assertEquals(3.048, metersTen, delta)
    }

    // 6. Basic volume calculation: 5m x 4m x 0.15m (150mm) = 3.0 m3
    @Test
    fun testBasicVolumeCalculation() {
        val input = ConcreteCalculationInput(
            length = 5.0,
            width = 4.0,
            depth = 150.0,
            lengthUnit = MeasurementUnit.METER,
            widthUnit = MeasurementUnit.METER,
            depthUnit = MeasurementUnit.MILLIMETER,
            wastePercent = 0.0,
            bagsPerCubicMeter = null,
            bagSizeKg = null,
            pricePerCubicMeter = null
        )
        val result = ConcreteCalculatorEngine.calculateConcrete(input)
        assertEquals(3.0, result.baseVolumeM3, delta)
        assertEquals(3.0 * ConcreteCalculatorEngine.CUBIC_METER_TO_FEET_CUBED, result.volumeFt3, delta)
        assertEquals(3000.0, result.volumeLiters, delta)
    }

    // 7. Decimal depth calculation: 10m x 2.5m x 0.125m = 3.125 m3
    @Test
    fun testDecimalDepthCalculation() {
        val input = ConcreteCalculationInput(
            length = 10.0,
            width = 2.5,
            depth = 12.5, // 12.5 cm = 0.125 m
            lengthUnit = MeasurementUnit.METER,
            widthUnit = MeasurementUnit.METER,
            depthUnit = MeasurementUnit.CENTIMETER,
            wastePercent = 0.0,
            bagsPerCubicMeter = null,
            bagSizeKg = null,
            pricePerCubicMeter = null
        )
        val result = ConcreteCalculatorEngine.calculateConcrete(input)
        assertEquals(3.125, result.baseVolumeM3, delta)
    }

    // 8. Waste calculation: 10 m3 with 10% waste = 1.0 m3 waste
    @Test
    fun testWasteCalculation() {
        val input = ConcreteCalculationInput(
            length = 10.0,
            width = 1.0,
            depth = 1.0,
            lengthUnit = MeasurementUnit.METER,
            widthUnit = MeasurementUnit.METER,
            depthUnit = MeasurementUnit.METER,
            wastePercent = 10.0,
            bagsPerCubicMeter = null,
            bagSizeKg = null,
            pricePerCubicMeter = null
        )
        val result = ConcreteCalculatorEngine.calculateConcrete(input)
        assertEquals(10.0, result.baseVolumeM3, delta)
        assertEquals(1.0, result.wasteVolumeM3, delta)
    }

    // 9. Required volume calculation: 5m x 4m x 150mm with 10% waste = 3.3 m3 required
    @Test
    fun testRequiredVolumeCalculation() {
        val input = ConcreteCalculationInput(
            length = 5.0,
            width = 4.0,
            depth = 150.0,
            lengthUnit = MeasurementUnit.METER,
            widthUnit = MeasurementUnit.METER,
            depthUnit = MeasurementUnit.MILLIMETER,
            wastePercent = 10.0,
            bagsPerCubicMeter = null,
            bagSizeKg = null,
            pricePerCubicMeter = null
        )
        val result = ConcreteCalculatorEngine.calculateConcrete(input)
        assertEquals(3.0, result.baseVolumeM3, delta)
        assertEquals(0.3, result.wasteVolumeM3, delta)
        assertEquals(3.3, result.requiredVolumeM3, delta)
    }

    // 10. Bag estimation: 2.0 m3 required with 40 bags/m3 = 80 bags
    @Test
    fun testBagEstimation() {
        val input = ConcreteCalculationInput(
            length = 2.0,
            width = 1.0,
            depth = 1.0,
            lengthUnit = MeasurementUnit.METER,
            widthUnit = MeasurementUnit.METER,
            depthUnit = MeasurementUnit.METER,
            wastePercent = 0.0,
            bagsPerCubicMeter = 40.0,
            bagSizeKg = 25,
            pricePerCubicMeter = null
        )
        val result = ConcreteCalculatorEngine.calculateConcrete(input)
        assertNotNull(result.estimatedBags)
        assertEquals(80, result.estimatedBags)
    }

    // 11. Bag ceiling behavior: 1.01 m3 with 10 bags/m3 = 10.1 -> ceil is 11 bags
    @Test
    fun testBagCeilingBehavior() {
        val input = ConcreteCalculationInput(
            length = 1.01,
            width = 1.0,
            depth = 1.0,
            lengthUnit = MeasurementUnit.METER,
            widthUnit = MeasurementUnit.METER,
            depthUnit = MeasurementUnit.METER,
            wastePercent = 0.0,
            bagsPerCubicMeter = 10.0,
            bagSizeKg = 40,
            pricePerCubicMeter = null
        )
        val result = ConcreteCalculatorEngine.calculateConcrete(input)
        // 1.01 * 10 = 10.1 -> ceil is 11
        assertEquals(11, result.estimatedBags)
    }

    // 12. Cost calculation: 3.3 m3 required at $120/m3 = $396.00
    @Test
    fun testCostCalculation() {
        val input = ConcreteCalculationInput(
            length = 5.0,
            width = 4.0,
            depth = 150.0,
            lengthUnit = MeasurementUnit.METER,
            widthUnit = MeasurementUnit.METER,
            depthUnit = MeasurementUnit.MILLIMETER,
            wastePercent = 10.0,
            bagsPerCubicMeter = null,
            bagSizeKg = null,
            pricePerCubicMeter = 120.0
        )
        val result = ConcreteCalculatorEngine.calculateConcrete(input)
        assertNotNull(result.estimatedCost)
        assertEquals(3.3 * 120.0, result.estimatedCost!!, delta)
    }

    // 13. Zero validation: reject zero dimensions
    @Test
    fun testZeroValidation() {
        val zeroLengthValidation = ConcreteCalculatorEngine.validateDimension("0", "Length")
        assertFalse(zeroLengthValidation.isValid)
        assertEquals("Length must be greater than zero.", zeroLengthValidation.errorMessage)

        val zeroBagsValidation = ConcreteCalculatorEngine.validateBagsPerM3("0")
        assertFalse(zeroBagsValidation.isValid)
        assertEquals("Bags per m³ must be greater than zero.", zeroBagsValidation.errorMessage)
    }

    // 14. Negative validation: reject negative dimensions and negative price
    @Test
    fun testNegativeValidation() {
        val negativeWidth = ConcreteCalculatorEngine.validateDimension("-4.5", "Width")
        assertFalse(negativeWidth.isValid)
        assertEquals("Width must be greater than zero.", negativeWidth.errorMessage)

        val negativePrice = ConcreteCalculatorEngine.validatePrice("-50")
        assertFalse(negativePrice.isValid)
        assertEquals("Price cannot be negative.", negativePrice.errorMessage)

        val negativeWaste = ConcreteCalculatorEngine.validateWaste("-5")
        assertFalse(negativeWaste.isValid)
        assertEquals("Waste cannot be negative.", negativeWaste.errorMessage)
    }

    // 15. Waste validation: reject waste > 100% and empty waste
    @Test
    fun testWasteValidation() {
        val overHundredWaste = ConcreteCalculatorEngine.validateWaste("105")
        assertFalse(overHundredWaste.isValid)
        assertEquals("Waste must be between 0% and 100%.", overHundredWaste.errorMessage)

        val validWaste = ConcreteCalculatorEngine.validateWaste("15.5")
        assertTrue(validWaste.isValid)
        assertNull(validWaste.errorMessage)

        val emptyWaste = ConcreteCalculatorEngine.validateWaste("")
        assertFalse(emptyWaste.isValid)
    }

    // 16. Unit conversion consistency: round-trip consistency across units
    @Test
    fun testUnitConversionConsistency() {
        val originalMeters = 2.5
        for (unit in MeasurementUnit.entries) {
            val inUnit = ConcreteCalculatorEngine.convertFromMeters(originalMeters, unit)
            val backToMeters = ConcreteCalculatorEngine.convertToMeters(inUnit, unit)
            assertEquals("Failed consistency for unit: ${unit.name}", originalMeters, backToMeters, delta)
        }
    }
}
