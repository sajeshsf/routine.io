package com.projectgarage.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BurntOrange,
    secondary = MetallicBlue,
    tertiary = SlateGrey,
    background = GarageBlack,
    surface = GarageSurface,
    surfaceVariant = GarageSurfaceVariant,
    onPrimary = GarageBlack,
    onSecondary = GarageBlack,
    onTertiary = GarageTextPrimary,
    onBackground = GarageTextPrimary,
    onSurface = GarageTextPrimary,
    onSurfaceVariant = GarageTextSecondary
)

@Composable
fun ProjectGarageTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
