package com.ruy.vielasegura.ui.screens.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruy.vielasegura.data.local.entity.AlertaEntity
import com.ruy.vielasegura.data.repository.MonitoramentoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertasViewModel(
    private val repository: MonitoramentoRepository,
) : ViewModel() {

    /** null enquanto carrega; lista vazia = estado sem dados. */
    val alertas: StateFlow<List<AlertaEntity>?> = repository.observarAlertas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun confirmarEstouBem(alertaId: Long) {
        viewModelScope.launch { repository.confirmarEstouBem(alertaId) }
    }

    fun pedirAjuda(alertaId: Long) {
        viewModelScope.launch { repository.pedirAjuda(alertaId) }
    }
}
