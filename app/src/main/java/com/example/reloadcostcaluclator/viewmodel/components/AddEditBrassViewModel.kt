package com.example.reloadcostcaluclator.viewmodel.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reloadcostcaluclator.data.local.entity.BrassEntity
import com.example.reloadcostcaluclator.data.repository.BrassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditBrassUiState(
    val id: Long? = null,
    val name: String = "",
    val pricePaid: String = "",
    val quantity: String = "",
    val reloadCount: String = "",
    val errorMessage: String? = null,
)

class AddEditBrassViewModel(
    private val repository: BrassRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddEditBrassUiState())
    val uiState: StateFlow<AddEditBrassUiState> = _uiState.asStateFlow()

    fun load(itemId: Long?) {
        if (itemId == null || _uiState.value.id == itemId) return
        viewModelScope.launch {
            repository.getById(itemId).first()?.let { brass ->
                _uiState.value = AddEditBrassUiState(
                    id = brass.id,
                    name = brass.name,
                    pricePaid = brass.pricePaid.toString(),
                    quantity = brass.quantity.toString(),
                    reloadCount = brass.reloadCount.toString(),
                )
            }
        }
    }

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun onPricePaidChanged(value: String) = _uiState.update { it.copy(pricePaid = value, errorMessage = null) }
    fun onQuantityChanged(value: String) = _uiState.update { it.copy(quantity = value, errorMessage = null) }
    fun onReloadCountChanged(value: String) = _uiState.update { it.copy(reloadCount = value, errorMessage = null) }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val parsedPricePaid = state.pricePaid.toDoubleOrNull()
        val pricePaid = parsedPricePaid ?: 0.0

        val validationError = when {
            state.name.isBlank() -> "Name is required."
            state.quantity.toIntOrNull()?.let { it > 0 } != true -> "Quantity must be greater than 0."
            state.reloadCount.toIntOrNull()?.let { it > 0 } != true -> "Reload count must be greater than 0."
            state.pricePaid.isNotBlank() && parsedPricePaid == null -> "Price paid must be a valid number."
            pricePaid < 0.0 -> "Price paid must be 0 or greater."
            else -> null
        }
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            val entity = BrassEntity(
                id = state.id ?: 0,
                name = state.name.trim(),
                pricePaid = pricePaid,
                quantity = state.quantity.toInt(),
                reloadCount = state.reloadCount.toInt(),
            )
            if (state.id == null) repository.insert(entity) else repository.update(entity)
            onSaved()
        }
    }

    companion object {
        fun provideFactory(repository: BrassRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = AddEditBrassViewModel(repository) as T
            }
    }
}
