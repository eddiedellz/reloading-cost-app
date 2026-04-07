package com.example.reloadcostcaluclator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.reloadcostcaluclator.ui.LoadCostCalculatorScreen
import com.example.reloadcostcaluclator.ui.theme.ReloadCostCaluclatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReloadCostCaluclatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoadCostCalculatorScreen(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
