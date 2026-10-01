package com.example.concretecalculator.ui.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NumberFormatter {

    private val symbols = DecimalFormatSymbols(Locale.US)

    private val twoDecimalFormat = DecimalFormat("#,##0.00", symbols)
    private val threeDecimalFormat = DecimalFormat("#,##0.000", symbols)
    private val integerFormat = DecimalFormat("#,##0", symbols)
    private val flexibleFormat = DecimalFormat("#,##0.##", symbols)

    fun formatVolumeM3(volume: Double): String {
        return "${twoDecimalFormat.format(volume)} m³"
    }

    fun formatVolumeFt3(volume: Double): String {
        return "${twoDecimalFormat.format(volume)} ft³"
    }

    fun formatVolumeLiters(liters: Double): String {
        return "${integerFormat.format(liters)} L"
    }

    fun formatCost(cost: Double, currency: String): String {
        return "$currency ${twoDecimalFormat.format(cost)}"
    }

    fun formatNumber(value: Double): String {
        return flexibleFormat.format(value)
    }

    fun formatTwoDecimals(value: Double): String {
        return twoDecimalFormat.format(value)
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
