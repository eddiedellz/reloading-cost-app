package com.example.reloadcostcaluclator.model

data class LoadCostResult(
    val powderCostPerRound: Double,
    val primerCostPerRound: Double,
    val bulletCostPerRound: Double,
    val brassCostPerRound: Double,
    val totalCostPerRound: Double,
    val totalCostPer50: Double,
    val totalCostPer100: Double,
)
