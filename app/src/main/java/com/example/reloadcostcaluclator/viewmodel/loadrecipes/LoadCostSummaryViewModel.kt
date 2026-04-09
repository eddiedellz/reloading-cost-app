package com.example.reloadcostcaluclator.viewmodel.loadrecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reloadcostcaluclator.data.repository.BrassRepository
import com.example.reloadcostcaluclator.data.repository.BulletRepository
import com.example.reloadcostcaluclator.data.repository.FactoryComparisonRepository
import com.example.reloadcostcaluclator.data.repository.LoadRecipeRepository
import com.example.reloadcostcaluclator.data.repository.PowderRepository
import com.example.reloadcostcaluclator.data.repository.PrimerRepository
import com.example.reloadcostcaluclator.util.AmmoCostCalculator
import com.example.reloadcostcaluclator.util.FactoryAmmoComparisonCalculator
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
    factoryComparisonRepository: FactoryComparisonRepository,
) : ViewModel() {

    data class LoadCostSummaryItemUi(
        val id: Long,
        val loadName: String,
        val caliber: String,
        val grain: Double,
        val costPerEach: Double,
        val costPer50: Double,
        val costPer100: Double,
        val factoryCostPerRound: Double?,
    )

    private data class RecipeCoreData(
        val recipes: List<com.example.reloadcostcaluclator.data.local.entity.LoadRecipeEntity>,
        val powders: List<com.example.reloadcostcaluclator.data.local.entity.PowderEntity>,
        val primers: List<com.example.reloadcostcaluclator.data.local.entity.PrimerEntity>,
    )

    private data class ComparisonData(
        val bullets: List<com.example.reloadcostcaluclator.data.local.entity.BulletEntity>,
        val brassList: List<com.example.reloadcostcaluclator.data.local.entity.BrassEntity>,
        val factoryComparisons: List<com.example.reloadcostcaluclator.data.local.entity.FactoryComparisonEntity>,
    )

    val summaryItems: StateFlow<List<LoadCostSummaryItemUi>> = combine(
        combine(
            loadRecipeRepository.getAll(),
            powderRepository.getAll(),
            primerRepository.getAll(),
        ) { recipes, powders, primers ->
            RecipeCoreData(recipes, powders, primers)
        },
        combine(
            bulletRepository.getAll(),
            brassRepository.getAll(),
            factoryComparisonRepository.getAll(),
        ) { bullets, brassList, factoryComparisons ->
            ComparisonData(bullets, brassList, factoryComparisons)
        },
    ) { core, comparison ->
        core.recipes.map { recipe ->
            val powder = core.powders.firstOrNull { it.id == recipe.powderId }
            val primer = core.primers.firstOrNull { it.id == recipe.primerId }
            val bullet = comparison.bullets.firstOrNull { it.id == recipe.bulletId }
            val brass = comparison.brassList.firstOrNull { it.id == recipe.brassId }

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

            val factory = FactoryAmmoComparisonCalculator.findBestMatch(
                loadCaliber = recipe.caliber,
                loadGrain = bullet?.grain,
                loadBulletType = bullet?.bulletType,
                comparisons = comparison.factoryComparisons,
            )

            LoadCostSummaryItemUi(
                id = recipe.id,
                loadName = recipe.name,
                caliber = recipe.caliber,
                grain = recipe.chargeWeightGr,
                costPerEach = costPerEach,
                costPer50 = AmmoCostCalculator.totalCostPer50(costPerEach),
                costPer100 = AmmoCostCalculator.totalCostPer100(costPerEach),
                factoryCostPerRound = factory?.costPerRound,
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
            factoryComparisonRepository: FactoryComparisonRepository,
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
                        factoryComparisonRepository = factoryComparisonRepository,
                    ) as T
                }
            }
    }
}
