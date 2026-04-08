package com.example.reloadcostcaluclator.ui.screens.loadrecipes

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
import com.example.reloadcostcaluclator.data.local.entity.LoadRecipeEntity
import com.example.reloadcostcaluclator.data.repository.LoadRecipeRepository
import com.example.reloadcostcaluclator.viewmodel.loadrecipes.LoadRecipeListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadRecipeListScreen(
    repository: LoadRecipeRepository,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onRecipeClick: (Long) -> Unit,
    viewModel: LoadRecipeListViewModel = viewModel(factory = LoadRecipeListViewModel.provideFactory(repository)),
) {
    val recipes = viewModel.loadRecipes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Load Recipes") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) { Text("Back") }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("Add")
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
            items(recipes.value, key = { it.id }) { recipe ->
                LoadRecipeRow(
                    recipe = recipe,
                    onClick = { onRecipeClick(recipe.id) },
                )
            }
        }
    }
}

@Composable
private fun LoadRecipeRow(
    recipe: LoadRecipeEntity,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(text = recipe.name)
            Text(text = "Caliber: ${recipe.caliber}")
            Text(text = "Charge: ${recipe.chargeWeightGr} gr")
        }
    }
}
