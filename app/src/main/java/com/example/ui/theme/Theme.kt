package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PixelYellow,
    onPrimary = Color(0xFF3B2F00),
    primaryContainer = PixelYellowContainer,
    onPrimaryContainer = PixelOnYellowContainer,
    secondary = PixelBlue,
    onSecondary = Color(0xFF043063),
    secondaryContainer = Color(0xFF1E3A5F),
    onSecondaryContainer = Color(0xFFD2E3FF),
    tertiary = PixelGreen,
    onTertiary = Color(0xFF00391A),
    background = PixelDarkBackground,
    onBackground = PixelOnSurface,
    surface = PixelDarkSurface,
    onSurface = PixelOnSurface,
    surfaceVariant = PixelDarkSurfaceVariant,
    onSurfaceVariant = PixelOnSurfaceVariant,
    error = PixelRed,
    onError = Color(0xFF601410)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Camera apps look best in dark immersive theme
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicDarkColorScheme(context)
        }
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
