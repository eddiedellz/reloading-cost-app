package com.example.reloadcostcaluclator.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
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
    var addDialogType by remember { mutableStateOf<ComponentType?>(null) }
    if (uiState.value.saved) onSaved()

    addDialogType?.let { componentType ->
        AddItemDialog(
            componentType = componentType,
            onDismiss = { addDialogType = null },
            onAddItem = { name, packageQuantity, purchaseQuantity, unitPrice ->
                viewModel.addItem(
                    componentType = componentType,
                    itemName = name,
                    packageQuantity = packageQuantity,
                    purchaseQuantity = purchaseQuantity,
                    unitPrice = unitPrice,
                )
                addDialogType = null
            },
        )
    }

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
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("What did you buy?", style = MaterialTheme.typography.titleMedium)
                        Text("Tap one of these buttons to add that item to this receipt/cart.")
                        ComponentTypeQuickAddRow(onAddComponent = { addDialogType = it })
                        Text("Items added: ${uiState.value.items.size}")
                    }
                }
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
                        Text("Subtotal: ${CurrencyFormatters.formatUsd(uiState.value.computed.subtotalCents / 100.0)}")
                        Text("Extra charges: ${CurrencyFormatters.formatUsd(uiState.value.computed.extraChargesCents / 100.0)}")
                        Text("Order total: ${CurrencyFormatters.formatUsd(uiState.value.computed.totalCents / 100.0)}")
                    }
                }
            }

            item {
                Text("Receipt items", style = MaterialTheme.typography.titleMedium)
            }

            if (uiState.value.items.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                "No items added yet. Tap a component above to add something from your receipt/cart.",
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }
                }
            }

            items(uiState.value.items, key = { it.id }) { item ->
                val computedLine = uiState.value.computed.lines[item.id]
                OrderItemCard(
                    item = item,
                    lineSubtotalCents = computedLine?.lineSubtotalCents ?: 0,
                    allocatedExtraCents = computedLine?.allocatedExtraCents ?: 0,
                    baseUnitCostCents = computedLine?.unitPriceCents ?: 0,
                    adjustedUnitCostCents = computedLine?.adjustedUnitCostCents ?: 0,
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
                    Button(onClick = viewModel::saveOrder) { Text("Save Purchase Order") }
                }
            }

            if (uiState.value.errorMessage != null) {
                item { Text(uiState.value.errorMessage!!) }
            }
        }
    }
}

@Composable
private fun ComponentTypeQuickAddRow(
    onAddComponent: (ComponentType) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ComponentType.entries.forEach { type ->
            Button(onClick = { onAddComponent(type) }) {
                Text("+ ${type.displayLabel()}")
            }
        }
    }
}

private fun ComponentType.displayLabel(): String = when (this) {
    ComponentType.POWDER -> "Powder"
    ComponentType.PRIMER -> "Primer"
    ComponentType.BULLET -> "Bullet"
    ComponentType.BRASS -> "Brass"
    ComponentType.OTHER -> "Other"
}

private fun ComponentType.dialogTitle(): String = when (this) {
    ComponentType.POWDER -> "Add Powder"
    ComponentType.PRIMER -> "Add Primer"
    ComponentType.BULLET -> "Add Bullets"
    ComponentType.BRASS -> "Add Brass"
    ComponentType.OTHER -> "Add Other"
}

private fun ComponentType.defaultItemName(): String = when (this) {
    ComponentType.POWDER -> "Powder Item"
    ComponentType.PRIMER -> "Primer Item"
    ComponentType.BULLET -> "Bullet Item"
    ComponentType.BRASS -> "Brass Item"
    ComponentType.OTHER -> "Other Item"
}

private fun ComponentType.itemNameLabel(): String = when (this) {
    ComponentType.POWDER -> "Powder name"
    ComponentType.PRIMER -> "Primer name"
    ComponentType.BULLET -> "Bullet name"
    ComponentType.BRASS -> "Brass name"
    ComponentType.OTHER -> "Item name"
}

private fun ComponentType.packageQuantityLabel(): String = when (this) {
    ComponentType.POWDER -> "Container size in pounds"
    ComponentType.PRIMER -> "Primers per case/box"
    ComponentType.BULLET -> "Bullets per box"
    ComponentType.BRASS -> "Brass pieces per bag/box"
    ComponentType.OTHER -> "Quantity per package"
}

