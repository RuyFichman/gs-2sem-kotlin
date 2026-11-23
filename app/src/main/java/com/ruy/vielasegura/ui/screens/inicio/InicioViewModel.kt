package com.ruy.vielasegura.ui.screens.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruy.vielasegura.data.local.entity.SensorEntity
import com.ruy.vielasegura.data.local.entity.estaConfirmado
import com.ruy.vielasegura.data.local.entity.precisaAjuda
import com.ruy.vielasegura.data.repository.CasaRepository
import com.ruy.vielasegura.data.repository.MonitoramentoRepository
import com.ruy.vielasegura.model.NivelAgua
import com.ruy.vielasegura.model.Vielas
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class InicioUiState(
    val viela: String = Vielas.PADRAO,
    val nivel: NivelAgua = NivelAgua.NORMAL,
    val sensorCritico: SensorEntity? = null,
    val totalSensores: Int = 0,
    val alertasPendentes: Int = 0,
    val alertaPendenteId: Long? = null,
    val pedidosAjuda: Int = 0,
    val totalCasas: Int = 0,
    val codigoCasa: String? = null,
)

class InicioViewModel(
    monitoramentoRepository: MonitoramentoRepository,
    casaRepository: CasaRepository,
) : ViewModel() {

    val uiState: StateFlow<InicioUiState> = combine(
        monitoramentoRepository.observarSensores(),
        monitoramentoRepository.observarAlertas(),
        casaRepository.observarCasas(),
    ) { sensores, alertas, casas ->
        val critico = sensores.maxByOrNull { it.nivelCm }
        val pendentes = alertas.filterNot { it.estaConfirmado() }
        InicioUiState(
            viela = critico?.viela ?: Vielas.PADRAO,
            nivel = critico?.let { NivelAgua.deCentimetros(it.nivelCm) } ?: NivelAgua.NORMAL,
            sensorCritico = critico,
            totalSensores = sensores.size,
            alertasPendentes = pendentes.size,
            alertaPendenteId = pendentes.firstOrNull()?.id,
            pedidosAjuda = alertas.count { it.precisaAjuda() },
            totalCasas = casas.size,
            codigoCasa = casas.firstOrNull()?.codigo,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InicioUiState())
}
