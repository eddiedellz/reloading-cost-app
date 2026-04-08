package com.example.reloadcostcaluclator.ui.screens.loadrecipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reloadcostcaluclator.data.reference.FactoryAmmoReferenceRepository
import com.example.reloadcostcaluclator.data.repository.BrassRepository
import com.example.reloadcostcaluclator.data.repository.BulletRepository
import com.example.reloadcostcaluclator.data.repository.LoadRecipeRepository
import com.example.reloadcostcaluclator.data.repository.PowderRepository
import com.example.reloadcostcaluclator.data.repository.PrimerRepository
import com.example.reloadcostcaluclator.util.CurrencyFormatters
import com.example.reloadcostcaluclator.viewmodel.loadrecipes.LoadRecipeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadRecipeDetailScreen(
    recipeId: Long,
    loadRecipeRepository: LoadRecipeRepository,
    powderRepository: PowderRepository,
    primerRepository: PrimerRepository,
    bulletRepository: BulletRepository,
    brassRepository: BrassRepository,
    factoryAmmoReferenceRepository: FactoryAmmoReferenceRepository,
    onBackClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: LoadRecipeDetailViewModel = viewModel(
        factory = LoadRecipeDetailViewModel.provideFactory(
            recipeId = recipeId,
            loadRecipeRepository = loadRecipeRepository,
            powderRepository = powderRepository,
            primerRepository = primerRepository,
            bulletRepository = bulletRepository,
            brassRepository = brassRepository,
            factoryAmmoReferenceRepository = factoryAmmoReferenceRepository,
        ),
    ),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val recipe = uiState.value.loadRecipe

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe?.name ?: "Load Detail") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) { Text("Back") }
                },
                actions = {
                    recipe?.let {
                        TextButton(onClick = { onEditClick(it.id) }) { Text("Edit") }
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (recipe == null) {
                Text("Load recipe not found.")
                return@Column
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Load name: ${recipe.name}")
                    Text("Caliber: ${recipe.caliber}")
                    Text("Powder: ${uiState.value.powder?.name ?: "Not set"}")
                    Text("Charge weight: ${recipe.chargeWeightGr} gr")
                    Text("Primer: ${uiState.value.primer?.name ?: "Not set"}")
                    Text("Bullet: ${uiState.value.bullet?.name ?: "Not set"}")
                    uiState.value.bullet?.grain?.let { Text("Bullet grain: ${it} gr") }
                    if (!uiState.value.bullet?.bulletType.isNullOrBlank()) {
                        Text("Bullet type: ${uiState.value.bullet?.bulletType}")
                    }
                    Text("Brass: ${uiState.value.brass?.name ?: "Not set"}")
                    if (recipe.notes.isNotBlank()) {
                        Text("Notes: ${recipe.notes}")
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Powder cost / round: ${CurrencyFormatters.formatUsd(uiState.value.powderCostPerRound)}")
                    Text("Primer cost / round: ${CurrencyFormatters.formatUsd(uiState.value.primerCostPerRound)}")
                    Text("Bullet cost / round: ${CurrencyFormatters.formatUsd(uiState.value.bulletCostPerRound)}")
                    Text("Brass cost / round: ${CurrencyFormatters.formatUsd(uiState.value.brassCostPerRound)}")
                    Text("Total cost / round: ${CurrencyFormatters.formatUsd(uiState.value.totalCostPerRound)}")
                    Text("Total cost / 50: ${CurrencyFormatters.formatUsd(uiState.value.totalCostPer50)}")
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Factory / Reference Comparison")
                    val bestComparison = uiState.value.factoryComparisons.firstOrNull()
                    if (bestComparison == null) {
                        Text("No matching reference ammo found for caliber + grain + bullet type filters.")
                    } else {
                        Text("Reference: ${bestComparison.reference.name}")
                        Text("Reference price / round: ${CurrencyFormatters.formatUsd(bestComparison.reference.pricePerRound)}")
                        Text("Reference price / 50: ${CurrencyFormatters.formatUsd(bestComparison.reference.pricePer50)}")
                        Text("Savings / round: ${CurrencyFormatters.formatUsd(bestComparison.savingsPerRound)}")
                        Text("Savings / 50: ${CurrencyFormatters.formatUsd(bestComparison.savingsPer50)}")
                        Text("Savings / 1000: ${CurrencyFormatters.formatUsd(bestComparison.savingsPer1000)}")
                        if (bestComparison.reference.notes.isNotBlank()) {
                            Text("Notes: ${bestComparison.reference.notes}")
                        }
                    }
                }
            }

        }
    }
}
