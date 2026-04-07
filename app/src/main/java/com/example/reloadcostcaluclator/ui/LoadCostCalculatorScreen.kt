package com.example.reloadcostcaluclator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reloadcostcaluclator.ui.theme.ReloadCostCaluclatorTheme
import com.example.reloadcostcaluclator.util.AmmoCostCalculator

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

    var powderCostPerRound by remember { mutableStateOf(0.0) }
    var primerCostPerRound by remember { mutableStateOf(0.0) }
    var bulletCostPerRound by remember { mutableStateOf(0.0) }
    var brassCostPerRound by remember { mutableStateOf(0.0) }
    var totalCostPerRound by remember { mutableStateOf(0.0) }
    var totalCostPer50 by remember { mutableStateOf(0.0) }
    var totalCostPer100 by remember { mutableStateOf(0.0) }

    fun calculate() {
        powderCostPerRound = AmmoCostCalculator.powderCostPerRound(
            powderPrice = powderPrice.toDoubleOrNull() ?: 0.0,
            containerWeightLb = powderContainerWeight.toDoubleOrNull() ?: 0.0,
            chargeWeightGr = chargeWeight.toDoubleOrNull() ?: 0.0,
        )
        primerCostPerRound = AmmoCostCalculator.primerCostPerRound(
            primerPrice = primerPrice.toDoubleOrNull() ?: 0.0,
            primerQuantity = primerQuantity.toIntOrNull() ?: 0,
        )
        bulletCostPerRound = AmmoCostCalculator.bulletCostPerRound(
            bulletPrice = bulletPrice.toDoubleOrNull() ?: 0.0,
            bulletQuantity = bulletQuantity.toIntOrNull() ?: 0,
        )
        brassCostPerRound = AmmoCostCalculator.brassCostPerRound(
            brassPrice = brassPrice.toDoubleOrNull() ?: 0.0,
            brassQuantity = brassQuantity.toIntOrNull() ?: 0,
            reloadCount = brassReloadCount.toIntOrNull() ?: 0,
        )
        totalCostPerRound = AmmoCostCalculator.totalCostPerRound(
            powderCost = powderCostPerRound,
            primerCost = primerCostPerRound,
            bulletCost = bulletCostPerRound,
            brassCost = brassCostPerRound,
        )
        totalCostPer50 = AmmoCostCalculator.totalCostPer50(totalCostPerRound)
        totalCostPer100 = AmmoCostCalculator.totalCostPer100(totalCostPerRound)
    }

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
            value = powderPrice,
            onValueChange = { powderPrice = it },
            keyboardType = KeyboardType.Decimal,
        )
        CostInputField(
            label = "Powder container weight (lbs)",
            value = powderContainerWeight,
            onValueChange = { powderContainerWeight = it },
            keyboardType = KeyboardType.Decimal,
        )
        CostInputField(
            label = "Charge weight (grains)",
            value = chargeWeight,
            onValueChange = { chargeWeight = it },
            keyboardType = KeyboardType.Decimal,
        )

        CostInputField(
            label = "Primer price",
            value = primerPrice,
            onValueChange = { primerPrice = it },
            keyboardType = KeyboardType.Decimal,
        )
        CostInputField(
            label = "Primer quantity",
            value = primerQuantity,
            onValueChange = { primerQuantity = it },
            keyboardType = KeyboardType.Number,
        )

        CostInputField(
            label = "Bullet price",
            value = bulletPrice,
            onValueChange = { bulletPrice = it },
            keyboardType = KeyboardType.Decimal,
        )
        CostInputField(
            label = "Bullet quantity",
            value = bulletQuantity,
            onValueChange = { bulletQuantity = it },
            keyboardType = KeyboardType.Number,
        )

        CostInputField(
            label = "Brass price",
            value = brassPrice,
            onValueChange = { brassPrice = it },
            keyboardType = KeyboardType.Decimal,
        )
        CostInputField(
            label = "Brass quantity",
            value = brassQuantity,
            onValueChange = { brassQuantity = it },
            keyboardType = KeyboardType.Number,
        )
        CostInputField(
            label = "Brass reload count",
            value = brassReloadCount,
            onValueChange = { brassReloadCount = it },
            keyboardType = KeyboardType.Number,
        )

        Button(
            onClick = ::calculate,
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
                ResultRow("Powder cost per round", powderCostPerRound)
                ResultRow("Primer cost per round", primerCostPerRound)
                ResultRow("Bullet cost per round", bulletCostPerRound)
                ResultRow("Brass cost per round", brassCostPerRound)
                ResultRow("Total cost per round", totalCostPerRound)
                ResultRow("Total cost per 50", totalCostPer50)
                ResultRow("Total cost per 100", totalCostPer100)
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
        LoadCostCalculatorScreen()
    }
}
