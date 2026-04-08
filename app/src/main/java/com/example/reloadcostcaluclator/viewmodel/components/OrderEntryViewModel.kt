package com.example.reloadcostcaluclator.viewmodel.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reloadcostcaluclator.data.local.entity.ComponentType
import com.example.reloadcostcaluclator.data.local.entity.ComponentUpdateMode
import com.example.reloadcostcaluclator.data.local.entity.ExtraChargeAllocationMethod
import com.example.reloadcostcaluclator.data.local.entity.ExtraChargeMode
import com.example.reloadcostcaluclator.data.repository.CreateOrderItemInput
import com.example.reloadcostcaluclator.data.repository.PurchaseOrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

data class OrderEntryItemUi(
    val id: Long,
    val itemName: String = "",
    val componentType: ComponentType = ComponentType.POWDER,
    val unitPrice: String = "",
    val packageQuantity: String = "",
    val purchaseQuantity: String = "",
    val updateMode: ComponentUpdateMode = ComponentUpdateMode.LATEST_PRICE,
)

data class OrderEntryComputedLine(
    val id: Long,
    val lineSubtotal: BigDecimal,
    val allocatedExtra: BigDecimal,
    val adjustedLineTotal: BigDecimal,
    val adjustedUnitCost: BigDecimal,
)

data class OrderEntryComputedTotals(
    val subtotal: BigDecimal,
    val extraCharges: BigDecimal,
    val orderTotal: BigDecimal,
    val lines: Map<Long, OrderEntryComputedLine>,
)

data class OrderEntryUiState(
    val purchaseDateEpochMillis: Long = System.currentTimeMillis(),
    val extraChargeMode: ExtraChargeMode = ExtraChargeMode.MANUAL_EXTRA_CHARGES,
    val allocationMethod: ExtraChargeAllocationMethod = ExtraChargeAllocationMethod.PROPORTIONAL_BY_LINE_SUBTOTAL,
    val orderTotal: String = "",
    val manualExtraCharges: String = "",
    val items: List<OrderEntryItemUi> = listOf(OrderEntryItemUi(id = 1L)),
    val computed: OrderEntryComputedTotals = OrderEntryComputedTotals(
        subtotal = BigDecimal.ZERO,
        extraCharges = BigDecimal.ZERO,
        orderTotal = BigDecimal.ZERO,
        lines = emptyMap(),
    ),
    val errorMessage: String? = null,
    val saved: Boolean = false,
)

