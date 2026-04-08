package com.example.reloadcostcaluclator.viewmodel.loadrecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reloadcostcaluclator.data.repository.BrassRepository
import com.example.reloadcostcaluclator.data.repository.BulletRepository
import com.example.reloadcostcaluclator.data.repository.LoadRecipeRepository
import com.example.reloadcostcaluclator.data.repository.PowderRepository
import com.example.reloadcostcaluclator.data.repository.PrimerRepository
import com.example.reloadcostcaluclator.util.AmmoCostCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class LoadCostSummaryViewModel(
    loadRecipeRepository: LoadRecipeRepository,
    powderRepository: PowderRepository,
    primerRepository: PrimerRepository,
    bulletRepository: BulletRepository,
    brassRepository: BrassRepository,
) : ViewModel() {

    data class LoadCostSummaryItemUi(
        val id: Long,
        val loadName: String,
        val caliber: String,
        val grain: Double,
        val costPerEach: Double,
        val costPer50: Double,
        val costPer1000: Double,
    )

    val summaryItems: StateFlow<List<LoadCostSummaryItemUi>> = combine(
        loadRecipeRepository.getAll(),
        powderRepository.getAll(),
        primerRepository.getAll(),
        bulletRepository.getAll(),
        brassRepository.getAll(),
    ) { recipes, powders, primers, bullets, brassList ->
        recipes.map { recipe ->
            val powder = powders.firstOrNull { it.id == recipe.powderId }
            val primer = primers.firstOrNull { it.id == recipe.primerId }
            val bullet = bullets.firstOrNull { it.id == recipe.bulletId }
            val brass = brassList.firstOrNull { it.id == recipe.brassId }

            val powderCostPerRound = AmmoCostCalculator.powderCostPerRound(
                powderPrice = powder?.pricePaid ?: 0.0,
                containerWeightLb = powder?.containerWeightLb ?: 0.0,
                chargeWeightGr = recipe.chargeWeightGr,
            )
            val primerCostPerRound = AmmoCostCalculator.primerCostPerRound(
                primerPrice = primer?.pricePaid ?: 0.0,
                primerQuantity = primer?.quantity ?: 0,
            )
            val bulletCostPerRound = AmmoCostCalculator.bulletCostPerRound(
                bulletPrice = bullet?.pricePaid ?: 0.0,
                bulletQuantity = bullet?.quantity ?: 0,
            )
            val brassCostPerRound = AmmoCostCalculator.brassCostPerRound(
                brassPrice = brass?.pricePaid ?: 0.0,
                brassQuantity = brass?.quantity ?: 0,
                reloadCount = brass?.reloadCount ?: 0,
            )

            val costPerEach = AmmoCostCalculator.totalCostPerRound(
                powderCost = powderCostPerRound,
                primerCost = primerCostPerRound,
                bulletCost = bulletCostPerRound,
                brassCost = brassCostPerRound,
            )

            LoadCostSummaryItemUi(
                id = recipe.id,
                loadName = recipe.name,
                caliber = recipe.caliber,
                grain = recipe.chargeWeightGr,
                costPerEach = costPerEach,
                costPer50 = AmmoCostCalculator.totalCostPer50(costPerEach),
                costPer1000 = costPerEach * 1000,
            )
        }.sortedBy { it.loadName.lowercase() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    companion object {
        fun provideFactory(
            loadRecipeRepository: LoadRecipeRepository,
            powderRepository: PowderRepository,
            primerRepository: PrimerRepository,
            bulletRepository: BulletRepository,
            brassRepository: BrassRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LoadCostSummaryViewModel(
                        loadRecipeRepository = loadRecipeRepository,
                        powderRepository = powderRepository,
                        primerRepository = primerRepository,
                        bulletRepository = bulletRepository,
                        brassRepository = brassRepository,
                    ) as T
                }
            }
    }
}
