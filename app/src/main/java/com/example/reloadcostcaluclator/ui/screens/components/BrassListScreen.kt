package com.example.reloadcostcaluclator.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import com.example.reloadcostcaluclator.data.local.entity.BrassEntity
import com.example.reloadcostcaluclator.data.repository.BrassRepository
import com.example.reloadcostcaluclator.viewmodel.components.BrassListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrassListScreen(
    repository: BrassRepository,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: BrassListViewModel = viewModel(factory = BrassListViewModel.provideFactory(repository)),
) {
    val brass = viewModel.brass.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Brass") },
                navigationIcon = { TextButton(onClick = onBackClick) { Text("Back") } },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) { Text("Add") }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(brass.value, key = { it.id }) { brassItem ->
                BrassRow(
                    brass = brassItem,
                    onEditClick = { onEditClick(brassItem.id) },
                    onDeleteClick = { viewModel.delete(brassItem) },
                )
            }
        }
    }
}

@Composable
private fun BrassRow(
    brass: BrassEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = brass.name)
            Text(text = "Price: $${brass.pricePaid}")
            Text(text = "Quantity: ${brass.quantity}")
            Text(text = "Reload count: ${brass.reloadCount}")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEditClick) { Text("Edit") }
                Button(onClick = onDeleteClick) { Text("Delete") }
            }
        }
    }
}
