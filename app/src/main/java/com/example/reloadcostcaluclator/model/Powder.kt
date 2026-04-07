package com.example.reloadcostcaluclator.model

data class Powder(
    val id: Long = 0,
    val name: String,
    val pricePerContainer: Double,
    val containerWeightLb: Double,
)
