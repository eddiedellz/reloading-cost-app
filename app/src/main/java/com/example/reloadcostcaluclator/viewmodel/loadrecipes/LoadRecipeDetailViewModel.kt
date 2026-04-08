package com.example.reloadcostcaluclator.viewmodel.loadrecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reloadcostcaluclator.data.local.entity.BrassEntity
import com.example.reloadcostcaluclator.data.local.entity.BulletEntity
import com.example.reloadcostcaluclator.data.local.entity.LoadRecipeEntity
import com.example.reloadcostcaluclator.data.local.entity.PowderEntity
import com.example.reloadcostcaluclator.data.local.entity.PrimerEntity
import com.example.reloadcostcaluclator.data.reference.FactoryAmmoReferenceRepository
import com.example.reloadcostcaluclator.data.repository.BrassRepository
import com.example.reloadcostcaluclator.data.repository.BulletRepository
import com.example.reloadcostcaluclator.data.repository.LoadRecipeRepository
import com.example.reloadcostcaluclator.data.repository.PowderRepository
import com.example.reloadcostcaluclator.data.repository.PrimerRepository
import com.example.reloadcostcaluclator.model.FactoryAmmoComparison
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
    val factoryComparisons: List<FactoryAmmoComparison> = emptyList(),
)

class LoadRecipeDetailViewModel(
    recipeId: Long,
    loadRecipeRepository: LoadRecipeRepository,
    powderRepository: PowderRepository,
    primerRepository: PrimerRepository,
    bulletRepository: BulletRepository,
    brassRepository: BrassRepository,
    factoryAmmoReferenceRepository: FactoryAmmoReferenceRepository,
) : ViewModel() {

    val uiState: StateFlow<LoadRecipeDetailUiState> = combine(
        loadRecipeRepository.getById(recipeId),
        powderRepository.getAll(),
        primerRepository.getAll(),
        bulletRepository.getAll(),
        brassRepository.getAll(),
        factoryAmmoReferenceRepository.getAll(),
    ) { recipe, powders, primers, bullets, brassList, factoryReferences ->
        if (recipe == null) {
            return@combine LoadRecipeDetailUiState()
        }

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

        val totalCostPerRound = AmmoCostCalculator.totalCostPerRound(
            powderCost = powderCostPerRound,
            primerCost = primerCostPerRound,
            bulletCost = bulletCostPerRound,
            brassCost = brassCostPerRound,
        )

        val factoryComparisons = FactoryAmmoComparisonCalculator.compare(
            loadCaliber = recipe.caliber,
            loadGrain = bullet?.grain,
            loadBulletType = bullet?.bulletType,
            loadCostPerRound = totalCostPerRound,
            references = factoryReferences,
        )

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
            totalCostPer50 = AmmoCostCalculator.totalCostPer50(totalCostPerRound),
            factoryComparisons = factoryComparisons,
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
            factoryAmmoReferenceRepository: FactoryAmmoReferenceRepository,
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
                        factoryAmmoReferenceRepository = factoryAmmoReferenceRepository,
                    ) as T
                }
            }
    }
}
