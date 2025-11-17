package com.donglab.crashviewer.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = GeminiPrimary,
    onPrimary = GeminiOnPrimary,
    background = GeminiLightBackground,
    onBackground = GeminiLightOnBackground,
    surface = GeminiLightSurface,
    onSurface = GeminiLightOnSurface,
    surfaceVariant = GeminiLightSurfaceVariant,
    onSurfaceVariant = GeminiLightOnSurfaceVariant,
    outline = GeminiLightOutline
)

@Composable
fun CrashViewerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
