package com.example.matrixsolver.ui.theme

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

// Pastel green theme with good contrast
private val LightColorScheme = lightColorScheme(
    // Deep muted green for primary — white text reads well on it
    primary = Color(0xFF4A7C59),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4EDDA),
    onPrimaryContainer = Color(0xFF1B4332),
    // Soft grey-green secondary
    secondary = Color(0xFF6B8F71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = Color(0xFF2D5A3D),
    // Muted teal tertiary
    tertiary = Color(0xFF5B8A72),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1E8DD),
    onTertiaryContainer = Color(0xFF1A3D2E),
    // Error
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    // Background — warm off-white
    background = Color(0xFFFAFAF5),
    onBackground = Color(0xFF1C1C1A),
    surface = Color(0xFFFAFAF5),
    onSurface = Color(0xFF1C1C1A),
    surfaceVariant = Color(0xFFE8EDE9),
    onSurfaceVariant = Color(0xFF3D4F42),
    outline = Color(0xFF8A9B8E),
    outlineVariant = Color(0xFFD5DDD7),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF2E302E),
    inverseOnSurface = Color(0xFFF0F0ED),
    inversePrimary = Color(0xFFA5D6A7)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF1B4332),
    primaryContainer = Color(0xFF2E5A3D),
    onPrimaryContainer = Color(0xFFD4EDDA),
    secondary = Color(0xFFA5D6A7),
    onSecondary = Color(0xFF1B4332),
    secondaryContainer = Color(0xFF2D5A3D),
    onSecondaryContainer = Color(0xFFE8F5E9),
    tertiary = Color(0xFF80CBC4),
    onTertiary = Color(0xFF003731),
    tertiaryContainer = Color(0xFF1A3D2E),
    onTertiaryContainer = Color(0xFFD1E8DD),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = Color(0xFF121412),
    onBackground = Color(0xFFE0E0DC),
    surface = Color(0xFF121412),
    onSurface = Color(0xFFE0E0DC),
    surfaceVariant = Color(0xFF2E3530),
    onSurfaceVariant = Color(0xFFB0BEB3),
    outline = Color(0xFF6B7D6F),
    outlineVariant = Color(0xFF3D4F42),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE0E0DC),
    inverseOnSurface = Color(0xFF2E302E),
    inversePrimary = Color(0xFF4A7C59)
)

@Composable
fun MatrixSolverTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
