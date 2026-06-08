package com.helwa.lifemanager.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.helwa.lifemanager.settings.ThemeMode

private val LightColors = lightColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    primaryContainer = Lilac,
    onPrimaryContainer = IndigoDeep,
    secondary = BlueDeep,
    onSecondary = Color.White,
    background = SurfaceLight,
    onBackground = IndigoDeep,
    surface = Color.White,
    onSurface = IndigoDeep,
    surfaceVariant = Lilac,
    onSurfaceVariant = Color(0xFF4A4A6A)
)

private val DarkColors = darkColorScheme(
    primary = PurpleLight,
    onPrimary = IndigoDeep,
    primaryContainer = PurpleDark,
    onPrimaryContainer = Color.White,
    secondary = PurpleLight,
    onSecondary = IndigoDeep,
    background = SurfaceDark,
    onBackground = Color(0xFFEDEBFF),
    surface = CardDark,
    onSurface = Color(0xFFEDEBFF),
    surfaceVariant = Color(0xFF2C2952),
    onSurfaceVariant = Color(0xFFC9C5E8)
)

@Composable
fun LifeManagerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val dark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colors = if (dark) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !dark
        }
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
