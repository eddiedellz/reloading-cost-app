package com.example.reloadcostcaluclator.ui.screens.loadrecipes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reloadcostcaluclator.data.local.entity.BrassEntity
import com.example.reloadcostcaluclator.data.local.entity.BulletEntity
import com.example.reloadcostcaluclator.data.local.entity.PowderEntity
import com.example.reloadcostcaluclator.data.local.entity.PrimerEntity
import com.example.reloadcostcaluclator.data.repository.BrassRepository
import com.example.reloadcostcaluclator.data.repository.BulletRepository
import com.example.reloadcostcaluclator.data.repository.LoadRecipeRepository
import com.example.reloadcostcaluclator.data.repository.PowderRepository
import com.example.reloadcostcaluclator.data.repository.PrimerRepository
import com.example.reloadcostcaluclator.ui.components.DecimalNumberInputField
import com.example.reloadcostcaluclator.ui.components.TextInputField
import com.example.reloadcostcaluclator.viewmodel.loadrecipes.AddEditLoadRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditLoadRecipeScreen(
    loadRecipeRepository: LoadRecipeRepository,
    powderRepository: PowderRepository,
    primerRepository: PrimerRepository,
    bulletRepository: BulletRepository,
    brassRepository: BrassRepository,
    itemId: Long?,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddEditLoadRecipeViewModel = viewModel(
        factory = AddEditLoadRecipeViewModel.provideFactory(
            loadRecipeRepository = loadRecipeRepository,
            powderRepository = powderRepository,
            primerRepository = primerRepository,
            bulletRepository = bulletRepository,
            brassRepository = brassRepository,
        ),
    ),
) {
    LaunchedEffect(itemId) { viewModel.load(itemId) }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val powders = viewModel.powders.collectAsStateWithLifecycle()
    val primers = viewModel.primers.collectAsStateWithLifecycle()
    val bullets = viewModel.bullets.collectAsStateWithLifecycle()
    val brass = viewModel.brass.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (itemId == null) "Add Load Recipe" else "Edit Load Recipe") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) { Text("Back") }
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
            TextInputField(
                label = "Load name",
                value = uiState.value.name,
                onValueChange = viewModel::onNameChanged,
            )
            TextInputField(
                label = "Caliber",
                value = uiState.value.caliber,
                onValueChange = viewModel::onCaliberChanged,
            )
            EntityDropdown(
                label = "Powder",
                options = powders.value,
                selectedId = uiState.value.powderId,
                optionLabel = { it.name },
                onSelected = viewModel::onPowderChanged,
            )
            DecimalNumberInputField(
                label = "Charge weight (grains)",
                value = uiState.value.chargeWeightGr,
                onValueChange = viewModel::onChargeWeightChanged,
            )
            EntityDropdown(
                label = "Primer",
                options = primers.value,
                selectedId = uiState.value.primerId,
                optionLabel = { it.name },
                onSelected = viewModel::onPrimerChanged,
            )
            EntityDropdown(
                label = "Bullet",
                options = bullets.value,
                selectedId = uiState.value.bulletId,
                optionLabel = { it.name },
                onSelected = viewModel::onBulletChanged,
            )
            EntityDropdown(
                label = "Brass",
                options = brass.value,
                selectedId = uiState.value.brassId,
                optionLabel = { it.name },
                onSelected = viewModel::onBrassChanged,
            )
            OutlinedTextField(
                value = uiState.value.notes,
                onValueChange = viewModel::onNotesChanged,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
            )
            uiState.value.errorMessage?.let {
                Text(text = it)
            }
            Button(
                onClick = { viewModel.save(onSaved) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun <T> EntityDropdown(
    label: String,
    options: List<T>,
    selectedId: Long?,
    optionLabel: (T) -> String,
    onSelected: (Long?) -> Unit,
) where T : Any {
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = options.firstOrNull { it.getId() == selectedId }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption?.let(optionLabel).orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(),
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelected(option.getId())
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun Any.getId(): Long = when (this) {
    is PowderEntity -> id
    is PrimerEntity -> id
    is BulletEntity -> id
    is BrassEntity -> id
    else -> 0L
}
