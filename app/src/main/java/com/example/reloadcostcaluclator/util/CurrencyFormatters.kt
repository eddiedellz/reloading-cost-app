package com.example.reloadcostcaluclator.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatters {

    private val usCurrencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)

    fun formatUsd(value: Double): String {
        val safeValue = if (value.isFinite()) value else 0.0
        return usCurrencyFormatter.format(safeValue)
    }

    fun formatCostPerRound(value: Double): String {
        val safeValue = if (value.isFinite() && value >= 0.0) value else 0.0
        return if (safeValue in 0.0..<1.0) {
            String.format(Locale.US, "%.1f¢/rd", safeValue * 100.0)
        } else {
            "${formatUsd(safeValue)}/rd"
        }
    }
}
