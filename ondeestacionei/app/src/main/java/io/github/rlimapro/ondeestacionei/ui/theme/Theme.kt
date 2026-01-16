package io.github.rlimapro.ondeestacionei.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Blue60,
    onPrimary = Color.White,
    primaryContainer = Blue80.copy(alpha = 0.2f),
    onPrimaryContainer = Blue80,

    secondary = Green60,
    onSecondary = Color.White,
    secondaryContainer = Green80.copy(alpha = 0.2f),
    onSecondaryContainer = Green80,

    tertiary = Orange60,
    onTertiary = Color.White,
    tertiaryContainer = Orange80.copy(alpha = 0.2f),
    onTertiaryContainer = Orange80,

    background = Grey20,
    onBackground = Grey90,

    surface = Grey30,
    onSurface = Grey90,
    surfaceVariant = Grey30,
    onSurfaceVariant = Grey80,

    error = Color(0xFFCF6679),
    onError = Color(0xFF000000),

    outline = Grey70,
    outlineVariant = Grey50
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color.White,
    primaryContainer = Blue80,
    onPrimaryContainer = Blue20,

    secondary = Green40,
    onSecondary = Color.White,
    secondaryContainer = Green80,
    onSecondaryContainer = Green40,

    tertiary = Orange40,
    onTertiary = Color.White,
    tertiaryContainer = Orange80,
    onTertiaryContainer = Orange40,

    background = Color(0xFFFAFAFA),
    onBackground = Grey20,

    surface = Color.White,
    onSurface = Grey20,
    surfaceVariant = Grey90,
    onSurfaceVariant = Grey50,

    error = Color(0xFFB00020),
    onError = Color.White,

    outline = Grey70,
    outlineVariant = Grey80
)

@Composable
fun OndeEstacioneiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}