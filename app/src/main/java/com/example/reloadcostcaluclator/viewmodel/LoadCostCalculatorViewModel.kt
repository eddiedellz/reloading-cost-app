package com.example.reloadcostcaluclator.viewmodel

import androidx.lifecycle.ViewModel
import com.example.reloadcostcaluclator.util.AmmoCostCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoadCostCalculatorUiState(
    val powderPrice: String = "",
    val powderContainerWeight: String = "",
    val chargeWeight: String = "",
    val primerPrice: String = "",
    val primerQuantity: String = "",
    val bulletPrice: String = "",
    val bulletQuantity: String = "",
    val brassPrice: String = "",
    val brassQuantity: String = "",
    val brassReloadCount: String = "",
    val result: LoadCostResultUiState = LoadCostResultUiState(),
)

data class LoadCostResultUiState(
    val powderCostPerRound: Double = 0.0,
    val primerCostPerRound: Double = 0.0,
    val bulletCostPerRound: Double = 0.0,
    val brassCostPerRound: Double = 0.0,
    val totalCostPerRound: Double = 0.0,
    val totalCostPer50: Double = 0.0,
    val totalCostPer100: Double = 0.0,
)

class LoadCostCalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoadCostCalculatorUiState())
    val uiState: StateFlow<LoadCostCalculatorUiState> = _uiState.asStateFlow()

    fun onPowderPriceChanged(value: String) {
        _uiState.update { it.copy(powderPrice = value) }
    }

    fun onPowderContainerWeightChanged(value: String) {
        _uiState.update { it.copy(powderContainerWeight = value) }
    }

    fun onChargeWeightChanged(value: String) {
        _uiState.update { it.copy(chargeWeight = value) }
    }

    fun onPrimerPriceChanged(value: String) {
        _uiState.update { it.copy(primerPrice = value) }
    }

    fun onPrimerQuantityChanged(value: String) {
        _uiState.update { it.copy(primerQuantity = value) }
    }

    fun onBulletPriceChanged(value: String) {
        _uiState.update { it.copy(bulletPrice = value) }
    }

    fun onBulletQuantityChanged(value: String) {
        _uiState.update { it.copy(bulletQuantity = value) }
    }

    fun onBrassPriceChanged(value: String) {
        _uiState.update { it.copy(brassPrice = value) }
    }

    fun onBrassQuantityChanged(value: String) {
        _uiState.update { it.copy(brassQuantity = value) }
    }

    fun onBrassReloadCountChanged(value: String) {
        _uiState.update { it.copy(brassReloadCount = value) }
    }

    fun onCalculateClicked() {
        val state = _uiState.value

        val powderCostPerRound = AmmoCostCalculator.powderCostPerRound(
            powderPrice = state.powderPrice.toSafeDouble(),
            containerWeightLb = state.powderContainerWeight.toSafeDouble(),
            chargeWeightGr = state.chargeWeight.toSafeDouble(),
        )
        val primerCostPerRound = AmmoCostCalculator.primerCostPerRound(
            primerPrice = state.primerPrice.toSafeDouble(),
            primerQuantity = state.primerQuantity.toSafeInt(),
        )
        val bulletCostPerRound = AmmoCostCalculator.bulletCostPerRound(
            bulletPrice = state.bulletPrice.toSafeDouble(),
            bulletQuantity = state.bulletQuantity.toSafeInt(),
        )
        val brassCostPerRound = AmmoCostCalculator.brassCostPerRound(
            brassPrice = state.brassPrice.toSafeDouble(),
            brassQuantity = state.brassQuantity.toSafeInt(),
            reloadCount = state.brassReloadCount.toSafeInt(),
        )
        val totalCostPerRound = AmmoCostCalculator.totalCostPerRound(
            powderCost = powderCostPerRound,
            primerCost = primerCostPerRound,
            bulletCost = bulletCostPerRound,
            brassCost = brassCostPerRound,
        )

        _uiState.update {
            it.copy(
                result = LoadCostResultUiState(
                    powderCostPerRound = powderCostPerRound,
                    primerCostPerRound = primerCostPerRound,
                    bulletCostPerRound = bulletCostPerRound,
                    brassCostPerRound = brassCostPerRound,
                    totalCostPerRound = totalCostPerRound,
                    totalCostPer50 = AmmoCostCalculator.totalCostPer50(totalCostPerRound),
                    totalCostPer100 = AmmoCostCalculator.totalCostPer100(totalCostPerRound),
                ),
            )
        }
    }

    private fun String.toSafeDouble(): Double = toDoubleOrNull() ?: 0.0

    private fun String.toSafeInt(): Int = toIntOrNull() ?: 0
}
