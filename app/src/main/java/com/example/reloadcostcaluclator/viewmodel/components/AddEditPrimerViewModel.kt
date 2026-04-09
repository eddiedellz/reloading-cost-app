package com.example.reloadcostcaluclator.viewmodel.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reloadcostcaluclator.data.local.entity.PrimerEntity
import com.example.reloadcostcaluclator.data.repository.PrimerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditPrimerUiState(
    val id: Long? = null,
    val name: String = "",
    val pricePaid: String = "",
    val quantity: String = "",
    val errorMessage: String? = null,
)

class AddEditPrimerViewModel(
    private val repository: PrimerRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddEditPrimerUiState())
    val uiState: StateFlow<AddEditPrimerUiState> = _uiState.asStateFlow()

    fun load(itemId: Long?) {
        if (itemId == null || _uiState.value.id == itemId) return
        viewModelScope.launch {
            repository.getById(itemId).first()?.let { primer ->
                _uiState.value = AddEditPrimerUiState(
                    id = primer.id,
                    name = primer.name,
                    pricePaid = primer.pricePaid.toString(),
                    quantity = primer.quantity.toString(),
                )
            }
        }
    }

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun onPricePaidChanged(value: String) = _uiState.update { it.copy(pricePaid = sanitizeDecimalInput(value), errorMessage = null) }
    fun onQuantityChanged(value: String) = _uiState.update { it.copy(quantity = value, errorMessage = null) }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val parsedPricePaid = state.pricePaid.toDoubleOrNull()
        val parsedQuantity = state.quantity.toIntOrNull()
        val validationError = when {
            state.name.isBlank() -> "Name is required."
            parsedPricePaid?.let { it > 0.0 } != true -> "Price paid must be greater than 0."
            parsedQuantity?.let { it > 0 } != true -> "Quantity must be greater than 0."
            else -> null
        }
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            val entity = PrimerEntity(
                id = state.id ?: 0,
                name = state.name.trim(),
                pricePaid = parsedPricePaid ?: 0.0,
                quantity = parsedQuantity ?: 0,
            )
            if (state.id == null) repository.insert(entity) else repository.update(entity)
            onSaved()
        }
    }

    private fun sanitizeDecimalInput(value: String): String {
        val builder = StringBuilder()
        var hasDot = false
        value.forEach { char ->
            when {
                char.isDigit() -> builder.append(char)
                char == '.' && !hasDot -> {
                    builder.append(char)
                    hasDot = true
                }
            }
        }
        return builder.toString()
    }

    companion object {
        fun provideFactory(repository: PrimerRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = AddEditPrimerViewModel(repository) as T
            }
    }
}
