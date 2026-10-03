package com.code4galaxy.vaaniverse4u.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

// These foreground/background pairs meet WCAG AA for normal-sized text.
private val LingoGreen = Color(0xFF007A3D)
private val LingoGreenDark = Color(0xFF62DC91)
private val LingoBlue = Color(0xFF2563EB)
private val LingoLightSurface = Color(0xFFF8FAFC)

private val LightColors = lightColorScheme(
    primary = LingoGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8F7D7),
    onPrimaryContainer = Color(0xFF003919),
    secondary = LingoBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCE8FF),
    onSecondaryContainer = Color(0xFF001A41),
    tertiary = Color(0xFFB45309),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE1C3),
    onTertiaryContainer = Color(0xFF341100),
    background = LingoLightSurface,
    surface = Color.White,
    surfaceVariant = Color(0xFFEAF1EB),
    outline = Color(0xFF6E7C71),
)

private val DarkColors = darkColorScheme(
    primary = LingoGreenDark,
    onPrimary = Color(0xFF003919),
    primaryContainer = Color(0xFF005227),
    onPrimaryContainer = Color(0xFFC8F7D7),
    secondary = Color(0xFFB5C8FF),
    onSecondary = Color(0xFF002E6B),
    secondaryContainer = Color(0xFF174A94),
    tertiary = Color(0xFFFFB77D),
    onTertiary = Color(0xFF4A2800),
    tertiaryContainer = Color(0xFF683900),
    onTertiaryContainer = Color(0xFFFFDCC2),
    background = Color(0xFF101512),
    surface = Color(0xFF161D18),
    surfaceVariant = Color(0xFF28332B),
)

private val HighContrastLightColors = lightColorScheme(
    primary = Color(0xFF005F2F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8F7D7),
    onPrimaryContainer = Color(0xFF002A13),
    secondary = Color(0xFF174EA6),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCE8FF),
    onSecondaryContainer = Color(0xFF001A41),
    tertiary = Color(0xFF8A3B00),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE1C3),
    onTertiaryContainer = Color(0xFF341100),
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Color(0xFFE8F1E9),
    onSurface = Color(0xFF111411),
    onSurfaceVariant = Color(0xFF354038),
    outline = Color(0xFF354038),
)

private val HighContrastDarkColors = darkColorScheme(
    primary = Color(0xFFA5FFBD),
    onPrimary = Color(0xFF00210F),
    primaryContainer = Color(0xFF005227),
    onPrimaryContainer = Color(0xFFE1FFE6),
    secondary = Color(0xFFD5E2FF),
    onSecondary = Color(0xFF001D42),
    secondaryContainer = Color(0xFF174A94),
    onSecondaryContainer = Color(0xFFE0E7FF),
    tertiary = Color(0xFFFFDCC2),
    onTertiary = Color(0xFF4A2800),
    tertiaryContainer = Color(0xFF683900),
    onTertiaryContainer = Color(0xFFFFE2CB),
    background = Color(0xFF101512),
    surface = Color(0xFF101512),
    surfaceVariant = Color(0xFF202820),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFD9E5DA),
    outline = Color(0xFFD9E5DA),
)

@Composable
fun VaaniVerse4UTheme(
    // The illustrated product surfaces are intentionally light. Dark system mode previously
    // inverted Material foreground tokens over those fixed light cards, reducing contrast.
    // A future user-selected dark palette can opt in explicitly through this parameter.
    darkTheme: Boolean = false,
    highContrast: Boolean = false,
    content: @Composable () -> Unit,
) = MaterialTheme(
    colorScheme = when {
        highContrast && darkTheme -> HighContrastDarkColors
        highContrast -> HighContrastLightColors
        darkTheme -> DarkColors
        else -> LightColors
    },
    content = content,
)

/** Scales app text while preserving the user's system font scale as the baseline. */
@Composable
fun VaaniVerse4UTextScale(scale: Float, content: @Composable () -> Unit) {
    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = density.density,
            fontScale = (density.fontScale * scale).coerceIn(0.85f, 1.6f),
        ),
        content = content,
    )
}
