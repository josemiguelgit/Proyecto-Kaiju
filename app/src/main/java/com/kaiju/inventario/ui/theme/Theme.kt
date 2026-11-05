package com.kaiju.inventario.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = KaijuVerde,
    onPrimary = Color.White,
    secondary = KaijuVerdeClaro,
    background = KaijuFondo,
    onBackground = KaijuOscuro,
    surface = KaijuFondo,
    onSurface = KaijuOscuro
)

private val DarkColorScheme = darkColorScheme(
    primary = KaijuVerdeClaro,
    onPrimary = KaijuOscuro,
    secondary = KaijuVerde,
    background = KaijuOscuro,
    onBackground = Color.White
)

@Composable
fun KaijuAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}