class OrderEntryViewModel(
    private val repository: PurchaseOrderRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderEntryUiState())
    val uiState: StateFlow<OrderEntryUiState> = _uiState.asStateFlow()
    private var nextId: Long = 2

    fun onExtraChargeModeChanged(value: ExtraChargeMode) = updateState { it.copy(extraChargeMode = value) }
    fun onAllocationMethodChanged(value: ExtraChargeAllocationMethod) = updateState { it.copy(allocationMethod = value) }
    fun onManualExtraChargesChanged(value: String) = updateState { it.copy(manualExtraCharges = value) }
    fun onOrderTotalChanged(value: String) = updateState { it.copy(orderTotal = value) }

    fun addItemRow() = updateState { it.copy(items = it.items + OrderEntryItemUi(id = nextId++)) }

    fun removeItemRow(id: Long) {
        updateState {
            val remaining = it.items.filterNot { row -> row.id == id }
            it.copy(items = if (remaining.isEmpty()) listOf(OrderEntryItemUi(id = nextId++)) else remaining)
        }
    }

    fun onItemNameChanged(id: Long, value: String) = updateItem(id) { it.copy(itemName = value) }
    fun onItemTypeChanged(id: Long, value: ComponentType) = updateItem(id) { it.copy(componentType = value) }
    fun onUnitPriceChanged(id: Long, value: String) = updateItem(id) { it.copy(unitPrice = value) }
    fun onPackageQuantityChanged(id: Long, value: String) = updateItem(id) { it.copy(packageQuantity = value) }
    fun onPurchaseQuantityChanged(id: Long, value: String) = updateItem(id) { it.copy(purchaseQuantity = value) }
    fun onUpdateModeChanged(id: Long, value: ComponentUpdateMode) = updateItem(id) { it.copy(updateMode = value) }

    fun saveOrder() {
        val state = uiState.value
        val parsedLines = state.items.map { item ->
            ParsedItem(
                item = item,
                unitPrice = item.unitPrice.toBigDecimalOrNull(),
                packageQuantity = item.packageQuantity.toBigDecimalOrNull(),
                purchaseQuantity = item.purchaseQuantity.toBigDecimalOrNull(),
            )
        }
        val error = when {
            parsedLines.isEmpty() -> "Add at least one item."
            parsedLines.any { it.item.itemName.isBlank() } -> "Each item needs a name."
            parsedLines.any { (it.unitPrice ?: BigDecimal.ZERO) <= BigDecimal.ZERO } -> "Each unit price must be greater than 0."
            parsedLines.any { (it.packageQuantity ?: BigDecimal.ZERO) <= BigDecimal.ZERO } -> "Each package quantity must be greater than 0."
            parsedLines.any { (it.purchaseQuantity ?: BigDecimal.ZERO) <= BigDecimal.ZERO } -> "Each purchase quantity must be greater than 0."
            state.extraChargeMode == ExtraChargeMode.USE_ORDER_TOTAL && state.orderTotal.toBigDecimalOrNull() == null -> "Enter a valid order total."
            state.extraChargeMode == ExtraChargeMode.MANUAL_EXTRA_CHARGES && state.manualExtraCharges.toBigDecimalOrNull() == null -> "Enter valid manual extra charges."
            else -> null
        }

        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }

        viewModelScope.launch {
            repository.createOrder(
                purchaseDateEpochMillis = System.currentTimeMillis(),
                extraChargeMode = state.extraChargeMode,
                allocationMethod = state.allocationMethod,
                orderTotal = state.computed.orderTotal.toDouble(),
                extraChargesTotal = state.computed.extraCharges.toDouble(),
                subtotal = state.computed.subtotal.toDouble(),
                items = parsedLines.map { parsed ->
                    val line = state.computed.lines.getValue(parsed.item.id)
                    CreateOrderItemInput(
                        componentType = parsed.item.componentType,
                        itemName = parsed.item.itemName.trim(),
                        unitPrice = parsed.unitPrice!!.toDouble(),
                        packageQuantity = parsed.packageQuantity!!.toDouble(),
                        purchaseQuantity = parsed.purchaseQuantity!!.toDouble(),
                        lineSubtotal = line.lineSubtotal.toDouble(),
                        allocatedExtraCharge = line.allocatedExtra.toDouble(),
                        originalUnitCost = parsed.unitPrice.toDouble(),
                        adjustedUnitCost = line.adjustedUnitCost.toDouble(),
                        adjustedLineTotal = line.adjustedLineTotal.toDouble(),
                        updateMode = parsed.item.updateMode,
                    )
                },
            )
            _uiState.update { OrderEntryUiState(saved = true) }
        }
    }

    private fun updateItem(id: Long, block: (OrderEntryItemUi) -> OrderEntryItemUi) {
        updateState { state ->
            state.copy(items = state.items.map { if (it.id == id) block(it) else it })
        }
    }

    private fun updateState(block: (OrderEntryUiState) -> OrderEntryUiState) {
        _uiState.update { current ->
            val updated = block(current).copy(errorMessage = null)
            updated.copy(computed = computeTotals(updated))
        }
    }

    private fun computeTotals(state: OrderEntryUiState): OrderEntryComputedTotals {
        val rawLines = state.items.map { item ->
            val unitPrice = item.unitPrice.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val purchaseQuantity = item.purchaseQuantity.toBigDecimalOrNull() ?: BigDecimal.ZERO
            Triple(item.id, purchaseQuantity, unitPrice.multiply(purchaseQuantity))
        }
        val subtotal = rawLines.fold(BigDecimal.ZERO) { acc, (_, _, lineSubtotal) -> acc + lineSubtotal }

        val manualExtra = state.manualExtraCharges.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val enteredOrderTotal = state.orderTotal.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val extraCharges = when (state.extraChargeMode) {
            ExtraChargeMode.MANUAL_EXTRA_CHARGES -> manualExtra
            ExtraChargeMode.USE_ORDER_TOTAL -> enteredOrderTotal - subtotal
        }
        val orderTotal = subtotal + extraCharges

        val extraCents = extraCharges.setScale(2, RoundingMode.HALF_UP)
        val lineSubtotalCents = rawLines.associate { (id, _, line) -> id to line.setScale(2, RoundingMode.HALF_UP) }
        val lines = allocateExtras(
            rawLines = rawLines,
            subtotal = subtotal,
            extraCharges = extraCents,
            method = state.allocationMethod,
            lineSubtotalCents = lineSubtotalCents,
        )

        return OrderEntryComputedTotals(
            subtotal = subtotal.setScale(2, RoundingMode.HALF_UP),
            extraCharges = extraCents,
            orderTotal = orderTotal.setScale(2, RoundingMode.HALF_UP),
            lines = lines,
        )
    }

    private fun allocateExtras(
        rawLines: List<Triple<Long, BigDecimal, BigDecimal>>,
        subtotal: BigDecimal,
        extraCharges: BigDecimal,
        method: ExtraChargeAllocationMethod,
        lineSubtotalCents: Map<Long, BigDecimal>,
    ): Map<Long, OrderEntryComputedLine> {
        if (rawLines.isEmpty()) return emptyMap()

        val ids = rawLines.map { it.first }
        val allocations = mutableMapOf<Long, BigDecimal>()

        if (extraCharges == BigDecimal.ZERO) {
            ids.forEach { allocations[it] = BigDecimal.ZERO }
        } else {
            when (method) {
                ExtraChargeAllocationMethod.PROPORTIONAL_BY_LINE_SUBTOTAL -> {
                    rawLines.forEach { (id, _, lineSubtotal) ->
                        val ratio = if (subtotal == BigDecimal.ZERO) BigDecimal.ZERO else lineSubtotal.divide(subtotal, 12, RoundingMode.HALF_UP)
                        allocations[id] = extraCharges.multiply(ratio).setScale(2, RoundingMode.HALF_UP)
                    }
                }

                ExtraChargeAllocationMethod.EVEN_BY_QUANTITY -> {
                    val totalQty = rawLines.fold(BigDecimal.ZERO) { acc, (_, qty, _) -> acc + qty }
                    rawLines.forEach { (id, qty, _) ->
                        val ratio = if (totalQty == BigDecimal.ZERO) BigDecimal.ZERO else qty.divide(totalQty, 12, RoundingMode.HALF_UP)
                        allocations[id] = extraCharges.multiply(ratio).setScale(2, RoundingMode.HALF_UP)
                    }
                }
            }

            val allocatedSum = allocations.values.fold(BigDecimal.ZERO) { acc, value -> acc + value }
            val remainder = extraCharges - allocatedSum
            val lastId = ids.last()
            allocations[lastId] = (allocations[lastId] ?: BigDecimal.ZERO) + remainder
        }

        return rawLines.associate { (id, qty, _) ->
            val lineSubtotalRounded = lineSubtotalCents[id] ?: BigDecimal.ZERO
            val allocated = allocations[id] ?: BigDecimal.ZERO
            val adjustedLineTotal = lineSubtotalRounded + allocated
            val adjustedUnitCost = if (qty == BigDecimal.ZERO) BigDecimal.ZERO else adjustedLineTotal.divide(qty, 6, RoundingMode.HALF_UP)
            id to OrderEntryComputedLine(
                id = id,
                lineSubtotal = lineSubtotalRounded,
                allocatedExtra = allocated.setScale(2, RoundingMode.HALF_UP),
                adjustedLineTotal = adjustedLineTotal.setScale(2, RoundingMode.HALF_UP),
                adjustedUnitCost = adjustedUnitCost,
            )
        }
    }

    private data class ParsedItem(
        val item: OrderEntryItemUi,
        val unitPrice: BigDecimal?,
        val packageQuantity: BigDecimal?,
        val purchaseQuantity: BigDecimal?,
    )

    companion object {
        fun provideFactory(repository: PurchaseOrderRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = OrderEntryViewModel(repository) as T
            }
    }
}
