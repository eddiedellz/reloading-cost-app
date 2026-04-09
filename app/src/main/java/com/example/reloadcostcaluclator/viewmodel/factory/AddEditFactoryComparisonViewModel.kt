package com.example.reloadcostcaluclator.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reloadcostcaluclator.data.local.entity.FactoryComparisonEntity
import com.example.reloadcostcaluclator.data.repository.FactoryComparisonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditFactoryComparisonUiState(
    val id: Long? = null,
    val brand: String = "",
    val productName: String = "",
    val caliber: String = "",
    val grain: String = "",
    val bulletType: String = "",
    val boxQuantity: String = "50",
    val totalPrice: String = "",
    val notes: String = "",
    val errorMessage: String? = null,
)

class AddEditFactoryComparisonViewModel(
    private val repository: FactoryComparisonRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddEditFactoryComparisonUiState())
    val uiState: StateFlow<AddEditFactoryComparisonUiState> = _uiState.asStateFlow()

    fun load(itemId: Long?) {
        if (itemId == null || _uiState.value.id == itemId) return
        viewModelScope.launch {
            repository.getById(itemId).first()?.let { item ->
                _uiState.value = AddEditFactoryComparisonUiState(
                    id = item.id,
                    brand = item.brand,
                    productName = item.productName,
                    caliber = item.caliber,
                    grain = item.grain.toString(),
                    bulletType = item.bulletType.orEmpty(),
                    boxQuantity = item.boxQuantity.toString(),
                    totalPrice = item.totalPrice.toString(),
                    notes = item.notes,
                )
            }
        }
    }

    fun onBrandChanged(value: String) = _uiState.update { it.copy(brand = value, errorMessage = null) }
    fun onProductNameChanged(value: String) = _uiState.update { it.copy(productName = value, errorMessage = null) }
    fun onCaliberChanged(value: String) = _uiState.update { it.copy(caliber = value, errorMessage = null) }
    fun onGrainChanged(value: String) = _uiState.update { it.copy(grain = value, errorMessage = null) }
    fun onBulletTypeChanged(value: String) = _uiState.update { it.copy(bulletType = value, errorMessage = null) }
    fun onBoxQuantityChanged(value: String) = _uiState.update { it.copy(boxQuantity = value, errorMessage = null) }
    fun onTotalPriceChanged(value: String) = _uiState.update { it.copy(totalPrice = value, errorMessage = null) }
    fun onNotesChanged(value: String) = _uiState.update { it.copy(notes = value, errorMessage = null) }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val brand = state.brand.trim()
        val productName = state.productName.trim()
        val caliber = state.caliber.trim()
        val grain = state.grain.trim().toIntOrNull()
        val boxQuantity = state.boxQuantity.trim().toIntOrNull()
        val totalPrice = state.totalPrice.trim().toDoubleOrNull()

        if (brand.isBlank() || productName.isBlank() || caliber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Brand, product, and caliber are required.") }
            return
        }
        if (grain == null || grain <= 0) {
            _uiState.update { it.copy(errorMessage = "Grain must be a positive whole number.") }
            return
        }
        if (boxQuantity == null || boxQuantity <= 0) {
            _uiState.update { it.copy(errorMessage = "Box quantity must be greater than 0.") }
            return
        }
        if (totalPrice == null || totalPrice <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Total price must be greater than 0.") }
            return
        }

        val now = System.currentTimeMillis()
        val costPerRound = totalPrice / boxQuantity.toDouble()

        viewModelScope.launch {
            val base = FactoryComparisonEntity(
                id = state.id ?: 0,
                brand = brand,
                productName = productName,
                caliber = caliber,
                grain = grain,
                bulletType = state.bulletType.trim().ifBlank { null },
                boxQuantity = boxQuantity,
                totalPrice = totalPrice,
                costPerRound = costPerRound,
                notes = state.notes.trim(),
                createdAtEpochMillis = now,
                updatedAtEpochMillis = now,
            )
            if (state.id == null) {
                repository.insert(base)
            } else {
                val existing = repository.getById(state.id).first()
                repository.update(base.copy(createdAtEpochMillis = existing?.createdAtEpochMillis ?: now))
            }
            onSaved()
        }
    }

    companion object {
        fun provideFactory(repository: FactoryComparisonRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddEditFactoryComparisonViewModel(repository) as T
                }
            }
    }
}
