package com.example.concretecalculator.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.concretecalculator.data.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = AmberAccentDark,
    onPrimary = Graphite950,
    primaryContainer = AmberContainerDark,
    onPrimaryContainer = AmberAccentDark,
    secondary = Slate400,
    onSecondary = Graphite950,
    secondaryContainer = Graphite700,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = Slate200,
    onTertiary = Graphite950,
    background = Graphite950,
    onBackground = TextPrimaryDark,
    surface = Graphite900,
    onSurface = TextPrimaryDark,
    surfaceVariant = Graphite800,
    onSurfaceVariant = TextSecondaryDark,
    outline = Graphite700,
    outlineVariant = Graphite600,
    error = ErrorRedDark,
    onError = Graphite950
)

private val LightColorScheme = lightColorScheme(
    primary = AmberAccent,
    onPrimary = ConcreteWhite,
    primaryContainer = AmberContainerLight,
    onPrimaryContainer = Slate900,
    secondary = Slate700,
    onSecondary = ConcreteWhite,
    secondaryContainer = Slate100,
    onSecondaryContainer = Slate900,
    tertiary = Slate800,
    onTertiary = ConcreteWhite,
    background = ConcreteWhite,
    onBackground = TextPrimaryLight,
    surface = ConcreteSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = ConcreteCard,
    onSurfaceVariant = TextSecondaryLight,
    outline = ConcreteBorder,
    outlineVariant = Slate200,
    error = ErrorRedLight,
    onError = ConcreteWhite
)

@Composable
fun ConcreteCalculatorTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                @Suppress("DEPRECATION")
                window.statusBarColor = colorScheme.background.toArgb()
                @Suppress("DEPRECATION")
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}