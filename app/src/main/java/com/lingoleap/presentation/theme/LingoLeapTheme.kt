package com.lingoleap.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme

private val LingoGreen = Color(0xFF149B50)
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
    tertiary = Color(0xFFFF8A00),
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
    background = Color(0xFF101512),
    surface = Color(0xFF161D18),
    surfaceVariant = Color(0xFF28332B),
)

@Composable
fun LingoLeapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
