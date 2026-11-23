package com.ruy.vielasegura.ui.screens.casa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruy.vielasegura.data.local.entity.CasaEntity
import com.ruy.vielasegura.data.repository.CasaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CasasViewModel(
    private val repository: CasaRepository,
) : ViewModel() {

    /** null enquanto carrega; lista vazia = estado sem dados. */
    val casas: StateFlow<List<CasaEntity>?> = repository.observarCasas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun excluir(casa: CasaEntity) {
        viewModelScope.launch { repository.excluir(casa) }
    }

    fun desfazerExclusao(casa: CasaEntity) {
        viewModelScope.launch { repository.restaurar(casa) }
    }
}
