package com.ruy.vielasegura.ui.theme

import androidx.compose.ui.graphics.Color
import com.ruy.vielasegura.model.NivelAgua

val Petroleo = Color(0xFF0E6B66)
val PetroleoClaro = Color(0xFFD7EDEA)
val Fundo = Color(0xFFF2F5F5)
val Superficie = Color(0xFFFFFFFF)
val Borda = Color(0xFFDDE3E3)
val TextoPrincipal = Color(0xFF1B2426)
val TextoSecundario = Color(0xFF55615F)
val Erro = Color(0xFFC62828)
val AmareloSuave = Color(0xFFFFE08A)
val AzulSuave = Color(0xFFDCE7F5)
val AzulEscuro = Color(0xFF26456E)

/** Cor de fundo de cartões e etiquetas do nível. */
fun NivelAgua.corDestaque(): Color = when (this) {
    NivelAgua.NORMAL -> PetroleoClaro
    NivelAgua.PREPARAR -> AmareloSuave
    NivelAgua.ATENCAO -> Color(0xFFFFAD6B)
    NivelAgua.SAIA_AGORA -> Color(0xFFD32F2F)
}

/** Cor do texto sobre [corDestaque]. */
fun NivelAgua.corTexto(): Color = when (this) {
    NivelAgua.NORMAL -> Color(0xFF0B4F4B)
    NivelAgua.PREPARAR -> Color(0xFF4A3B00)
    NivelAgua.ATENCAO -> Color(0xFF3D1F00)
    NivelAgua.SAIA_AGORA -> Color.White
}

/** Cor forte usada em barras e bordas. */
fun NivelAgua.corForte(): Color = when (this) {
    NivelAgua.NORMAL -> Petroleo
    NivelAgua.PREPARAR -> Color(0xFF8A6A00)
    NivelAgua.ATENCAO -> Color(0xFFC2570C)
    NivelAgua.SAIA_AGORA -> Color(0xFFB71C1C)
}
