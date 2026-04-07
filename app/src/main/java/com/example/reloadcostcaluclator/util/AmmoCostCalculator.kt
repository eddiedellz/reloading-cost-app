package com.example.reloadcostcaluclator.util

/**
 * Utility functions for ammunition cost calculations.
 */
object AmmoCostCalculator {

    private const val GRAINS_PER_POUND = 7000.0

    fun powderCostPerRound(
        powderPrice: Double,
        containerWeightLb: Double,
        chargeWeightGr: Double,
    ): Double {
        if (!powderPrice.isValidPositive() || !containerWeightLb.isValidPositive() || !chargeWeightGr.isValidPositive()) {
            return 0.0
        }

        val totalGrains = containerWeightLb * GRAINS_PER_POUND
        if (totalGrains <= 0.0) return 0.0

        val roundsPerContainer = totalGrains / chargeWeightGr
        if (roundsPerContainer <= 0.0) return 0.0

        return powderPrice / roundsPerContainer
    }

    fun primerCostPerRound(primerPrice: Double, primerQuantity: Int): Double {
        if (!primerPrice.isValidPositive() || primerQuantity <= 0) return 0.0
        return primerPrice / primerQuantity.toDouble()
    }

    fun bulletCostPerRound(bulletPrice: Double, bulletQuantity: Int): Double {
        if (!bulletPrice.isValidPositive() || bulletQuantity <= 0) return 0.0
        return bulletPrice / bulletQuantity.toDouble()
    }

    fun brassCostPerRound(brassPrice: Double, brassQuantity: Int, reloadCount: Int): Double {
        if (!brassPrice.isValidPositive() || brassQuantity <= 0 || reloadCount <= 0) return 0.0

        val totalUses = brassQuantity.toDouble() * reloadCount.toDouble()
        if (totalUses <= 0.0) return 0.0

        return brassPrice / totalUses
    }

    fun totalCostPerRound(
        powderCost: Double,
        primerCost: Double,
        bulletCost: Double,
        brassCost: Double,
    ): Double {
        if (!powderCost.isValidNonNegative() || !primerCost.isValidNonNegative() || !bulletCost.isValidNonNegative() || !brassCost.isValidNonNegative()) {
            return 0.0
        }

        return powderCost + primerCost + bulletCost + brassCost
    }

    fun totalCostPer50(total: Double): Double {
        if (!total.isValidNonNegative()) return 0.0
        return total * 50.0
    }

    fun totalCostPer100(total: Double): Double {
        if (!total.isValidNonNegative()) return 0.0
        return total * 100.0
    }

    private fun Double.isValidPositive(): Boolean = this.isFinite() && this > 0.0

    private fun Double.isValidNonNegative(): Boolean = this.isFinite() && this >= 0.0
}
