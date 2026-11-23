package com.ruy.vielasegura.ui.screens.casa

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruy.vielasegura.data.local.entity.CasaEntity
import com.ruy.vielasegura.data.repository.CasaRepository
import com.ruy.vielasegura.model.Vielas
import com.ruy.vielasegura.ui.navigation.Rotas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CasaFormUiState(
    val id: Long = 0,
    val codigo: String = "",
    val apelido: String = "",
    val viela: String = Vielas.PADRAO,
    val referencia: String = "",
    val moradores: String = "",
    val erroApelido: String? = null,
    val erroMoradores: String? = null,
    val salvando: Boolean = false,
) {
    val editando: Boolean get() = id != 0L
}

class CasaFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: CasaRepository,
) : ViewModel() {

    private val casaId: Long = savedStateHandle[Rotas.ARG_CASA_ID] ?: Rotas.SEM_ID

    private val _uiState = MutableStateFlow(CasaFormUiState())
    val uiState: StateFlow<CasaFormUiState> = _uiState.asStateFlow()

    init {
        if (casaId != Rotas.SEM_ID) {
            viewModelScope.launch {
                repository.buscar(casaId)?.let { casa ->
                    _uiState.value = CasaFormUiState(
                        id = casa.id,
                        codigo = casa.codigo,
                        apelido = casa.apelido,
                        viela = casa.viela,
                        referencia = casa.referencia,
                        moradores = casa.moradores.toString(),
                    )
                }
            }
        }
    }

    fun onApelidoChange(valor: String) =
        _uiState.update { it.copy(apelido = valor.take(40), erroApelido = null) }

    fun onVielaChange(valor: String) = _uiState.update { it.copy(viela = valor) }

    fun onReferenciaChange(valor: String) = _uiState.update { it.copy(referencia = valor.take(140)) }

    fun onMoradoresChange(valor: String) =
        _uiState.update { it.copy(moradores = valor.filter(Char::isDigit).take(2), erroMoradores = null) }

    /** Valida os campos obrigatórios. Retorna true se o formulário pode ser salvo. */
    private fun validar(): Boolean {
        val estado = _uiState.value
        val erroApelido = when {
            estado.apelido.isBlank() -> "Informe o apelido da casa"
            estado.apelido.trim().length < 3 -> "O apelido precisa ter pelo menos 3 letras"
            else -> null
        }
        val moradores = estado.moradores.toIntOrNull()
        val erroMoradores = when {
            moradores == null -> "Informe quantas pessoas moram na casa"
            moradores < 1 -> "A casa precisa ter pelo menos 1 morador"
            else -> null
        }
        _uiState.update { it.copy(erroApelido = erroApelido, erroMoradores = erroMoradores) }
        return erroApelido == null && erroMoradores == null
    }

    /** Salva no Room e devolve o código da casa no [onSalvo]. */
    fun salvar(onSalvo: (codigo: String, editou: Boolean) -> Unit) {
        if (_uiState.value.salvando || !validar()) return
        val estado = _uiState.value
        _uiState.update { it.copy(salvando = true) }
        viewModelScope.launch {
            val codigo = repository.salvar(
                CasaEntity(
                    id = estado.id,
                    apelido = estado.apelido.trim(),
                    viela = estado.viela,
                    referencia = estado.referencia.trim(),
                    moradores = estado.moradores.toInt(),
                )
            )
            _uiState.update { it.copy(salvando = false) }
            onSalvo(codigo, estado.editando)
        }
    }
}
