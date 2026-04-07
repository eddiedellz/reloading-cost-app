package com.example.reloadcostcaluclator.models

/**
 * Represents brass purchase details and expected number of reloads.
 */
data class Brass(
    val name: String,
    val price: Double,
    val quantity: Int,
    val reloadCount: Int,
)
