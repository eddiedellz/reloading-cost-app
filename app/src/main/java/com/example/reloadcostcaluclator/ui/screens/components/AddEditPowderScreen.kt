package com.example.reloadcostcaluclator.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reloadcostcaluclator.data.local.entity.ComponentType
import com.example.reloadcostcaluclator.data.repository.PowderRepository
import com.example.reloadcostcaluclator.data.repository.PurchaseOrderRepository
import com.example.reloadcostcaluclator.ui.components.ComponentPriceHistoryCard
import com.example.reloadcostcaluclator.ui.components.DecimalNumberInputField
import com.example.reloadcostcaluclator.ui.components.TextInputField
import com.example.reloadcostcaluclator.viewmodel.components.AddEditPowderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPowderScreen(
    repository: PowderRepository,
    purchaseOrderRepository: PurchaseOrderRepository,
    itemId: Long?,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddEditPowderViewModel = viewModel(factory = AddEditPowderViewModel.provideFactory(repository)),
) {
    LaunchedEffect(itemId) { viewModel.load(itemId) }
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (itemId == null) "Add Powder" else "Edit Powder") },
            navigationIcon = { TextButton(onClick = onBackClick) { Text("Back") } },
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TextInputField("Name", uiState.value.name, viewModel::onNameChanged)
            DecimalNumberInputField("Price paid", uiState.value.pricePaid, viewModel::onPricePaidChanged)
            DecimalNumberInputField("Container weight (lb)", uiState.value.containerWeightLb, viewModel::onContainerWeightChanged)
            ComponentPriceHistoryCard(purchaseOrderRepository, ComponentType.POWDER, uiState.value.name)
            if (uiState.value.errorMessage != null) Text(uiState.value.errorMessage!!)
            Button(onClick = { viewModel.save(onSaved) }, modifier = Modifier.fillMaxWidth()) { Text("Save") }
        }
    }
}
