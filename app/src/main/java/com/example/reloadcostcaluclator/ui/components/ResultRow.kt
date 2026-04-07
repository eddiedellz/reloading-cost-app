package com.example.reloadcostcaluclator.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ResultRow(label: String, value: Double) {
    Text(
        text = "$label: ${formatCurrency(value)}",
        modifier = Modifier.fillMaxWidth(),
    )
}

private fun formatCurrency(value: Double): String = "$" + "%.4f".format(value)
