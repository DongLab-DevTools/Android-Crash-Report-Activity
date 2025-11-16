package com.donglab.crashviewer.ui.theme

import android.app.Activity
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
    primary = GeminiDarkBlue,
    onPrimary = Color.Black,
    primaryContainer = GeminiDarkPurple,
    onPrimaryContainer = Color.Black,
    secondary = GeminiDarkPurple,
    onSecondary = Color.Black,
    tertiary = GeminiLightPurple,
    onTertiary = Color.Black,
    error = GeminiRed,
    onError = Color.White,
    background = GeminiDarkBackground,
    onBackground = GeminiDarkOnBackground,
    surface = GeminiDarkSurface,
    onSurface = GeminiDarkOnSurface,
)

private val LightColorScheme = lightColorScheme(
    primary = GeminiBlue,
    onPrimary = Color.White,
    primaryContainer = GeminiLightBlue,
    onPrimaryContainer = Color.Black,
    secondary = GeminiPurple,
    onSecondary = Color.White,
    tertiary = GeminiLightPurple,
    onTertiary = Color.Black,
    error = GeminiRed,
    onError = Color.White,
    background = GeminiBackground,
    onBackground = GeminiOnBackground,
    surface = GeminiSurface,
    onSurface = GeminiOnSurface,
)

@Composable
fun CrashViewerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to use Gemini colors
    dynamicColor: Boolean = false,
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