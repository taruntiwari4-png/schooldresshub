package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SchoolNavyLight,
    onPrimary = Color.White,
    primaryContainer = SchoolNavy,
    onPrimaryContainer = Color.White,
    secondary = SchoolAmber,
    onSecondary = Slate900,
    secondaryContainer = SchoolGold,
    tertiary = SchoolEmerald,
    background = Slate900,
    surface = Slate800,
    surfaceVariant = Slate700,
    onBackground = Slate50,
    onSurface = Slate50,
    onSurfaceVariant = Slate200
)

private val LightColorScheme = lightColorScheme(
    primary = SchoolNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EEF8),
    onPrimaryContainer = SchoolNavyDark,
    secondary = SchoolGold,
    onSecondary = Color.White,
    secondaryContainer = SchoolAmberLight,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = SchoolEmerald,
    onTertiary = Color.White,
    background = Slate50,
    surface = Color.White,
    surfaceVariant = Slate100,
    onBackground = Slate900,
    onSurface = Slate900,
    onSurfaceVariant = Slate700
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our brand colors for cohesive marketplace experience
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
