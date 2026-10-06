package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PresidentialGreenLight,
    onPrimary = Color.Black,
    primaryContainer = PresidentialGreenDark,
    onPrimaryContainer = PresidentialGoldLight,
    secondary = PresidentialGold,
    onSecondary = Color.Black,
    secondaryContainer = SlateNavyCardElevated,
    onSecondaryContainer = PresidentialGoldLight,
    tertiary = InfoBlue,
    background = SlateNavyDark,
    onBackground = TextWhite,
    surface = SlateNavyCard,
    onSurface = TextWhite,
    surfaceVariant = SlateNavyCardElevated,
    onSurfaceVariant = TextMuted,
    outline = SlateBorder,
    error = AlertRed
)

private val LightColorScheme = lightColorScheme(
    primary = PresidentialGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = PresidentialGreenDark,
    secondary = PresidentialGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = InfoBlue,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = AlertRedDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek executive dark theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
