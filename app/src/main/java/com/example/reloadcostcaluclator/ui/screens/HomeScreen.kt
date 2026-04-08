package com.example.reloadcostcaluclator.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPowdersClick: () -> Unit,
    onPrimersClick: () -> Unit,
    onBulletsClick: () -> Unit,
    onBrassClick: () -> Unit,
    onLoadsClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Reloading Components") })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HomeNavCard(title = "Powders", onClick = onPowdersClick)
            HomeNavCard(title = "Primers", onClick = onPrimersClick)
            HomeNavCard(title = "Bullets", onClick = onBulletsClick)
            HomeNavCard(title = "Brass", onClick = onBrassClick)
            HomeNavCard(title = "Loads", onClick = onLoadsClick)
        }
    }
}

@Composable
private fun HomeNavCard(
    title: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(20.dp),
        )
    }
}
