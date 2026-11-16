package com.redmagic.gamespace.ui.theme

import android.app.Activity
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
    primaryContainer = RedMagicRedDark,
    onPrimaryContainer = Color.White,
    secondary = RedMagicCyan,
    onSecondary = Color.Black,
    tertiary = RedMagicPurple,
    background = RedMagicBlack,
    onBackground = TextPrimary,
    surface = RedMagicSurface,
    onSurface = TextPrimary,
    surfaceVariant = RedMagicCard,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF333333),
    error = ErrorRed
)

@Composable
fun REDMAGICGameSpaceTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = RedMagicDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = RedMagicBlack.toArgb()
            window.navigationBarColor = RedMagicBlack.toArgb()
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
