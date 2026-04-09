package com.example.reloadcostcaluclator.util

import com.example.reloadcostcaluclator.data.local.entity.FactoryComparisonEntity

object FactoryAmmoComparisonCalculator {
    fun findBestMatch(
        loadCaliber: String,
        loadGrain: Int?,
        loadBulletType: String?,
        comparisons: List<FactoryComparisonEntity>,
    ): FactoryComparisonEntity? {
        if (loadGrain == null) return null

        return comparisons
            .asSequence()
            .filter { matchesCaliber(it.caliber, loadCaliber) }
            .filter { it.grain == loadGrain }
            .filter { bulletTypeMatches(loadBulletType, it.bulletType) }
            .maxByOrNull { it.updatedAtEpochMillis }
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
