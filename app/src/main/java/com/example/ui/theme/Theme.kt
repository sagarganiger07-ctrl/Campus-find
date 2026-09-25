package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CampusNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = CampusGold,
    onSecondary = Color(0xFF78350F),
    secondaryContainer = CampusGoldLight,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = CampusEmerald,
    onTertiary = Color.White,
    tertiaryContainer = CampusEmeraldLight,
    onTertiaryContainer = Color(0xFF065F46),
    error = CampusCoral,
    onError = Color.White,
    errorContainer = CampusCoralLight,
    onErrorContainer = Color(0xFF991B1B),
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate200
)

private val DarkColorScheme = darkColorScheme(
    primary = CampusNavyLight,
    onPrimary = CampusNavyDark,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = CampusGold,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = CampusGoldLight,
    tertiary = CampusEmerald,
    onTertiary = Color(0xFF064E3B),
    tertiaryContainer = Color(0xFF065F46),
    onTertiaryContainer = CampusEmeraldLight,
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF991B1B),
    onErrorContainer = CampusCoralLight,
    background = Color(0xFF0B0F19),
    onBackground = Slate100,
    surface = Color(0xFF131B2E),
    onSurface = Slate100,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
