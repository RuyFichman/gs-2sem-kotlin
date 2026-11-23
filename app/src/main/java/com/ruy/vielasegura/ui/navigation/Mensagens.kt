package com.ruy.vielasegura.ui.navigation

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Exibe mensagens de retorno (Snackbar) para as ações do usuário. Fica no nível do app,
 * então a mensagem continua visível mesmo depois de trocar de tela (ex.: após salvar a casa).
 */
class Mensagens(
    val hostState: SnackbarHostState,
    private val escopo: CoroutineScope,
) {
    fun mostrar(texto: String, acao: String? = null, onAcao: () -> Unit = {}) {
        escopo.launch {
            hostState.currentSnackbarData?.dismiss()
            val resultado = hostState.showSnackbar(
                message = texto,
                actionLabel = acao,
                duration = if (acao != null) SnackbarDuration.Long else SnackbarDuration.Short,
            )
            if (resultado == SnackbarResult.ActionPerformed) onAcao()
        }
    }
}
