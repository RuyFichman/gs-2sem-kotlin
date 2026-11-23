package com.ruy.vielasegura.ui.screens.alertas

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruy.vielasegura.data.local.entity.AlertaEntity
import com.ruy.vielasegura.data.repository.MonitoramentoRepository
import com.ruy.vielasegura.ui.navigation.Rotas
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertaDetalheViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: MonitoramentoRepository,
) : ViewModel() {

    private val alertaId: Long = checkNotNull(savedStateHandle[Rotas.ARG_ALERTA_ID])

    val alerta: StateFlow<AlertaEntity?> = repository.observarAlerta(alertaId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun confirmarEstouBem() {
        viewModelScope.launch { repository.confirmarEstouBem(alertaId) }
    }

    fun pedirAjuda() {
        viewModelScope.launch { repository.pedirAjuda(alertaId) }
    }
}
