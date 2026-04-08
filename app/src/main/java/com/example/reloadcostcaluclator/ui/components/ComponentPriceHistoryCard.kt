package com.example.reloadcostcaluclator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reloadcostcaluclator.data.local.entity.ComponentType
import com.example.reloadcostcaluclator.data.repository.PriceHistorySummary
import com.example.reloadcostcaluclator.data.repository.PurchaseOrderRepository
import com.example.reloadcostcaluclator.util.CurrencyFormatters
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ComponentPriceHistoryCard(
    repository: PurchaseOrderRepository,
    componentType: ComponentType,
    componentName: String,
) {
    if (componentName.isBlank()) return
    var summary by remember(componentType, componentName) { mutableStateOf<PriceHistorySummary?>(null) }

    LaunchedEffect(componentType, componentName) {
        summary = repository.getPriceHistorySummary(componentType, componentName)
    }

    val history = summary?.history ?: emptyList()
    if (history.isEmpty()) return

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text("Price History")
            Text("Weighted avg cost/unit: ${CurrencyFormatters.currency(summary!!.weightedAverageCostPerUnit)}")
            Text("Latest landed cost: ${CurrencyFormatters.currency(summary!!.latestLandedCost)}")

            history.take(8).forEach {
                val date = Instant.ofEpochMilli(it.purchaseDateEpochMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE)
                val sign = if (it.previousDifference >= 0) "+" else "-"
                Text(
                    "$date • Landed ${CurrencyFormatters.currency(it.landedCost)} • Δ vs prev: $sign${CurrencyFormatters.currency(kotlin.math.abs(it.previousDifference))}",
                )
            }
        }
    }
}
