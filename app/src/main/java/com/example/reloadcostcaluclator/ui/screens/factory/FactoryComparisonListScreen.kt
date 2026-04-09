package com.example.reloadcostcaluclator.ui.screens.factory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reloadcostcaluclator.data.local.entity.FactoryComparisonEntity
import com.example.reloadcostcaluclator.data.repository.FactoryComparisonRepository
import com.example.reloadcostcaluclator.util.CurrencyFormatters
import com.example.reloadcostcaluclator.viewmodel.factory.FactoryComparisonListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactoryComparisonListScreen(
    repository: FactoryComparisonRepository,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: FactoryComparisonListViewModel = viewModel(
        factory = FactoryComparisonListViewModel.provideFactory(repository),
    ),
) {
    val comparisons = viewModel.comparisons.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Factory Comparison") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) { Text("Back") }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("Add Factory Cost")
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(comparisons.value, key = { it.id }) { item ->
                FactoryComparisonRow(item = item, onClick = { onEditClick(item.id) })
            }
        }
    }
}

@Composable
private fun FactoryComparisonRow(item: FactoryComparisonEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text("${item.brand} ${item.productName}")
            Text("${item.caliber} ${item.grain}gr")
            if (!item.bulletType.isNullOrBlank()) {
                Text("Type: ${item.bulletType}")
            }
            Text("Box: ${item.boxQuantity} | Total: ${CurrencyFormatters.formatUsd(item.totalPrice)}")
            Text("Cost / round: ${CurrencyFormatters.formatUsd(item.costPerRound)}")
        }
    }
}
