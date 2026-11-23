package com.ruy.vielasegura.ui.screens.sensores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruy.vielasegura.data.local.entity.SensorEntity
import com.ruy.vielasegura.data.repository.MonitoramentoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SensoresViewModel(
    private val repository: MonitoramentoRepository,
) : ViewModel() {

    val sensores: StateFlow<List<SensorEntity>?> = repository.observarSensores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _simulando = MutableStateFlow(false)
    val simulando: StateFlow<Boolean> = _simulando.asStateFlow()

    /** Gera uma leitura simulada e devolve uma mensagem de retorno para o usuário. */
    fun simularLeitura(onResultado: (String) -> Unit) {
        if (_simulando.value) return
        viewModelScope.launch {
            _simulando.value = true
            val leitura = repository.simularLeitura()
            _simulando.value = false
            val mensagem = when {
                leitura == null -> "Nenhum sensor disponível."
                leitura.alertaGerado ->
                    "${leitura.sensor.nome}: ${leitura.sensor.nivelCm} cm. Novo alerta criado!"
                else ->
                    "${leitura.sensor.nome}: ${leitura.nivelAnteriorCm} cm → ${leitura.sensor.nivelCm} cm"
            }
            onResultado(mensagem)
        }
    }
}
