package com.redmagic.gamespace.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val RedMagicDarkColorScheme = darkColorScheme(
    primary = RedMagicRed,
    onPrimary = Color.White,
    primaryContainer = RedMagicAccent,
    onPrimaryContainer = Color.White,
    secondary = RedMagicCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF003D40),
    onSecondaryContainer = RedMagicCyan,
    tertiary = RedMagicPurple,
    onTertiary = Color.White,
    background = RedMagicDark,
    onBackground = TextPrimary,
    surface = RedMagicCard,
    onSurface = TextPrimary,
    surfaceVariant = RedMagicCardElevated,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFF3A3A3A),
    outlineVariant = Color(0xFF2A2A2A)
)

@Composable
fun REDMAGICGameSpaceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = RedMagicDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = RedMagicDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
