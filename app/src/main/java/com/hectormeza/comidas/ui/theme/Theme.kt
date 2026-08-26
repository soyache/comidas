package com.hectormeza.comidas.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = BurgundyDark,
    primaryContainer = BurgundyContainer,
    onPrimaryContainer = Color.White,
    secondary = CoralAccent,
    onSecondary = Color.White,
    tertiary = BreakfastAccent,
    background = BurgundyDeep,
    surface = BurgundyContainer,
    surfaceVariant = Color(0xFF2D1120),
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFC4B8BF),
    outline = Color(0xFF4A253A)
)

private val LightColorScheme = lightColorScheme(
    primary = BurgundyDark,
    onPrimary = Color.White,
    primaryContainer = SoftCardBg,
    onPrimaryContainer = BurgundyText,
    secondary = CoralAccent,
    onSecondary = Color.White,
    tertiary = BreakfastAccent,
    background = WarmBackground,
    surface = PureWhite,
    surfaceVariant = SoftCardBg,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
)

@Composable
fun ComidasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}