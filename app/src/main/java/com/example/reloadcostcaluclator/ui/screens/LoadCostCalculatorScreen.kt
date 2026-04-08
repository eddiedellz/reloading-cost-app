package com.example.reloadcostcaluclator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reloadcostcaluclator.ui.components.CostInputField
import com.example.reloadcostcaluclator.ui.theme.ReloadCostCaluclatorTheme
import com.example.reloadcostcaluclator.util.CurrencyFormatters
import com.example.reloadcostcaluclator.viewmodel.LoadCostCalculatorUiState
import com.example.reloadcostcaluclator.viewmodel.LoadCostCalculatorViewModel

@Composable
fun LoadCostCalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: LoadCostCalculatorViewModel = viewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LoadCostCalculatorScreenContent(
        modifier = modifier,
        uiState = uiState.value,
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
private fun LoadCostCalculatorScreenContent(
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
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "RELOADING COST CALCULATOR TEST",
            style = MaterialTheme.typography.headlineSmall,
        )

        CostInputField(
            label = "Powder price",
            value = uiState.powderPrice,
            onValueChange = onPowderPriceChanged,
        )
        CostInputField(
            label = "Powder container weight (lbs)",
            value = uiState.powderContainerWeight,
            onValueChange = onPowderContainerWeightChanged,
            isDecimal = true,
        )
        CostInputField(
            label = "Charge weight (grains)",
            value = uiState.chargeWeight,
            onValueChange = onChargeWeightChanged,
            isDecimal = true,
        )

        CostInputField(
            label = "Primer price",
            value = uiState.primerPrice,
            onValueChange = onPrimerPriceChanged,
        )
        CostInputField(
            label = "Primer quantity",
            value = uiState.primerQuantity,
            onValueChange = onPrimerQuantityChanged,
            isDecimal = false,
        )

        CostInputField(
            label = "Bullet price",
            value = uiState.bulletPrice,
            onValueChange = onBulletPriceChanged,
        )
        CostInputField(
            label = "Bullet quantity",
            value = uiState.bulletQuantity,
            onValueChange = onBulletQuantityChanged,
            isDecimal = false,
        )

        CostInputField(
            label = "Brass price",
            value = uiState.brassPrice,
            onValueChange = onBrassPriceChanged,
        )
        CostInputField(
            label = "Brass quantity",
            value = uiState.brassQuantity,
            onValueChange = onBrassQuantityChanged,
            isDecimal = false,
        )
        CostInputField(
            label = "Brass reload count",
            value = uiState.brassReloadCount,
            onValueChange = onBrassReloadCountChanged,
            isDecimal = false,
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
            val formattedPowderCostPerRound = "${CurrencyFormatters.formatUsd(uiState.result.powderCostPerRound)}/rd"
            val formattedPrimerCostPerRound = "${CurrencyFormatters.formatUsd(uiState.result.primerCostPerRound)}/rd"
            val formattedBulletCostPerRound = "${CurrencyFormatters.formatUsd(uiState.result.bulletCostPerRound)}/rd"
            val formattedBrassCostPerRound = "${CurrencyFormatters.formatUsd(uiState.result.brassCostPerRound)}/rd"
            val formattedTotalCostPerRound = "${CurrencyFormatters.formatUsd(uiState.result.totalCostPerRound)}/rd"
            val formattedTotalCostPer50 = CurrencyFormatters.formatUsd(uiState.result.totalCostPer50)
            val formattedTotalCostPer100 = CurrencyFormatters.formatUsd(uiState.result.totalCostPer100)

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Powder cost per round: $formattedPowderCostPerRound")
                Text("Primer cost per round: $formattedPrimerCostPerRound")
                Text("Bullet cost per round: $formattedBulletCostPerRound")
                Text("Brass cost per round: $formattedBrassCostPerRound")
                Text("Total cost per round: $formattedTotalCostPerRound")
                Text("Total cost per 50: $formattedTotalCostPer50")
                Text("Total cost per 100: $formattedTotalCostPer100")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadCostCalculatorScreenPreview() {
    ReloadCostCaluclatorTheme {
        LoadCostCalculatorScreenContent(
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
