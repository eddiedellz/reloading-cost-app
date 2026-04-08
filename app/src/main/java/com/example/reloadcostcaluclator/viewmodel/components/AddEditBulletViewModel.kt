package com.example.reloadcostcaluclator.viewmodel.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reloadcostcaluclator.data.local.entity.BulletEntity
import com.example.reloadcostcaluclator.data.repository.BulletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditBulletUiState(
    val id: Long? = null,
    val name: String = "",
    val pricePaid: String = "",
    val quantity: String = "",
    val errorMessage: String? = null,
)

class AddEditBulletViewModel(
    private val repository: BulletRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddEditBulletUiState())
    val uiState: StateFlow<AddEditBulletUiState> = _uiState.asStateFlow()

    fun load(itemId: Long?) {
        if (itemId == null || _uiState.value.id == itemId) return
        viewModelScope.launch {
            repository.getById(itemId).first()?.let { bullet ->
                _uiState.value = AddEditBulletUiState(
                    id = bullet.id,
                    name = bullet.name,
                    pricePaid = bullet.pricePaid.toString(),
                    quantity = bullet.quantity.toString(),
                )
            }
        }
    }

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun onPricePaidChanged(value: String) = _uiState.update { it.copy(pricePaid = value, errorMessage = null) }
    fun onQuantityChanged(value: String) = _uiState.update { it.copy(quantity = value, errorMessage = null) }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val validationError = when {
            state.name.isBlank() -> "Name is required."
            state.pricePaid.toDoubleOrNull()?.let { it > 0.0 } != true -> "Price paid must be greater than 0."
            state.quantity.toIntOrNull()?.let { it > 0 } != true -> "Quantity must be greater than 0."
            else -> null
        }
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            val entity = BulletEntity(
                id = state.id ?: 0,
                name = state.name.trim(),
                pricePaid = state.pricePaid.toDouble(),
                quantity = state.quantity.toInt(),
            )
            if (state.id == null) repository.insert(entity) else repository.update(entity)
            onSaved()
        }
    }

    companion object {
        fun provideFactory(repository: BulletRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = AddEditBulletViewModel(repository) as T
            }
    }
}
