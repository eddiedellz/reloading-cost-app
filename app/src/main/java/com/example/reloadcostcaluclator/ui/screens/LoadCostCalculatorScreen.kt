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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reloadcostcaluclator.ui.components.CostInputField
import com.example.reloadcostcaluclator.ui.theme.ReloadCostCaluclatorTheme
import com.example.reloadcostcaluclator.util.AmmoCostCalculator
import com.example.reloadcostcaluclator.util.CurrencyFormatters

@Composable
fun LoadCostCalculatorScreen(modifier: Modifier = Modifier) {
    var powderPrice by remember { mutableStateOf("") }
    var powderContainerWeight by remember { mutableStateOf("") }
    var chargeWeight by remember { mutableStateOf("") }
    var primerPrice by remember { mutableStateOf("") }
    var primerQuantity by remember { mutableStateOf("") }
    var bulletPrice by remember { mutableStateOf("") }
    var bulletQuantity by remember { mutableStateOf("") }
    var brassPrice by remember { mutableStateOf("") }
    var brassQuantity by remember { mutableStateOf("") }
    var brassReloadCount by remember { mutableStateOf("") }

    var powderCostPerRound by remember { mutableDoubleStateOf(0.0) }
    var primerCostPerRound by remember { mutableDoubleStateOf(0.0) }
    var bulletCostPerRound by remember { mutableDoubleStateOf(0.0) }
    var brassCostPerRound by remember { mutableDoubleStateOf(0.0) }
    var totalCostPerRound by remember { mutableDoubleStateOf(0.0) }
    var totalCostPer50 by remember { mutableDoubleStateOf(0.0) }
    var totalCostPer100 by remember { mutableDoubleStateOf(0.0) }

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
            value = powderPrice,
            onValueChange = { powderPrice = it },
        )
        CostInputField(
            label = "Powder container weight (lbs)",
            value = powderContainerWeight,
            onValueChange = { powderContainerWeight = it },
            isDecimal = true,
        )
        CostInputField(
            label = "Charge weight (grains)",
            value = chargeWeight,
            onValueChange = { chargeWeight = it },
            isDecimal = true,
        )

        CostInputField(
            label = "Primer price",
            value = primerPrice,
            onValueChange = { primerPrice = it },
        )
        CostInputField(
            label = "Primer quantity",
            value = primerQuantity,
            onValueChange = { primerQuantity = it },
            isDecimal = false,
        )

        CostInputField(
            label = "Bullet price",
            value = bulletPrice,
            onValueChange = { bulletPrice = it },
        )
        CostInputField(
            label = "Bullet quantity",
            value = bulletQuantity,
            onValueChange = { bulletQuantity = it },
            isDecimal = false,
        )

        CostInputField(
            label = "Brass price",
            value = brassPrice,
            onValueChange = { brassPrice = it },
        )
        CostInputField(
            label = "Brass quantity",
            value = brassQuantity,
            onValueChange = { brassQuantity = it },
            isDecimal = false,
        )
        CostInputField(
            label = "Brass reload count",
            value = brassReloadCount,
            onValueChange = { brassReloadCount = it },
            isDecimal = false,
        )

        Button(
            onClick = {
                val powderPriceValue = powderPrice.toDoubleOrNull() ?: 0.0
                val powderContainerWeightValue = powderContainerWeight.toDoubleOrNull() ?: 0.0
                val chargeWeightValue = chargeWeight.toDoubleOrNull() ?: 0.0
                val primerPriceValue = primerPrice.toDoubleOrNull() ?: 0.0
                val primerQuantityValue = primerQuantity.toIntOrNull() ?: 0
                val bulletPriceValue = bulletPrice.toDoubleOrNull() ?: 0.0
                val bulletQuantityValue = bulletQuantity.toIntOrNull() ?: 0
                val brassPriceValue = brassPrice.toDoubleOrNull() ?: 0.0
                val brassQuantityValue = brassQuantity.toIntOrNull() ?: 0
                val brassReloadCountValue = brassReloadCount.toIntOrNull() ?: 0

                powderCostPerRound = AmmoCostCalculator.powderCostPerRound(
                    powderPrice = powderPriceValue,
                    containerWeightLb = powderContainerWeightValue,
                    chargeWeightGr = chargeWeightValue,
                )
                primerCostPerRound = AmmoCostCalculator.primerCostPerRound(
                    primerPrice = primerPriceValue,
                    primerQuantity = primerQuantityValue,
                )
                bulletCostPerRound = AmmoCostCalculator.bulletCostPerRound(
                    bulletPrice = bulletPriceValue,
                    bulletQuantity = bulletQuantityValue,
                )
                brassCostPerRound = AmmoCostCalculator.brassCostPerRound(
                    brassPrice = brassPriceValue,
                    brassQuantity = brassQuantityValue,
                    reloadCount = brassReloadCountValue,
                )
                totalCostPerRound = AmmoCostCalculator.totalCostPerRound(
                    powderCost = powderCostPerRound,
                    primerCost = primerCostPerRound,
                    bulletCost = bulletCostPerRound,
                    brassCost = brassCostPerRound,
                )
                totalCostPer50 = AmmoCostCalculator.totalCostPer50(totalCostPerRound)
                totalCostPer100 = AmmoCostCalculator.totalCostPer100(totalCostPerRound)
            },
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
                Text("Powder cost per round: ${CurrencyFormatters.formatCostPerRound(powderCostPerRound)}")
                Text("Primer cost per round: ${CurrencyFormatters.formatCostPerRound(primerCostPerRound)}")
                Text("Bullet cost per round: ${CurrencyFormatters.formatCostPerRound(bulletCostPerRound)}")
                Text("Brass cost per round: ${CurrencyFormatters.formatCostPerRound(brassCostPerRound)}")
                Text("Total cost per round: ${CurrencyFormatters.formatCostPerRound(totalCostPerRound)}")
                Text("Total cost per 50: ${CurrencyFormatters.formatUsd(totalCostPer50)}")
                Text("Total cost per 100: ${CurrencyFormatters.formatUsd(totalCostPer100)}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadCostCalculatorScreenPreview() {
    ReloadCostCaluclatorTheme {
        LoadCostCalculatorScreen()
    }
}
