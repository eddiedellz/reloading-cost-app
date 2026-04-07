package com.example.reloadcostcaluclator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reloadcostcaluclator.ui.theme.ReloadCostCaluclatorTheme

@Composable
fun LoadCostCalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: LoadCostCalculatorViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoadCostCalculatorContent(
        modifier = modifier,
        uiState = uiState,
        onPowderPriceChanged = viewModel::onPowderPriceChanged,
        onPowderContainerWeightChanged = viewModel::onPowderContainerWeightChanged,
        onChargeWeightChanged = viewModel::onChargeWeightChanged,
        onPrimerPriceChanged = viewModel::onPrimerPriceChanged,
        onPrimerQuantityChanged = viewModel::onPrimerQuantityChanged,
        onBulletPriceChanged = viewModel::onBulletPriceChanged,
        onBulletQuantityChanged = viewModel::onBulletQuantityChanged,
        onBrassPriceChanged = viewModel::onBrassPriceChanged,
        onBrassQuantityChanged = viewModel::onBrassQuantityChanged,
        onBrassReloadCountChanged = viewModel::onBrassReloadCountChanged,
        onCalculateClicked = viewModel::onCalculateClicked,
    )
}

@Composable
private fun LoadCostCalculatorContent(
    modifier: Modifier = Modifier,
    uiState: LoadCostCalculatorUiState,
    onPowderPriceChanged: (String) -> Unit,
    onPowderContainerWeightChanged: (String) -> Unit,
    onChargeWeightChanged: (String) -> Unit,
    onPrimerPriceChanged: (String) -> Unit,
    onPrimerQuantityChanged: (String) -> Unit,
    onBulletPriceChanged: (String) -> Unit,
    onBulletQuantityChanged: (String) -> Unit,
    onBrassPriceChanged: (String) -> Unit,
    onBrassQuantityChanged: (String) -> Unit,
    onBrassReloadCountChanged: (String) -> Unit,
    onCalculateClicked: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Load Cost Calculator")

        CostInputField(
            label = "Powder price",
            value = uiState.powderPrice,
            onValueChange = onPowderPriceChanged,
            keyboardType = KeyboardType.Decimal,
        )
        CostInputField(
            label = "Powder container weight (lbs)",
            value = uiState.powderContainerWeight,
            onValueChange = onPowderContainerWeightChanged,
            keyboardType = KeyboardType.Decimal,
        )
        CostInputField(
            label = "Charge weight (grains)",
            value = uiState.chargeWeight,
            onValueChange = onChargeWeightChanged,
            keyboardType = KeyboardType.Decimal,
        )

        CostInputField(
            label = "Primer price",
            value = uiState.primerPrice,
            onValueChange = onPrimerPriceChanged,
            keyboardType = KeyboardType.Decimal,
        )
        CostInputField(
            label = "Primer quantity",
            value = uiState.primerQuantity,
            onValueChange = onPrimerQuantityChanged,
            keyboardType = KeyboardType.Number,
        )

        CostInputField(
            label = "Bullet price",
            value = uiState.bulletPrice,
            onValueChange = onBulletPriceChanged,
            keyboardType = KeyboardType.Decimal,
        )
        CostInputField(
            label = "Bullet quantity",
            value = uiState.bulletQuantity,
            onValueChange = onBulletQuantityChanged,
            keyboardType = KeyboardType.Number,
        )

        CostInputField(
            label = "Brass price",
            value = uiState.brassPrice,
            onValueChange = onBrassPriceChanged,
            keyboardType = KeyboardType.Decimal,
        )
        CostInputField(
            label = "Brass quantity",
            value = uiState.brassQuantity,
            onValueChange = onBrassQuantityChanged,
            keyboardType = KeyboardType.Number,
        )
        CostInputField(
            label = "Brass reload count",
            value = uiState.brassReloadCount,
            onValueChange = onBrassReloadCountChanged,
            keyboardType = KeyboardType.Number,
        )

        Button(
            onClick = onCalculateClicked,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Calculate")
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ResultRow("Powder cost per round", uiState.result.powderCostPerRound)
                ResultRow("Primer cost per round", uiState.result.primerCostPerRound)
                ResultRow("Bullet cost per round", uiState.result.bulletCostPerRound)
                ResultRow("Brass cost per round", uiState.result.brassCostPerRound)
                ResultRow("Total cost per round", uiState.result.totalCostPerRound)
                ResultRow("Total cost per 50", uiState.result.totalCostPer50)
                ResultRow("Total cost per 100", uiState.result.totalCostPer100)
            }
        }
    }
}

@Composable
private fun CostInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
private fun ResultRow(label: String, value: Double) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "$label: ${formatCurrency(value)}")
    }
}

private fun formatCurrency(value: Double): String = "$" + "%.4f".format(value)

@Preview(showBackground = true)
@Composable
private fun LoadCostCalculatorScreenPreview() {
    ReloadCostCaluclatorTheme {
        LoadCostCalculatorContent(
            uiState = LoadCostCalculatorUiState(),
            onPowderPriceChanged = {},
            onPowderContainerWeightChanged = {},
            onChargeWeightChanged = {},
            onPrimerPriceChanged = {},
            onPrimerQuantityChanged = {},
            onBulletPriceChanged = {},
            onBulletQuantityChanged = {},
            onBrassPriceChanged = {},
            onBrassQuantityChanged = {},
            onBrassReloadCountChanged = {},
            onCalculateClicked = {},
        )
    }
}