private fun ComponentType.purchaseQuantityLabel(): String = when (this) {
    ComponentType.POWDER -> "Number of containers bought"
    ComponentType.PRIMER -> "Number of cases/boxes bought"
    ComponentType.BULLET -> "Number of boxes bought"
    ComponentType.BRASS -> "Number of bags/boxes bought"
    ComponentType.OTHER -> "Number of packages bought"
}

private fun ComponentType.unitPriceLabel(): String = when (this) {
    ComponentType.POWDER -> "Price per container"
    ComponentType.PRIMER -> "Price per case/box"
    ComponentType.BULLET -> "Price per box"
    ComponentType.BRASS -> "Price per bag/box"
    ComponentType.OTHER -> "Price per package"
}

private fun ComponentType.purchaseExample(): String = when (this) {
    ComponentType.POWDER -> "Example: 1 lb container. If you bought 2 containers, enter 1 in amount and 2 in packages."
    ComponentType.PRIMER -> "Example: 1 case of 1,000 primers. Enter 1000 in amount and 1 in packages."
    ComponentType.BULLET -> "Example: box of 500 bullets. Enter 500 in amount and the number of boxes bought."
    ComponentType.BRASS -> "Example: bag of 250 brass cases. Enter 250 in amount and the number of bags bought."
    ComponentType.OTHER -> "Example: package quantity of 1 with the number of packages bought."
}

@Composable
private fun AddItemDialog(
    componentType: ComponentType,
    onDismiss: () -> Unit,
    onAddItem: (name: String, packageQuantity: String, purchaseQuantity: String, unitPrice: String) -> Unit,
) {
    var name by remember(componentType) { mutableStateOf("") }
    var packageQuantity by remember(componentType) { mutableStateOf("") }
    var purchaseQuantity by remember(componentType) { mutableStateOf("") }
    var unitPrice by remember(componentType) { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(componentType.dialogTitle()) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextInputField(
                    label = componentType.itemNameLabel(),
                    value = name,
                    onValueChange = { name = it },
                )
                DecimalNumberInputField(
                    label = componentType.packageQuantityLabel(),
                    value = packageQuantity,
                    onValueChange = { packageQuantity = it },
                )
                DecimalNumberInputField(
                    label = componentType.purchaseQuantityLabel(),
                    value = purchaseQuantity,
                    onValueChange = { purchaseQuantity = it },
                )
                DecimalNumberInputField(
                    label = componentType.unitPriceLabel(),
                    value = unitPrice,
                    onValueChange = { unitPrice = it },
                )
            }
        },
        confirmButton = {
            Button(onClick = { onAddItem(name, packageQuantity, purchaseQuantity, unitPrice) }) {
                Text("Add Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
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
    lineSubtotalCents: Int,
    allocatedExtraCents: Int,
    baseUnitCostCents: Int,
    adjustedUnitCostCents: Int,
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    item.itemName.ifBlank { item.componentType.defaultItemName() },
                    style = MaterialTheme.typography.titleMedium,
                )
                TextButton(onClick = onRemove) { Text("Remove") }
            }
            TextInputField(label = "Receipt/cart item name", value = item.itemName, onValueChange = onNameChanged)
            EnumDropdown(
                label = "Component type",
                options = ComponentType.entries,
                selected = item.componentType,
                onSelected = onTypeChanged,
            )
            DecimalNumberInputField(
                label = item.componentType.unitPriceLabel(),
                value = item.unitPrice,
                onValueChange = onUnitPriceChanged,
            )
            DecimalNumberInputField(
                label = item.componentType.packageQuantityLabel(),
                value = item.packageQuantity,
                onValueChange = onPackageQuantityChanged,
            )
            DecimalNumberInputField(
                label = item.componentType.purchaseQuantityLabel(),
                value = item.purchaseQuantity,
                onValueChange = onPurchaseQuantityChanged,
            )
            Text(item.componentType.purchaseExample())
            EnumDropdown(
                label = "Save behavior",
                options = ComponentUpdateMode.entries,
                selected = item.updateMode,
                onSelected = onModeChanged,
            )
            Text("Line subtotal: ${CurrencyFormatters.formatUsd(lineSubtotalCents / 100.0)}")
            Text("Base unit cost: ${CurrencyFormatters.formatUsd(baseUnitCostCents / 100.0)}")
            Text("Allocated extra: ${CurrencyFormatters.formatUsd(allocatedExtraCents / 100.0)}")
            Text("True cost per unit after tax/shipping/hazmat: ${CurrencyFormatters.formatUsd(adjustedUnitCostCents / 100.0)}")
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
