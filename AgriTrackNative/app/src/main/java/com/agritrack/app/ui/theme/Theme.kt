
package com.agritrack.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta de colores para el tema oscuro
private val DarkColorScheme = darkColorScheme(
    primary = Green80,       // Un verde más brillante para el modo oscuro
    secondary = Brown80,     // Un marrón complementario
    tertiary = GreenGrey80,
    background = DarkGrey,   // Fondo oscuro pero no negro puro
    surface = LightDarkGrey  // Superficie de las tarjetas ligeramente más clara
)

// Paleta de colores para el tema claro
private val LightColorScheme = lightColorScheme(
    primary = Green40,       // El verde principal de la marca
    secondary = Brown40,     // Marrón para acentos
    tertiary = GreenGrey40,
    background = Grey99,     // Un fondo gris muy claro
    surface = GreenGrey95    // Un tinte verde/gris para las tarjetas
)

@Composable
fun AgriTrackNativeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
        typography = Typography, // Usará la tipografía por defecto de Material 3
        content = content
    )
}
