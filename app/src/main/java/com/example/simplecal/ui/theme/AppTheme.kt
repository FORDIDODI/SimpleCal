package com.example.simplecal.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

class ThemeState(
    initialTheme: ThemeMode = ThemeMode.SYSTEM,
    private val onThemeUpdated: (ThemeMode) -> Unit = {}
) {
    var theme by mutableStateOf(initialTheme)
        private set

    fun updateTheme(newTheme: ThemeMode) {
        theme = newTheme
        onThemeUpdated(newTheme)
    }
}

val LocalTheme = compositionLocalOf<ThemeState> { error("Theme state not initialized") }

@Composable
fun rememberThemeState(
    initialTheme: ThemeMode = ThemeMode.SYSTEM,
    onThemeUpdated: (ThemeMode) -> Unit = {}
): ThemeState {
    return remember { ThemeState(initialTheme, onThemeUpdated) }
}

@Composable
fun AppTheme(
    themeState: ThemeState = rememberThemeState(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeState.theme) {
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = colorScheme == LightColorScheme
        }
    }

    CompositionLocalProvider(
        LocalTheme provides themeState
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
