package com.example.reloadcostcaluclator.util

import com.example.reloadcostcaluclator.model.FactoryAmmoComparison
import com.example.reloadcostcaluclator.model.FactoryAmmoReference

object FactoryAmmoComparisonCalculator {
    fun compare(
        loadCaliber: String,
        loadGrain: Int?,
        loadBulletType: String?,
        loadCostPerRound: Double,
        references: List<FactoryAmmoReference>,
    ): List<FactoryAmmoComparison> {
        if (loadGrain == null) return emptyList()

        return references
            .asSequence()
            .filter { matchesCaliber(it.caliber, loadCaliber) }
            .filter { it.grain == loadGrain }
            .filter { bulletTypeMatches(loadBulletType, it.bulletType) }
            .map { reference ->
                val savingsPerRound = reference.pricePerRound - loadCostPerRound
                FactoryAmmoComparison(
                    reference = reference,
                    savingsPerRound = savingsPerRound,
                    savingsPer50 = savingsPerRound * 50,
                    savingsPer1000 = savingsPerRound * 1_000,
                )
            }
            .sortedByDescending { it.savingsPerRound }
            .toList()
    }

    private fun matchesCaliber(referenceCaliber: String, loadCaliber: String): Boolean =
        referenceCaliber.trim().equals(loadCaliber.trim(), ignoreCase = true)

    private fun bulletTypeMatches(loadBulletType: String?, referenceBulletType: String?): Boolean {
        val loadType = loadBulletType?.trim().orEmpty()
        val referenceType = referenceBulletType?.trim().orEmpty()
        if (loadType.isBlank() || referenceType.isBlank()) return true
        return loadType.equals(referenceType, ignoreCase = true)
    }
}
