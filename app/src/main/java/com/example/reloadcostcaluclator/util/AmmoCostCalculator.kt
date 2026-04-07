package com.example.reloadcostcaluclator.util

object AmmoCostCalculator {

    private const val GRAINS_PER_POUND = 7000.0

    fun powderCostPerRound(
        powderPrice: Double,
        containerWeightLb: Double,
        chargeWeightGr: Double,
    ): Double {
        if (!powderPrice.isPositiveFinite() || !containerWeightLb.isPositiveFinite() || !chargeWeightGr.isPositiveFinite()) {
            return 0.0
        }

        val totalGrains = containerWeightLb * GRAINS_PER_POUND
        if (totalGrains <= 0.0) return 0.0

        val roundsPerContainer = totalGrains / chargeWeightGr
        if (roundsPerContainer <= 0.0) return 0.0

        return powderPrice / roundsPerContainer
    }

    fun primerCostPerRound(primerPrice: Double, primerQuantity: Int): Double {
        if (!primerPrice.isPositiveFinite() || primerQuantity <= 0) return 0.0
        return primerPrice / primerQuantity
    }

    fun bulletCostPerRound(bulletPrice: Double, bulletQuantity: Int): Double {
        if (!bulletPrice.isPositiveFinite() || bulletQuantity <= 0) return 0.0
        return bulletPrice / bulletQuantity
    }

    fun brassCostPerRound(brassPrice: Double, brassQuantity: Int, reloadCount: Int): Double {
        if (!brassPrice.isPositiveFinite() || brassQuantity <= 0 || reloadCount <= 0) return 0.0

        val totalUses = brassQuantity.toDouble() * reloadCount
        if (totalUses <= 0.0) return 0.0

        return brassPrice / totalUses
    }

    fun totalCostPerRound(
        powderCost: Double,
        primerCost: Double,
        bulletCost: Double,
        brassCost: Double,
    ): Double {
        if (!powderCost.isNonNegativeFinite() ||
            !primerCost.isNonNegativeFinite() ||
            !bulletCost.isNonNegativeFinite() ||
            !brassCost.isNonNegativeFinite()
        ) {
            return 0.0
        }

        return powderCost + primerCost + bulletCost + brassCost
    }

    fun totalCostPer50(totalCostPerRound: Double): Double {
        if (!totalCostPerRound.isNonNegativeFinite()) return 0.0
        return totalCostPerRound * 50
    }

    fun totalCostPer100(totalCostPerRound: Double): Double {
        if (!totalCostPerRound.isNonNegativeFinite()) return 0.0
        return totalCostPerRound * 100
    }

    private fun Double.isPositiveFinite(): Boolean = isFinite() && this > 0.0

    private fun Double.isNonNegativeFinite(): Boolean = isFinite() && this >= 0.0
}
