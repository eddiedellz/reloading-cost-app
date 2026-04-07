package com.example.reloadcostcaluclator.models

/**
 * Represents a powder product and its container pricing details.
 */
data class Powder(
    val name: String,
    val pricePerContainer: Double,
    val containerWeightLb: Double,
)
