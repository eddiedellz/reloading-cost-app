package com.example.reloadcostcaluclator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DashboardDarkColorScheme = darkColorScheme(
    primary = MutedBrass,
    onPrimary = DeepInk,
    primaryContainer = MutedBrassContainer,
    onPrimaryContainer = SoftWhite,

    secondary = CoolGray,
    onSecondary = DeepInk,
    secondaryContainer = ElevatedSlate,
    onSecondaryContainer = SoftWhite,

    tertiary = Color(0xFF8AA3C0),
    onTertiary = DeepInk,
    tertiaryContainer = Color(0xFF203041),
    onTertiaryContainer = SoftWhite,

    background = NightBlack,
    onBackground = SoftWhite,
    surface = CharcoalBlue,
    onSurface = SoftWhite,
    surfaceVariant = SlateSurface,
    onSurfaceVariant = CoolGray,
    surfaceTint = MutedBrass,

    inverseSurface = SoftWhite,
    inverseOnSurface = DeepInk,
    inversePrimary = MutedBrassContainer,

    outline = Color(0xFF4F5C6D),
    outlineVariant = Color(0xFF374353),
    scrim = Color(0xFF000000),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun ReloadCostCaluclatorTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DashboardDarkColorScheme,
        typography = Typography,
        content = content
    )
}
