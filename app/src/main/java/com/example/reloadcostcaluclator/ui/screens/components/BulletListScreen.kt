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
import com.example.reloadcostcaluclator.data.local.entity.BulletEntity
import com.example.reloadcostcaluclator.data.repository.BulletRepository
import com.example.reloadcostcaluclator.viewmodel.components.BulletListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulletListScreen(
    repository: BulletRepository,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: BulletListViewModel = viewModel(factory = BulletListViewModel.provideFactory(repository)),
) {
    val bullets = viewModel.bullets.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bullets") },
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
            items(bullets.value, key = { it.id }) { bullet ->
                BulletRow(
                    bullet = bullet,
                    onEditClick = { onEditClick(bullet.id) },
                    onDeleteClick = { viewModel.delete(bullet) },
                )
            }
        }
    }
}

@Composable
private fun BulletRow(
    bullet: BulletEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = bullet.name)
            Text(text = "Price: $${bullet.pricePaid}")
            Text(text = "Quantity: ${bullet.quantity}")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEditClick) { Text("Edit") }
                Button(onClick = onDeleteClick) { Text("Delete") }
            }
        }
    }
}
