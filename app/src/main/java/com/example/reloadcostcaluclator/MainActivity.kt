package com.example.reloadcostcaluclator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.reloadcostcaluclator.data.di.AppContainer
import com.example.reloadcostcaluclator.ui.navigation.AppNavHost
import com.example.reloadcostcaluclator.ui.theme.ReloadCostCaluclatorTheme

class MainActivity : ComponentActivity() {
    private val appContainer: AppContainer by lazy { AppContainer(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReloadCostCaluclatorTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost(appContainer = appContainer)
                }
            }
        }
    }
}
