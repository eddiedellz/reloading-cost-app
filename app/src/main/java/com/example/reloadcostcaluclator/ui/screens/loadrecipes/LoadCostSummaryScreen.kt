package com.example.reloadcostcaluclator.ui.screens.loadrecipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reloadcostcaluclator.data.repository.BrassRepository
import com.example.reloadcostcaluclator.data.repository.BulletRepository
import com.example.reloadcostcaluclator.data.repository.LoadRecipeRepository
import com.example.reloadcostcaluclator.data.repository.PowderRepository
import com.example.reloadcostcaluclator.data.repository.PrimerRepository
import com.example.reloadcostcaluclator.util.CurrencyFormatters
import com.example.reloadcostcaluclator.viewmodel.loadrecipes.LoadCostSummaryViewModel

private val DashboardBackground = Color(0xFF0E1116)
private val SectionCardColor = Color(0xFF161B22)
private val BodyText = Color(0xFFD7DCE4)
private val SubtleText = Color(0xFF8F98A7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadCostSummaryScreen(
    loadRecipeRepository: LoadRecipeRepository,
    powderRepository: PowderRepository,
    primerRepository: PrimerRepository,
    bulletRepository: BulletRepository,
    brassRepository: BrassRepository,
    onBackClick: () -> Unit,
    viewModel: LoadCostSummaryViewModel = viewModel(
        factory = LoadCostSummaryViewModel.provideFactory(
            loadRecipeRepository = loadRecipeRepository,
            powderRepository = powderRepository,
            primerRepository = primerRepository,
            bulletRepository = bulletRepository,
            brassRepository = brassRepository,
        ),
    ),
) {
    val summaryItems = viewModel.summaryItems.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = DashboardBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Load Cost Summary",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DashboardBackground,
                    titleContentColor = Color.White,
                ),
            )
        },
    ) { innerPadding ->
        if (summaryItems.value.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "No saved loads found.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )
                Text(
                    text = "Create a load recipe to see full cost summaries here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BodyText,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Text(
                        text = "All saved loads",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                items(summaryItems.value, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SectionCardColor),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = item.loadName,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "Each: ${CurrencyFormatters.formatUsd(item.costPerEach)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BodyText,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "50: ${CurrencyFormatters.formatUsd(item.costPer50)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SubtleText,
                                )
                                Text(
                                    text = "1000: ${CurrencyFormatters.formatUsd(item.costPer1000)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SubtleText,
                                )
                            }
                        }
                    }
                }
                item {
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = 10.dp))
                }
            }
        }
    }
}
