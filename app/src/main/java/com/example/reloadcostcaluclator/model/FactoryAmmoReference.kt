package com.example.reloadcostcaluclator.model

data class FactoryAmmoReference(
    val caliber: String,
    val grain: Int,
    val bulletType: String?,
    val name: String,
    val pricePerRound: Double,
    val pricePer50: Double,
    val notes: String,
)

data class FactoryAmmoComparison(
    val reference: FactoryAmmoReference,
    val savingsPerRound: Double,
    val savingsPer50: Double,
    val savingsPer1000: Double,
)
