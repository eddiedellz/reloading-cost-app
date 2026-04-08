package com.example.reloadcostcaluclator.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reloadcostcaluclator.data.local.entity.ComponentType
import com.example.reloadcostcaluclator.data.local.entity.ComponentUpdateMode
import com.example.reloadcostcaluclator.data.local.entity.ExtraChargeAllocationMethod
import com.example.reloadcostcaluclator.data.local.entity.ExtraChargeMode
import com.example.reloadcostcaluclator.data.repository.PurchaseOrderRepository
import com.example.reloadcostcaluclator.ui.components.DecimalNumberInputField
import com.example.reloadcostcaluclator.ui.components.TextInputField
import com.example.reloadcostcaluclator.util.CurrencyFormatters
import com.example.reloadcostcaluclator.viewmodel.components.OrderEntryItemUi
import com.example.reloadcostcaluclator.viewmodel.components.OrderEntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderEntryScreen(
    repository: PurchaseOrderRepository,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    viewModel: OrderEntryViewModel = viewModel(factory = OrderEntryViewModel.provideFactory(repository)),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.value.saved) onSaved()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Purchase Order") },
                navigationIcon = { TextButton(onClick = onBackClick) { Text("Back") } },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                ExtraChargeModeCard(
                    mode = uiState.value.extraChargeMode,
                    manualExtraCharges = uiState.value.manualExtraCharges,
                    orderTotal = uiState.value.orderTotal,
                    onModeChanged = viewModel::onExtraChargeModeChanged,
                    onManualExtraChargesChanged = viewModel::onManualExtraChargesChanged,
                    onOrderTotalChanged = viewModel::onOrderTotalChanged,
                )
            }

            item {
                EnumDropdown(
                    label = "Allocation method",
                    options = ExtraChargeAllocationMethod.entries,
                    selected = uiState.value.allocationMethod,
                    onSelected = viewModel::onAllocationMethodChanged,
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Subtotal: ${CurrencyFormatters.formatUsd(uiState.value.computed.subtotal.toDouble())}")
                        Text("Extra charges: ${CurrencyFormatters.formatUsd(uiState.value.computed.extraCharges.toDouble())}")
                        Text("Order total: ${CurrencyFormatters.formatUsd(uiState.value.computed.orderTotal.toDouble())}")
                    }
                }
            }

            items(uiState.value.items, key = { it.id }) { item ->
                val computedLine = uiState.value.computed.lines[item.id]
                OrderItemCard(
                    item = item,
                    lineSubtotal = computedLine?.lineSubtotal?.toDouble() ?: 0.0,
                    allocatedExtra = computedLine?.allocatedExtra?.toDouble() ?: 0.0,
                    adjustedUnitCost = computedLine?.adjustedUnitCost?.toDouble() ?: 0.0,
                    onNameChanged = { viewModel.onItemNameChanged(item.id, it) },
                    onTypeChanged = { viewModel.onItemTypeChanged(item.id, it) },
                    onUnitPriceChanged = { viewModel.onUnitPriceChanged(item.id, it) },
                    onPackageQuantityChanged = { viewModel.onPackageQuantityChanged(item.id, it) },
                    onPurchaseQuantityChanged = { viewModel.onPurchaseQuantityChanged(item.id, it) },
                    onModeChanged = { viewModel.onUpdateModeChanged(item.id, it) },
                    onRemove = { viewModel.removeItemRow(item.id) },
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = viewModel::addItemRow) { Text("Add Item") }
                    Button(onClick = viewModel::saveOrder) { Text("Save Order") }
                }
            }

            if (uiState.value.errorMessage != null) {
                item { Text(uiState.value.errorMessage!!) }
            }
        }
    }
}

@Composable
private fun ExtraChargeModeCard(
    mode: ExtraChargeMode,
    manualExtraCharges: String,
    orderTotal: String,
    onModeChanged: (ExtraChargeMode) -> Unit,
    onManualExtraChargesChanged: (String) -> Unit,
    onOrderTotalChanged: (String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Extra charge input")
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                RadioButton(
                    selected = mode == ExtraChargeMode.MANUAL_EXTRA_CHARGES,
                    onClick = { onModeChanged(ExtraChargeMode.MANUAL_EXTRA_CHARGES) },
                )
                Text("Manual Extra Charges")
            }
            if (mode == ExtraChargeMode.MANUAL_EXTRA_CHARGES) {
                DecimalNumberInputField(
                    label = "Extra charges total (shipping + tax + hazmat)",
                    value = manualExtraCharges,
                    onValueChange = onManualExtraChargesChanged,
                )
            }

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                RadioButton(
                    selected = mode == ExtraChargeMode.USE_ORDER_TOTAL,
                    onClick = { onModeChanged(ExtraChargeMode.USE_ORDER_TOTAL) },
                )
                Text("Use Order Total")
            }
            if (mode == ExtraChargeMode.USE_ORDER_TOTAL) {
                DecimalNumberInputField(
                    label = "Final order total",
                    value = orderTotal,
                    onValueChange = onOrderTotalChanged,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderItemCard(
    item: OrderEntryItemUi,
    lineSubtotal: Double,
    allocatedExtra: Double,
    adjustedUnitCost: Double,
    onNameChanged: (String) -> Unit,
    onTypeChanged: (ComponentType) -> Unit,
    onUnitPriceChanged: (String) -> Unit,
    onPackageQuantityChanged: (String) -> Unit,
    onPurchaseQuantityChanged: (String) -> Unit,
    onModeChanged: (ComponentUpdateMode) -> Unit,
    onRemove: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TextInputField(label = "Item name", value = item.itemName, onValueChange = onNameChanged)
            EnumDropdown(
                label = "Component type",
                options = ComponentType.entries,
                selected = item.componentType,
                onSelected = onTypeChanged,
            )
            DecimalNumberInputField(
                label = "Unit price",
                value = item.unitPrice,
                onValueChange = onUnitPriceChanged,
            )
            DecimalNumberInputField(
                label = "Package quantity",
                value = item.packageQuantity,
                onValueChange = onPackageQuantityChanged,
            )
            DecimalNumberInputField(
                label = "Purchase quantity",
                value = item.purchaseQuantity,
                onValueChange = onPurchaseQuantityChanged,
            )
            EnumDropdown(
                label = "Save behavior",
                options = ComponentUpdateMode.entries,
                selected = item.updateMode,
                onSelected = onModeChanged,
            )
            Text("Line subtotal: ${CurrencyFormatters.formatUsd(lineSubtotal)}")
            Text("Allocated extra: ${CurrencyFormatters.formatUsd(allocatedExtra)}")
            Text("Adjusted unit cost: ${CurrencyFormatters.formatUsd(adjustedUnitCost)}")
            TextButton(onClick = onRemove) { Text("Remove") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T : Enum<T>> EnumDropdown(
    label: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}
