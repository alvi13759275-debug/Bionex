package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BioCyanPrimary,
    onPrimary = BioDarkBackground,
    primaryContainer = BioDarkBorder,
    onPrimaryContainer = BioCyanLight,
    secondary = BioEmeraldSecondary,
    onSecondary = BioDarkBackground,
    secondaryContainer = BioDarkBorder,
    onSecondaryContainer = BioEmeraldLight,
    tertiary = BioPurpleTertiary,
    onTertiary = BioDarkBackground,
    background = BioDarkBackground,
    onBackground = TextPrimary,
    surface = BioDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = BioDarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BioDarkBorder,
    error = BioCoralError,
    onError = BioDarkBackground
)

// For an elite scientific research platform, the dark theme provides optimal contrast and laboratory ambiance
private val LightColorScheme = DarkColorScheme

@Composable
fun BioNexusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
