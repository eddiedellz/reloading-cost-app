package com.example.reloadcostcaluclator.viewmodel.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reloadcostcaluclator.data.local.entity.ComponentType
import com.example.reloadcostcaluclator.data.local.entity.ComponentUpdateMode
import com.example.reloadcostcaluclator.data.repository.CreateOrderItemInput
import com.example.reloadcostcaluclator.data.repository.PurchaseOrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrderEntryItemUi(
    val id: Long,
    val itemName: String = "",
    val componentType: ComponentType = ComponentType.POWDER,
    val quantityOrPackageSize: String = "",
    val basePrice: String = "",
    val updateMode: ComponentUpdateMode = ComponentUpdateMode.LATEST_PRICE,
)

data class OrderEntryUiState(
    val purchaseDateEpochMillis: Long = System.currentTimeMillis(),
    val extraChargesTotal: String = "",
    val items: List<OrderEntryItemUi> = listOf(OrderEntryItemUi(id = 1L)),
    val errorMessage: String? = null,
    val saved: Boolean = false,
)

class OrderEntryViewModel(
    private val repository: PurchaseOrderRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderEntryUiState())
    val uiState: StateFlow<OrderEntryUiState> = _uiState.asStateFlow()
    private var nextId: Long = 2

    fun onExtraChargesChanged(value: String) = _uiState.update { it.copy(extraChargesTotal = value, errorMessage = null) }

    fun addItemRow() {
        _uiState.update {
            it.copy(items = it.items + OrderEntryItemUi(id = nextId++), errorMessage = null)
        }
    }

    fun removeItemRow(id: Long) {
        _uiState.update {
            val remaining = it.items.filterNot { item -> item.id == id }
            it.copy(items = if (remaining.isEmpty()) listOf(OrderEntryItemUi(id = nextId++)) else remaining)
        }
    }

    fun onItemNameChanged(id: Long, value: String) = updateItem(id) { it.copy(itemName = value) }
    fun onItemTypeChanged(id: Long, value: ComponentType) = updateItem(id) { it.copy(componentType = value) }
    fun onQuantityChanged(id: Long, value: String) = updateItem(id) { it.copy(quantityOrPackageSize = value) }
    fun onBasePriceChanged(id: Long, value: String) = updateItem(id) { it.copy(basePrice = value) }
    fun onUpdateModeChanged(id: Long, value: ComponentUpdateMode) = updateItem(id) { it.copy(updateMode = value) }

    fun saveOrder() {
        val extraCharges = uiState.value.extraChargesTotal.toDoubleOrNull() ?: 0.0
        val parsed = uiState.value.items.map { item ->
            val quantity = item.quantityOrPackageSize.toDoubleOrNull()
            val base = item.basePrice.toDoubleOrNull()
            Triple(item, quantity, base)
        }
        val error = when {
            parsed.isEmpty() -> "Add at least one item."
            parsed.any { it.first.itemName.isBlank() } -> "Each item needs a name."
            parsed.any { it.second == null || it.second <= 0.0 } -> "Each quantity/package size must be greater than 0."
            parsed.any { it.third == null || it.third <= 0.0 } -> "Each base price must be greater than 0."
            extraCharges < 0.0 -> "Extra charges must be 0 or greater."
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }

        viewModelScope.launch {
            repository.createOrder(
                purchaseDateEpochMillis = System.currentTimeMillis(),
                extraChargesTotal = extraCharges,
                items = parsed.map {
                    CreateOrderItemInput(
                        componentType = it.first.componentType,
                        itemName = it.first.itemName.trim(),
                        quantityOrPackageSize = it.second!!,
                        basePrice = it.third!!,
                        updateMode = it.first.updateMode,
                    )
                },
            )
            _uiState.update { OrderEntryUiState(saved = true) }
        }
    }

    private fun updateItem(id: Long, block: (OrderEntryItemUi) -> OrderEntryItemUi) {
        _uiState.update { state ->
            state.copy(
                items = state.items.map { if (it.id == id) block(it) else it },
                errorMessage = null,
            )
        }
    }

    companion object {
        fun provideFactory(repository: PurchaseOrderRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = OrderEntryViewModel(repository) as T
            }
    }
}
