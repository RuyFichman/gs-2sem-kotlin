package com.ruy.vielasegura.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Cores = lightColorScheme(
    primary = Petroleo,
    onPrimary = Color.White,
    primaryContainer = PetroleoClaro,
    onPrimaryContainer = Color(0xFF0B4F4B),
    secondary = Petroleo,
    onSecondary = Color.White,
    secondaryContainer = PetroleoClaro,
    onSecondaryContainer = Color(0xFF0B4F4B),
    background = Fundo,
    onBackground = TextoPrincipal,
    surface = Superficie,
    onSurface = TextoPrincipal,
    onSurfaceVariant = TextoSecundario,
    outline = Color(0xFF9AA5A4),
    outlineVariant = Borda,
    error = Erro,
)

@Composable
fun VielaSeguraTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Cores, content = content)
}
