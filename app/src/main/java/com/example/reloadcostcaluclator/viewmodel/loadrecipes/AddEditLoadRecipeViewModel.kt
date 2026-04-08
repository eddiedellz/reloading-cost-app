package com.example.reloadcostcaluclator.viewmodel.loadrecipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reloadcostcaluclator.data.local.entity.BrassEntity
import com.example.reloadcostcaluclator.data.local.entity.BulletEntity
import com.example.reloadcostcaluclator.data.local.entity.LoadRecipeEntity
import com.example.reloadcostcaluclator.data.local.entity.PowderEntity
import com.example.reloadcostcaluclator.data.local.entity.PrimerEntity
import com.example.reloadcostcaluclator.data.repository.BrassRepository
import com.example.reloadcostcaluclator.data.repository.BulletRepository
import com.example.reloadcostcaluclator.data.repository.LoadRecipeRepository
import com.example.reloadcostcaluclator.data.repository.PowderRepository
import com.example.reloadcostcaluclator.data.repository.PrimerRepository
import com.example.reloadcostcaluclator.util.AmmoCostCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditLoadRecipeUiState(
    val id: Long? = null,
    val name: String = "",
    val caliber: String = "",
    val powderId: Long? = null,
    val chargeWeightGr: String = "",
    val primerId: Long? = null,
    val bulletId: Long? = null,
    val brassId: Long? = null,
    val notes: String = "",
    val errorMessage: String? = null,
)

data class LoadRecipeCostPreview(
    val powderCostPerRound: Double = 0.0,
    val primerCostPerRound: Double = 0.0,
    val bulletCostPerRound: Double = 0.0,
    val brassCostPerRound: Double = 0.0,
    val totalCostPerRound: Double = 0.0,
    val totalCostPer50: Double = 0.0,
)

class AddEditLoadRecipeViewModel(
    private val loadRecipeRepository: LoadRecipeRepository,
    powderRepository: PowderRepository,
    primerRepository: PrimerRepository,
    bulletRepository: BulletRepository,
    brassRepository: BrassRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddEditLoadRecipeUiState())
    val uiState: StateFlow<AddEditLoadRecipeUiState> = _uiState.asStateFlow()

    val powders: StateFlow<List<PowderEntity>> = powderRepository.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val primers: StateFlow<List<PrimerEntity>> = primerRepository.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val bullets: StateFlow<List<BulletEntity>> = bulletRepository.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val brass: StateFlow<List<BrassEntity>> = brassRepository.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val costPreview: StateFlow<LoadRecipeCostPreview> = combine(
        _uiState,
        powders,
        primers,
        bullets,
        brass,
    ) { state, powderList, primerList, bulletList, brassList ->
        val chargeWeight = state.chargeWeightGr.toDoubleOrNull() ?: 0.0

        val selectedPowder = powderList.firstOrNull { it.id == state.powderId }
        val selectedPrimer = primerList.firstOrNull { it.id == state.primerId }
        val selectedBullet = bulletList.firstOrNull { it.id == state.bulletId }
        val selectedBrass = brassList.firstOrNull { it.id == state.brassId }

        val powderCostPerRound = AmmoCostCalculator.powderCostPerRound(
            powderPrice = selectedPowder?.pricePaid ?: 0.0,
            containerWeightLb = selectedPowder?.containerWeightLb ?: 0.0,
            chargeWeightGr = chargeWeight,
        )
        val primerCostPerRound = AmmoCostCalculator.primerCostPerRound(
            primerPrice = selectedPrimer?.pricePaid ?: 0.0,
            primerQuantity = selectedPrimer?.quantity ?: 0,
        )
        val bulletCostPerRound = AmmoCostCalculator.bulletCostPerRound(
            bulletPrice = selectedBullet?.pricePaid ?: 0.0,
            bulletQuantity = selectedBullet?.quantity ?: 0,
        )
        val brassCostPerRound = AmmoCostCalculator.brassCostPerRound(
            brassPrice = selectedBrass?.pricePaid ?: 0.0,
            brassQuantity = selectedBrass?.quantity ?: 0,
            reloadCount = selectedBrass?.reloadCount ?: 0,
        )
        val totalCostPerRound = AmmoCostCalculator.totalCostPerRound(
            powderCost = powderCostPerRound,
            primerCost = primerCostPerRound,
            bulletCost = bulletCostPerRound,
            brassCost = brassCostPerRound,
        )

        LoadRecipeCostPreview(
            powderCostPerRound = powderCostPerRound,
            primerCostPerRound = primerCostPerRound,
            bulletCostPerRound = bulletCostPerRound,
            brassCostPerRound = brassCostPerRound,
            totalCostPerRound = totalCostPerRound,
            totalCostPer50 = AmmoCostCalculator.totalCostPer50(totalCostPerRound),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LoadRecipeCostPreview(),
    )

    fun load(itemId: Long?) {
        if (itemId == null || _uiState.value.id == itemId) return
        viewModelScope.launch {
            loadRecipeRepository.getById(itemId).first()?.let { recipe ->
                _uiState.value = AddEditLoadRecipeUiState(
                    id = recipe.id,
                    name = recipe.name,
                    caliber = recipe.caliber,
                    powderId = recipe.powderId,
                    chargeWeightGr = recipe.chargeWeightGr.toString(),
                    primerId = recipe.primerId,
                    bulletId = recipe.bulletId,
                    brassId = recipe.brassId,
                    notes = recipe.notes,
                )
            }
        }
    }

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun onCaliberChanged(value: String) = _uiState.update { it.copy(caliber = value, errorMessage = null) }
    fun onChargeWeightChanged(value: String) = _uiState.update { it.copy(chargeWeightGr = value, errorMessage = null) }
    fun onNotesChanged(value: String) = _uiState.update { it.copy(notes = value, errorMessage = null) }

    fun onPowderChanged(value: Long?) = _uiState.update { it.copy(powderId = value, errorMessage = null) }
    fun onPrimerChanged(value: Long?) = _uiState.update { it.copy(primerId = value, errorMessage = null) }
    fun onBulletChanged(value: Long?) = _uiState.update { it.copy(bulletId = value, errorMessage = null) }
    fun onBrassChanged(value: Long?) = _uiState.update { it.copy(brassId = value, errorMessage = null) }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val chargeWeight = state.chargeWeightGr.toDoubleOrNull()

        val validationError = when {
            state.name.trim().isBlank() -> "Load name is required."
            state.caliber.trim().isBlank() -> "Caliber is required."
            state.powderId == null -> "Powder is required."
            chargeWeight == null || chargeWeight <= 0.0 -> "Charge weight must be greater than 0."
            state.primerId == null -> "Primer is required."
            state.bulletId == null -> "Bullet is required."
            state.brassId == null -> "Brass is required."
            else -> null
        }

        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            val entity = LoadRecipeEntity(
                id = state.id ?: 0,
                name = state.name.trim(),
                caliber = state.caliber.trim(),
                powderId = state.powderId,
                chargeWeightGr = chargeWeight!!,
                primerId = state.primerId,
                bulletId = state.bulletId,
                brassId = state.brassId,
                notes = state.notes.trim(),
            )
            if (state.id == null) loadRecipeRepository.insert(entity) else loadRecipeRepository.update(entity)
            onSaved()
        }
    }

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
                    return AddEditLoadRecipeViewModel(
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
