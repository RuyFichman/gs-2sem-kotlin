package com.ruy.vielasegura.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.ui.graphics.vector.ImageVector

object Rotas {
    const val INICIO = "inicio"
    const val SENSORES = "sensores"
    const val ALERTAS = "alertas"
    const val CASAS = "casas"

    const val ARG_ALERTA_ID = "alertaId"
    const val ALERTA_DETALHE = "alerta/{$ARG_ALERTA_ID}"
    fun alertaDetalhe(id: Long) = "alerta/$id"

    const val ARG_CASA_ID = "casaId"
    const val SEM_ID = -1L
    const val CASA_FORM = "casa/form?$ARG_CASA_ID={$ARG_CASA_ID}"
    fun casaForm(id: Long? = null) = if (id == null) "casa/form" else "casa/form?$ARG_CASA_ID=$id"
}

/** Abas da barra inferior. */
enum class Aba(val rota: String, val rotulo: String, val icone: ImageVector) {
    INICIO(Rotas.INICIO, "Início", Icons.Outlined.Home),
    SENSORES(Rotas.SENSORES, "Sensores", Icons.Filled.Waves),
    ALERTAS(Rotas.ALERTAS, "Alertas", Icons.Outlined.NotificationsNone),
    ENDERECO(Rotas.CASAS, "Endereço", Icons.Outlined.LocationOn),
}
