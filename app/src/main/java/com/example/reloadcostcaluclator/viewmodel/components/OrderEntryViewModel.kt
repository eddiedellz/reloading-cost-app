package com.example.reloadcostcaluclator.viewmodel.components

import android.util.Log
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
    val unitPriceCents: Int,
    val lineSubtotalCents: Int,
    val allocatedExtraCents: Int,
    val adjustedLineTotalCents: Int,
    val adjustedUnitCostCents: Int,
)

data class OrderEntryComputedTotals(
    val subtotalCents: Int,
    val extraChargesCents: Int,
    val totalCents: Int,
    val lines: Map<Long, OrderEntryComputedLine>,
)

data class OrderEntryUiState(
    val purchaseDateEpochMillis: Long = System.currentTimeMillis(),
    val extraChargeMode: ExtraChargeMode = ExtraChargeMode.MANUAL_EXTRA_CHARGES,
    val allocationMethod: ExtraChargeAllocationMethod = ExtraChargeAllocationMethod.PROPORTIONAL_BY_LINE_SUBTOTAL,
    val orderTotal: String = "",
    val manualExtraCharges: String = "",
    val items: List<OrderEntryItemUi> = emptyList(),
    val computed: OrderEntryComputedTotals = OrderEntryComputedTotals(
        subtotalCents = 0,
        extraChargesCents = 0,
        totalCents = 0,
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
    private var nextId: Long = 1

    fun onExtraChargeModeChanged(value: ExtraChargeMode) = updateState { it.copy(extraChargeMode = value) }
    fun onAllocationMethodChanged(value: ExtraChargeAllocationMethod) = updateState { it.copy(allocationMethod = value) }
    fun onManualExtraChargesChanged(value: String) = updateState { it.copy(manualExtraCharges = sanitizeMoneyInput(value)) }
    fun onOrderTotalChanged(value: String) = updateState { it.copy(orderTotal = sanitizeMoneyInput(value)) }

    fun addItem(
        componentType: ComponentType,
        itemName: String,
        packageQuantity: String,
        purchaseQuantity: String,
        unitPrice: String,
    ) = updateState { state ->
        state.copy(
            items = state.items + OrderEntryItemUi(
                id = nextId++,
                itemName = itemName.trim().ifBlank { componentType.defaultItemName() },
                componentType = componentType,
                unitPrice = sanitizeMoneyInput(unitPrice),
                packageQuantity = sanitizeDecimalInput(packageQuantity),
                purchaseQuantity = sanitizeDecimalInput(purchaseQuantity),
            ),
        )
    }

    fun removeItemRow(id: Long) {
        updateState { state -> state.copy(items = state.items.filterNot { row -> row.id == id }) }
    }

    fun onItemNameChanged(id: Long, value: String) = updateItem(id) { it.copy(itemName = value) }
    fun onItemTypeChanged(id: Long, value: ComponentType) = updateItem(id) { it.copy(componentType = value) }
    fun onUnitPriceChanged(id: Long, value: String) = updateItem(id) { it.copy(unitPrice = sanitizeMoneyInput(value)) }
    fun onPackageQuantityChanged(id: Long, value: String) = updateItem(id) { it.copy(packageQuantity = sanitizeDecimalInput(value)) }
    fun onPurchaseQuantityChanged(id: Long, value: String) = updateItem(id) { it.copy(purchaseQuantity = sanitizeDecimalInput(value)) }
    fun onUpdateModeChanged(id: Long, value: ComponentUpdateMode) = updateItem(id) { it.copy(updateMode = value) }

    fun saveOrder() {
        val state = uiState.value
        val parsedLines = state.items.map { item ->
            ParsedItem(
                item = item,
                unitPriceCents = parseMoneyToCents(item.unitPrice),
                packageQuantity = parseQuantity(item.packageQuantity),
                purchaseQuantity = parseQuantity(item.purchaseQuantity),
            )
        }

        val error = when {
            parsedLines.isEmpty() -> "Add at least one item."
            parsedLines.any { it.item.itemName.isBlank() } -> "Each item needs a name."
            parsedLines.any { (it.unitPriceCents ?: 0) <= 0 } -> "Each unit price must be greater than 0."
            parsedLines.any { (it.packageQuantity ?: 0.0) <= 0.0 } -> "Each package quantity must be greater than 0."
            parsedLines.any { (it.purchaseQuantity ?: 0.0) <= 0.0 } -> "Each purchase quantity must be greater than 0."
            state.extraChargeMode == ExtraChargeMode.USE_ORDER_TOTAL && parseMoneyToCents(state.orderTotal) == null -> "Enter a valid order total."
            state.extraChargeMode == ExtraChargeMode.MANUAL_EXTRA_CHARGES && parseMoneyToCents(state.manualExtraCharges) == null -> "Enter valid manual extra charges."
            else -> null
        }

        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }

        viewModelScope.launch {
            runCatching {
                repository.createOrder(
                    purchaseDateEpochMillis = System.currentTimeMillis(),
                    extraChargeMode = state.extraChargeMode,
                    allocationMethod = state.allocationMethod,
                    totalCents = state.computed.totalCents,
                    extraChargesCents = state.computed.extraChargesCents,
                    subtotalCents = state.computed.subtotalCents,
                    items = parsedLines.map { parsed ->
                        val line = state.computed.lines.getValue(parsed.item.id)
                        CreateOrderItemInput(
                            componentType = parsed.item.componentType,
                            itemName = parsed.item.itemName.trim(),
                            unitPriceCents = parsed.unitPriceCents!!,
                            packageQuantity = parsed.packageQuantity!!,
                            purchaseQuantity = parsed.purchaseQuantity!!,
                            lineSubtotalCents = line.lineSubtotalCents,
                            allocatedExtraChargeCents = line.allocatedExtraCents,
                            originalUnitCostCents = line.unitPriceCents,
                            adjustedUnitCostCents = line.adjustedUnitCostCents,
                            adjustedLineTotalCents = line.adjustedLineTotalCents,
                            updateMode = parsed.item.updateMode,
                        )
                    },
                )
            }.onSuccess {
                _uiState.update { OrderEntryUiState(saved = true) }
            }.onFailure { errorThrowable ->
                Log.e(TAG, "Crash path: saveOrder failed during repository save/mapping.", errorThrowable)
                _uiState.update { it.copy(errorMessage = "Unable to save order. Check logs for details.") }
            }
        }
    }

    private fun ComponentType.defaultItemName(): String = when (this) {
        ComponentType.POWDER -> "Powder Item"
        ComponentType.PRIMER -> "Primer Item"
        ComponentType.BULLET -> "Bullet Item"
        ComponentType.BRASS -> "Brass Item"
        ComponentType.OTHER -> "Other Item"
    }

    private fun updateItem(id: Long, block: (OrderEntryItemUi) -> OrderEntryItemUi) {
        updateState { state ->
            state.copy(items = state.items.map { if (it.id == id) block(it) else it })
        }
    }

    private fun updateState(block: (OrderEntryUiState) -> OrderEntryUiState) {
        _uiState.update { current ->
            val updated = block(current).copy(errorMessage = null)
            updated.copy(computed = computeTotalsSafely(updated))
        }
    }

    private fun computeTotalsSafely(state: OrderEntryUiState): OrderEntryComputedTotals {
        return runCatching { computeTotals(state) }
            .onFailure { throwable ->
                Log.e(TAG, "Crash path: summary calculation failed.", throwable)
            }
            .getOrElse { OrderEntryComputedTotals(0, 0, 0, emptyMap()) }
    }

    private fun computeTotals(state: OrderEntryUiState): OrderEntryComputedTotals {
        val rawLines = state.items.map { item ->
            val unitPriceCents = parseMoneyToCents(item.unitPrice) ?: 0
            val purchaseQuantity = parseQuantity(item.purchaseQuantity) ?: 0.0
            val lineSubtotalCents = multiplyMoneyByQuantity(unitPriceCents, purchaseQuantity)
            RawComputedLine(
                id = item.id,
                unitPriceCents = unitPriceCents,
                purchaseQuantity = purchaseQuantity,
                lineSubtotalCents = lineSubtotalCents,
            )
        }

        val subtotalCents = rawLines.sumOf { it.lineSubtotalCents }
        val manualExtraCents = parseMoneyToCents(state.manualExtraCharges) ?: 0
        val enteredOrderTotalCents = parseMoneyToCents(state.orderTotal) ?: 0
        val extraChargesCents = when (state.extraChargeMode) {
            ExtraChargeMode.MANUAL_EXTRA_CHARGES -> manualExtraCents
            ExtraChargeMode.USE_ORDER_TOTAL -> enteredOrderTotalCents - subtotalCents
        }
        val totalCents = subtotalCents + extraChargesCents

        val lines = allocateExtras(
            rawLines = rawLines,
            subtotalCents = subtotalCents,
            extraChargesCents = extraChargesCents,
            method = state.allocationMethod,
        )

        return OrderEntryComputedTotals(
            subtotalCents = subtotalCents,
            extraChargesCents = extraChargesCents,
            totalCents = totalCents,
            lines = lines,
        )
    }

    private fun allocateExtras(
        rawLines: List<RawComputedLine>,
        subtotalCents: Int,
        extraChargesCents: Int,
        method: ExtraChargeAllocationMethod,
    ): Map<Long, OrderEntryComputedLine> {
        if (rawLines.isEmpty()) return emptyMap()

        val allocations = mutableMapOf<Long, Int>()
        if (extraChargesCents == 0) {
            rawLines.forEach { allocations[it.id] = 0 }
        } else {
            val totalWeight = when (method) {
                ExtraChargeAllocationMethod.PROPORTIONAL_BY_LINE_SUBTOTAL -> rawLines.sumOf { it.lineSubtotalCents.toLong() }.toDouble()
                ExtraChargeAllocationMethod.EVEN_BY_QUANTITY -> rawLines.sumOf { it.purchaseQuantity }
            }

            var allocatedSoFar = 0
            rawLines.forEachIndexed { index, line ->
                val isLast = index == rawLines.lastIndex
                val amount = if (isLast) {
                    extraChargesCents - allocatedSoFar
                } else {
                    val weight = when (method) {
                        ExtraChargeAllocationMethod.PROPORTIONAL_BY_LINE_SUBTOTAL -> line.lineSubtotalCents.toDouble()
                        ExtraChargeAllocationMethod.EVEN_BY_QUANTITY -> line.purchaseQuantity
                    }
                    if (totalWeight == 0.0) 0 else ((extraChargesCents * weight) / totalWeight).toInt()
                }
                allocations[line.id] = amount
                allocatedSoFar += amount
            }
        }

        return rawLines.associate { line ->
            val allocated = allocations[line.id] ?: 0
            val adjustedLineTotal = line.lineSubtotalCents + allocated
            val adjustedUnitCents = if (line.purchaseQuantity <= 0.0) 0 else {
                BigDecimal(adjustedLineTotal)
                    .divide(BigDecimal.valueOf(line.purchaseQuantity), 0, RoundingMode.HALF_UP)
                    .toInt()
            }

            line.id to OrderEntryComputedLine(
                id = line.id,
                unitPriceCents = line.unitPriceCents,
                lineSubtotalCents = line.lineSubtotalCents,
                allocatedExtraCents = allocated,
                adjustedLineTotalCents = adjustedLineTotal,
                adjustedUnitCostCents = adjustedUnitCents,
            )
        }
    }

    private data class RawComputedLine(
        val id: Long,
        val unitPriceCents: Int,
        val purchaseQuantity: Double,
        val lineSubtotalCents: Int,
    )

    private data class ParsedItem(
        val item: OrderEntryItemUi,
        val unitPriceCents: Int?,
        val packageQuantity: Double?,
        val purchaseQuantity: Double?,
    )

    private fun parseMoneyToCents(value: String): Int? {
        val normalized = value.trim()
        if (normalized.isEmpty() || normalized == ".") return null
        if (!normalized.matches(Regex("^\\d*\\.?\\d{0,2}$"))) return null

        val parts = normalized.split('.')
        val whole = parts[0].ifEmpty { "0" }
        val frac = parts.getOrElse(1) { "" }.padEnd(2, '0')
        val wholeCents = whole.toLongOrNull()?.times(100L) ?: return null
        val fracCents = frac.take(2).toLongOrNull() ?: return null
        val total = wholeCents + fracCents
        return total.coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()
    }

    private fun multiplyMoneyByQuantity(cents: Int, quantity: Double): Int {
        if (quantity <= 0.0) return 0
        return BigDecimal(cents)
            .multiply(BigDecimal.valueOf(quantity))
            .setScale(0, RoundingMode.HALF_UP)
            .toInt()
    }

    private fun parseQuantity(value: String): Double? {
        val normalized = value.trim()
        if (normalized.isEmpty() || normalized == ".") return null
        return normalized.toDoubleOrNull()
    }

    private fun sanitizeMoneyInput(value: String): String {
        val builder = StringBuilder()
        var hasDot = false
        var decimalCount = 0

        value.forEach { char ->
            when {
                char.isDigit() && (!hasDot || decimalCount < 2) -> {
                    builder.append(char)
                    if (hasDot) decimalCount++
                }

                char == '.' && !hasDot -> {
                    builder.append(char)
                    hasDot = true
                }
            }
        }
        return builder.toString()
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
        private const val TAG = "OrderEntryViewModel"

        fun provideFactory(repository: PurchaseOrderRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = OrderEntryViewModel(repository) as T
            }
    }
}
