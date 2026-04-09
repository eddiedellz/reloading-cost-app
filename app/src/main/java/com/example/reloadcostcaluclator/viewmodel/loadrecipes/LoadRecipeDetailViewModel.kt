package com.example.reloadcostcaluclator.viewmodel.loadrecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reloadcostcaluclator.data.local.entity.BrassEntity
import com.example.reloadcostcaluclator.data.local.entity.BulletEntity
import com.example.reloadcostcaluclator.data.local.entity.FactoryComparisonEntity
import com.example.reloadcostcaluclator.data.local.entity.LoadRecipeEntity
import com.example.reloadcostcaluclator.data.local.entity.PowderEntity
import com.example.reloadcostcaluclator.data.local.entity.PrimerEntity
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

data class LoadRecipeDetailUiState(
    val loadRecipe: LoadRecipeEntity? = null,
    val powder: PowderEntity? = null,
    val primer: PrimerEntity? = null,
    val bullet: BulletEntity? = null,
    val brass: BrassEntity? = null,
    val powderCostPerRound: Double = 0.0,
    val primerCostPerRound: Double = 0.0,
    val bulletCostPerRound: Double = 0.0,
    val brassCostPerRound: Double = 0.0,
    val totalCostPerRound: Double = 0.0,
    val totalCostPer50: Double = 0.0,
    val totalCostPer100: Double = 0.0,
    val matchedFactory: FactoryComparisonEntity? = null,
    val factoryCostPer50: Double = 0.0,
    val factoryCostPer100: Double = 0.0,
    val savingsPer50: Double = 0.0,
    val savingsPer100: Double = 0.0,
)

class LoadRecipeDetailViewModel(
    recipeId: Long,
    loadRecipeRepository: LoadRecipeRepository,
    powderRepository: PowderRepository,
    primerRepository: PrimerRepository,
    bulletRepository: BulletRepository,
    brassRepository: BrassRepository,
    factoryComparisonRepository: FactoryComparisonRepository,
) : ViewModel() {

    private data class RecipeAndPowderPrimerData(
        val recipe: LoadRecipeEntity?,
        val powders: List<PowderEntity>,
        val primers: List<PrimerEntity>,
    )

    private data class BulletBrassFactoryData(
        val bullets: List<BulletEntity>,
        val brassList: List<BrassEntity>,
        val factoryComparisons: List<FactoryComparisonEntity>,
    )

    val uiState: StateFlow<LoadRecipeDetailUiState> = combine(
        combine(
            loadRecipeRepository.getById(recipeId),
            powderRepository.getAll(),
            primerRepository.getAll(),
        ) { recipe, powders, primers ->
            RecipeAndPowderPrimerData(recipe, powders, primers)
        },
        combine(
            bulletRepository.getAll(),
            brassRepository.getAll(),
            factoryComparisonRepository.getAll(),
        ) { bullets, brass, factoryComparisons ->
            BulletBrassFactoryData(bullets, brass, factoryComparisons)
        },
    ) { rppData, bbfData ->
        val recipe = rppData.recipe ?: return@combine LoadRecipeDetailUiState()

        val powder = rppData.powders.firstOrNull { it.id == recipe.powderId }
        val primer = rppData.primers.firstOrNull { it.id == recipe.primerId }
        val bullet = bbfData.bullets.firstOrNull { it.id == recipe.bulletId }
        val brass = bbfData.brassList.firstOrNull { it.id == recipe.brassId }

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

        val totalCostPerRound = AmmoCostCalculator.totalCostPerRound(
            powderCost = powderCostPerRound,
            primerCost = primerCostPerRound,
            bulletCost = bulletCostPerRound,
            brassCost = brassCostPerRound,
        )

        val matchedFactory = FactoryAmmoComparisonCalculator.findBestMatch(
            loadCaliber = recipe.caliber,
            loadGrain = bullet?.grain,
            loadBulletType = bullet?.bulletType,
            comparisons = bbfData.factoryComparisons,
        )

        val totalCostPer50 = AmmoCostCalculator.totalCostPer50(totalCostPerRound)
        val totalCostPer100 = AmmoCostCalculator.totalCostPer100(totalCostPerRound)
        val factoryCostPer50 = (matchedFactory?.costPerRound ?: 0.0) * 50
        val factoryCostPer100 = (matchedFactory?.costPerRound ?: 0.0) * 100

        LoadRecipeDetailUiState(
            loadRecipe = recipe,
            powder = powder,
            primer = primer,
            bullet = bullet,
            brass = brass,
            powderCostPerRound = powderCostPerRound,
            primerCostPerRound = primerCostPerRound,
            bulletCostPerRound = bulletCostPerRound,
            brassCostPerRound = brassCostPerRound,
            totalCostPerRound = totalCostPerRound,
            totalCostPer50 = totalCostPer50,
            totalCostPer100 = totalCostPer100,
            matchedFactory = matchedFactory,
            factoryCostPer50 = factoryCostPer50,
            factoryCostPer100 = factoryCostPer100,
            savingsPer50 = factoryCostPer50 - totalCostPer50,
            savingsPer100 = factoryCostPer100 - totalCostPer100,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LoadRecipeDetailUiState(),
    )

    companion object {
        fun provideFactory(
            recipeId: Long,
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
                    return LoadRecipeDetailViewModel(
                        recipeId = recipeId,
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
