package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HeraLightColorScheme = lightColorScheme(
    primary = HeraPrimary,
    onPrimary = HeraOnPrimary,
    primaryContainer = HeraPrimaryContainer,
    onPrimaryContainer = HeraOnPrimaryContainer,
    secondary = HeraSecondary,
    onSecondary = HeraOnSecondary,
    secondaryContainer = HeraSecondaryContainer,
    onSecondaryContainer = HeraOnSecondaryContainer,
    tertiary = HeraTertiary,
    onTertiary = HeraOnTertiary,
    tertiaryContainer = HeraTertiaryContainer,
    onTertiaryContainer = HeraOnTertiaryContainer,
    background = HeraSurface,
    onBackground = HeraOnSurface,
    surface = HeraSurface,
    onSurface = HeraOnSurface,
    surfaceVariant = HeraSurfaceContainer,
    onSurfaceVariant = HeraOnSurfaceVariant,
    outline = HeraOutline,
    outlineVariant = HeraOutlineVariant,
    error = HeraError,
    onError = HeraOnError,
    errorContainer = HeraErrorContainer,
    onErrorContainer = HeraOnErrorContainer,
    inverseSurface = HeraInverseSurface,
    inverseOnSurface = HeraInverseOnSurface
)

private val HeraDarkColorScheme = darkColorScheme(
    primary = HeraPrimaryFixedDim,
    onPrimary = HeraOnSurface,
    primaryContainer = HeraPrimary,
    onPrimaryContainer = HeraOnPrimaryContainer,
    secondary = HeraSecondaryFixed,
    onSecondary = HeraOnSurface,
    secondaryContainer = HeraSecondary,
    onSecondaryContainer = HeraOnSecondaryContainer,
    tertiary = HeraTertiaryFixed,
    onTertiary = HeraOnSurface,
    background = HeraInverseSurface,
    onBackground = HeraInverseOnSurface,
    surface = HeraInverseSurface,
    onSurface = HeraInverseOnSurface,
    error = HeraErrorContainer,
    onError = HeraOnErrorContainer
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use Shakthi.ai custom brand palette
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) HeraDarkColorScheme else HeraLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
