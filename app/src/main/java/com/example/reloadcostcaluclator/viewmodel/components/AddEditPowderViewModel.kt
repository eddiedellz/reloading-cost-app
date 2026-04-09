package com.example.reloadcostcaluclator.viewmodel.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reloadcostcaluclator.data.local.entity.PowderEntity
import com.example.reloadcostcaluclator.data.repository.PowderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditPowderUiState(
    val id: Long? = null,
    val name: String = "",
    val pricePaid: String = "",
    val containerWeightLb: String = "",
    val errorMessage: String? = null,
)

class AddEditPowderViewModel(
    private val repository: PowderRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddEditPowderUiState())
    val uiState: StateFlow<AddEditPowderUiState> = _uiState.asStateFlow()

    fun load(itemId: Long?) {
        if (itemId == null || _uiState.value.id == itemId) return
        viewModelScope.launch {
            repository.getById(itemId).first()?.let { powder ->
                _uiState.value = AddEditPowderUiState(
                    id = powder.id,
                    name = powder.name,
                    pricePaid = powder.pricePaid.toString(),
                    containerWeightLb = powder.containerWeightLb.toString(),
                )
            }
        }
    }

    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun onPricePaidChanged(value: String) = _uiState.update { it.copy(pricePaid = sanitizeDecimalInput(value), errorMessage = null) }
    fun onContainerWeightChanged(value: String) = _uiState.update { it.copy(containerWeightLb = value, errorMessage = null) }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val name = state.name.trim()

        val price = state.pricePaid.trim().toDoubleOrNull()
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Name is required.") }
            return
        }
        if (price == null || price <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Price paid must be greater than 0.") }
            return
        }

        val containerWeight = state.containerWeightLb.trim().toDoubleOrNull()
        if (containerWeight == null || containerWeight <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Container weight must be greater than 0.") }
            return
        }

        viewModelScope.launch {
            val entity = PowderEntity(
                id = state.id ?: 0,
                name = name,
                pricePaid = price,
                containerWeightLb = containerWeight,
            )
            if (state.id == null) repository.insert(entity) else repository.update(entity)
            onSaved()
        }
    }

    companion object {
        fun provideFactory(repository: PowderRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddEditPowderViewModel(repository) as T
                }
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
}
