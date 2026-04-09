package com.example.reloadcostcaluclator.ui.screens.factory

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
import com.example.reloadcostcaluclator.data.repository.FactoryComparisonRepository
import com.example.reloadcostcaluclator.ui.components.DecimalNumberInputField
import com.example.reloadcostcaluclator.ui.components.TextInputField
import com.example.reloadcostcaluclator.viewmodel.factory.AddEditFactoryComparisonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFactoryComparisonScreen(
    repository: FactoryComparisonRepository,
    itemId: Long?,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddEditFactoryComparisonViewModel = viewModel(
        factory = AddEditFactoryComparisonViewModel.provideFactory(repository),
    ),
) {
    LaunchedEffect(itemId) { viewModel.load(itemId) }
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (itemId == null) "Add Factory Cost" else "Edit Factory Cost") },
            navigationIcon = { TextButton(onClick = onBackClick) { Text("Back") } },
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TextInputField("Brand", uiState.value.brand, viewModel::onBrandChanged)
            TextInputField("Product name", uiState.value.productName, viewModel::onProductNameChanged)
            TextInputField("Caliber", uiState.value.caliber, viewModel::onCaliberChanged)
            DecimalNumberInputField("Grain", uiState.value.grain, viewModel::onGrainChanged)
            TextInputField("Bullet type (optional)", uiState.value.bulletType, viewModel::onBulletTypeChanged)
            DecimalNumberInputField("Box quantity", uiState.value.boxQuantity, viewModel::onBoxQuantityChanged)
            DecimalNumberInputField("Total price", uiState.value.totalPrice, viewModel::onTotalPriceChanged)
            TextInputField("Notes", uiState.value.notes, viewModel::onNotesChanged)

            uiState.value.errorMessage?.let { Text(it) }
            Button(onClick = { viewModel.save(onSaved) }, modifier = Modifier.fillMaxWidth()) { Text("Save") }
        }
    }
}
