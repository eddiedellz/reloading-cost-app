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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    onPowdersClick: () -> Unit,
    onPrimersClick: () -> Unit,
    onBulletsClick: () -> Unit,
    onBrassClick: () -> Unit,
    onLoadsClick: () -> Unit,
    onCalculatorClick: () -> Unit,
) {
    val manageComponentsCards = listOf(
        HomeCardItem("Powders", "Manage burn rates and cost", Icons.Filled.Science, onPowdersClick),
        HomeCardItem("Primers", "Track stock and pricing", Icons.Filled.WaterDrop, onPrimersClick),
        HomeCardItem("Bullets", "Catalog weight and profile", Icons.Filled.Tune, onBulletsClick),
        HomeCardItem("Brass", "Monitor lot life and reuse", Icons.Filled.Inventory2, onBrassClick),
    )

    val toolsCards = listOf(
        HomeCardItem("Loads", "View and edit load recipes", Icons.Filled.Dataset, onLoadsClick),
        HomeCardItem("Calculator", "Calculate per-round cost", Icons.Filled.Calculate, onCalculatorClick),
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
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                DashboardHeader()
            }

            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                FeaturedSummaryCard(onCalculatorClick = onCalculatorClick)
            }

            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                SectionHeader(title = "Manage Components", subtitle = "Inventory and pricing")
            }

            items(manageComponentsCards.size) { index ->
                HomeActionCard(item = manageComponentsCards[index])
            }

            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                SectionHeader(title = "Loads & Tools", subtitle = "Recipes and calculations")
            }

            items(toolsCards.size) { index ->
                HomeActionCard(item = toolsCards[index])
            }

            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
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
            text = "Manage your components, optimize costs, and build consistent loads.",
            color = SubtleText,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun FeaturedSummaryCard(
    onCalculatorClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCalculatorClick),
        colors = CardDefaults.cardColors(containerColor = SectionCardColor),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AccentBrassMuted),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Workspaces,
                    contentDescription = null,
                    tint = AccentBrass,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Quick Cost Calculator",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Jump into calculations with your latest component pricing.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BodyText,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
) {
    Column(
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = subtitle,
            color = SubtleText,
            style = MaterialTheme.typography.bodySmall,
        )
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

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = item.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = item.subtitle,
                    color = BodyText,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
