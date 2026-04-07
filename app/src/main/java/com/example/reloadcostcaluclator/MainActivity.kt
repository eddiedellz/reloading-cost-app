package com.example.reloadcostcaluclator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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

private const val GRAINS_PER_POUND = 7000.0

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReloadCostCaluclatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PowderCostCalculatorScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PowderCostCalculatorScreen(modifier: Modifier = Modifier) {
    var powderPriceInput by remember { mutableStateOf("") }
    var powderWeightInput by remember { mutableStateOf("") }
    var chargeWeightInput by remember { mutableStateOf("") }

    val powderPrice = powderPriceInput.toDoubleOrNull()
    val powderWeight = powderWeightInput.toDoubleOrNull()
    val chargeWeight = chargeWeightInput.toDoubleOrNull()

    val costPerGrain = if ((powderPrice ?: 0.0) > 0.0 && (powderWeight ?: 0.0) > 0.0) {
        powderPrice!! / (powderWeight!! * GRAINS_PER_POUND)
    } else {
        null
    }

    val powderCostPerRound = if (costPerGrain != null && (chargeWeight ?: 0.0) > 0.0) {
        costPerGrain * chargeWeight!!
    } else {
        null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Powder Cost Calculator")

        OutlinedTextField(
            value = powderPriceInput,
            onValueChange = { powderPriceInput = it },
            label = { Text("Powder price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = powderWeightInput,
            onValueChange = { powderWeightInput = it },
            label = { Text("Powder weight (lbs)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = chargeWeightInput,
            onValueChange = { chargeWeightInput = it },
            label = { Text("Charge weight (grains)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Cost per grain: ${formatCurrency(costPerGrain)}"
        )
        Text(
            text = "Powder cost per round: ${formatCurrency(powderCostPerRound)}"
        )
    }
}

private fun formatCurrency(value: Double?): String {
    if (value == null) return "--"
    return "$" + "%.4f".format(value)
}

@Preview(showBackground = true)
@Composable
fun PowderCostCalculatorScreenPreview() {
    ReloadCostCaluclatorTheme {
        PowderCostCalculatorScreen()
    }
}
