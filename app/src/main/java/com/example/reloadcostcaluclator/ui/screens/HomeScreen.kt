package com.example.reloadcostcaluclator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reloadcostcaluclator.data.repository.BrassRepository
import com.example.reloadcostcaluclator.data.repository.BulletRepository
import com.example.reloadcostcaluclator.data.repository.FactoryComparisonRepository
import com.example.reloadcostcaluclator.data.repository.LoadRecipeRepository
import com.example.reloadcostcaluclator.data.repository.PowderRepository
import com.example.reloadcostcaluclator.data.repository.PrimerRepository
import com.example.reloadcostcaluclator.util.CurrencyFormatters
import com.example.reloadcostcaluclator.viewmodel.loadrecipes.LoadCostSummaryViewModel

private val DashboardBackground = Color(0xFF0E1116)
private val SectionCardColor = Color(0xFF161B22)
private val AccentBrass = Color(0xFFD3A64A)
private val AccentBrassMuted = Color(0xFF7E6738)
private val BodyText = Color(0xFFD7DCE4)
private val SubtleText = Color(0xFF8F98A7)

private data class HomeCardItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    loadRecipeRepository: LoadRecipeRepository,
    powderRepository: PowderRepository,
    primerRepository: PrimerRepository,
    bulletRepository: BulletRepository,
    brassRepository: BrassRepository,
    factoryComparisonRepository: FactoryComparisonRepository,
    onComponentsClick: () -> Unit,
    onLoadsToolsClick: () -> Unit,
    onCalculatorClick: () -> Unit,
    onLoadCostSummaryClick: () -> Unit,
    viewModel: LoadCostSummaryViewModel = viewModel(
        factory = LoadCostSummaryViewModel.provideFactory(
            loadRecipeRepository = loadRecipeRepository,
            powderRepository = powderRepository,
            primerRepository = primerRepository,
            bulletRepository = bulletRepository,
            brassRepository = brassRepository,
            factoryComparisonRepository = factoryComparisonRepository,
        ),
    ),
) {
    val summaryItems = viewModel.summaryItems.collectAsStateWithLifecycle()

    val dashboardCards = listOf(
        HomeCardItem(
            title = "Quick Cost Calculator",
            subtitle = "Instant per-round and batch pricing",
            icon = Icons.Filled.Workspaces,
            onClick = onCalculatorClick,
        ),
        HomeCardItem(
            title = "Load Cost Summary",
            subtitle = "Review every saved recipe cost",
            icon = Icons.Filled.Calculate,
            onClick = onLoadCostSummaryClick,
        ),
        HomeCardItem(
            title = "Components",
            subtitle = "Manage powders, primers, bullets, and brass",
            icon = Icons.Filled.Science,
            onClick = onComponentsClick,
        ),
        HomeCardItem(
            title = "Loads / Tools",
            subtitle = "Build and tune load recipes",
            icon = Icons.Filled.Dataset,
            onClick = onLoadsToolsClick,
        ),
    )

    Scaffold(
        containerColor = DashboardBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reloading Cost Calculator",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DashboardBackground,
                    titleContentColor = Color.White,
                ),
            )
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(2) }) {
                DashboardHeader()
            }

            items(dashboardCards.size) { index ->
                HomeActionCard(item = dashboardCards[index])
            }

            item(span = { GridItemSpan(2) }) {
                LoadCostSummaryCard(
                    loadCostSummaries = summaryItems.value,
                    onShowAllClick = onLoadCostSummaryClick,
                )
            }

            item(span = { GridItemSpan(2) }) {
                Box(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DashboardHeader() {
    Column(
        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "Precision Reloading Dashboard",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Quick actions, full-cost visibility, and premium component management.",
            color = SubtleText,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun LoadCostSummaryCard(
    loadCostSummaries: List<LoadCostSummaryViewModel.LoadCostSummaryItemUi>,
    onShowAllClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SectionCardColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Load Cost Summary",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            if (loadCostSummaries.isEmpty()) {
                Text(
                    text = "No saved loads yet. Add a load recipe to see cost summaries here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BodyText,
                )
            } else {
                val topItems = loadCostSummaries.take(5)
                topItems.forEachIndexed { index, item ->
                    LoadSummaryRow(item = item)
                    if (index != topItems.lastIndex) {
                        Divider(color = Color(0xFF27303C))
                    }
                }

                if (loadCostSummaries.size > 5) {
                    Button(
                        onClick = onShowAllClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBrassMuted,
                            contentColor = Color.White,
                        ),
                    ) {
                        Text("Show All")
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadSummaryRow(
    item: LoadCostSummaryViewModel.LoadCostSummaryItemUi,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.loadName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
            Text(
                text = item.caliber,
                style = MaterialTheme.typography.bodySmall,
                color = SubtleText,
            )
        }
        Text(
            text = "${item.grain} gr",
            style = MaterialTheme.typography.bodySmall,
            color = BodyText,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Each: ${CurrencyFormatters.formatUsd(item.costPerEach)}",
                style = MaterialTheme.typography.bodySmall,
                color = BodyText,
            )
            Text(
                text = "50: ${CurrencyFormatters.formatUsd(item.costPer50)}",
                style = MaterialTheme.typography.bodySmall,
                color = SubtleText,
            )
            Text(
                text = "100: ${CurrencyFormatters.formatUsd(item.costPer100)}",
                style = MaterialTheme.typography.bodySmall,
                color = SubtleText,
            )
        }
    }
}

@Composable
private fun HomeActionCard(
    item: HomeCardItem,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick),
        colors = CardDefaults.cardColors(containerColor = SectionCardColor),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(AccentBrassMuted),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = AccentBrass,
                    modifier = Modifier.size(18.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = BodyText,
                )
            }
        }
    }
}
