package com.example.teacare.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TeaGreenPrimary,
    onPrimary = TeaSurface,
    primaryContainer = TeaGreenLight,
    onPrimaryContainer = TeaGreenDark,
    secondary = TeaGreenSecondary,
    onSecondary = TeaSurface,
    secondaryContainer = TeaSurfaceVariant,
    onSecondaryContainer = TeaTextPrimary,
    background = TeaBackground,
    onBackground = TeaTextPrimary,
    surface = TeaSurface,
    onSurface = TeaTextPrimary,
    surfaceVariant = TeaSurfaceVariant,
    onSurfaceVariant = TeaTextSecondary,
    outline = TeaBorder,
    error = TeaError,
    onError = TeaSurface,
    errorContainer = TeaErrorBg,
    onErrorContainer = TeaError
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkTeaPrimary,
    onPrimary = DarkTeaBackground,
    primaryContainer = DarkTeaSurfaceVariant,
    onPrimaryContainer = DarkTeaPrimary,
    secondary = DarkTeaPrimary,
    onSecondary = DarkTeaBackground,
    secondaryContainer = DarkTeaSurfaceVariant,
    onSecondaryContainer = DarkTeaTextPrimary,
    background = DarkTeaBackground,
    onBackground = DarkTeaTextPrimary,
    surface = DarkTeaSurface,
    onSurface = DarkTeaTextPrimary,
    surfaceVariant = DarkTeaSurfaceVariant,
    onSurfaceVariant = DarkTeaTextSecondary,
    outline = DarkTeaBorder,
    error = TeaError,
    onError = TeaSurface,
    errorContainer = TeaErrorBg,
    onErrorContainer = TeaError
)

@Composable
fun TeaCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
