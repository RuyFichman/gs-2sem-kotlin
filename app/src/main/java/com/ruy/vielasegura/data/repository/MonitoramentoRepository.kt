package com.ruy.vielasegura.data.repository

import com.ruy.vielasegura.data.local.dao.AlertaDao
import com.ruy.vielasegura.data.local.dao.SensorDao
import com.ruy.vielasegura.data.local.entity.AlertaEntity
import com.ruy.vielasegura.data.local.entity.SensorEntity
import com.ruy.vielasegura.model.NivelAgua
import com.ruy.vielasegura.model.Vielas
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

/** Resultado de uma leitura simulada, usado para montar a mensagem exibida ao usuário. */
data class LeituraSimulada(
    val sensor: SensorEntity,
    val nivelAnteriorCm: Int,
    val alertaGerado: Boolean,
)

/**
 * Sensores e alertas. No protótipo os sensores são simulados: não há integração com
 * hardware ou serviço externo. Leituras e alertas ficam gravados no Room.
 */
class MonitoramentoRepository(
    private val sensorDao: SensorDao,
    private val alertaDao: AlertaDao,
) {

    fun observarSensores(): Flow<List<SensorEntity>> = sensorDao.observarTodos()

    fun observarAlertas(): Flow<List<AlertaEntity>> = alertaDao.observarTodos()

    fun observarAlerta(id: Long): Flow<AlertaEntity?> = alertaDao.observarPorId(id)

    suspend fun confirmarEstouBem(alertaId: Long) {
        alertaDao.responder(alertaId, System.currentTimeMillis(), pediuAjuda = false)
    }

    /** No protótipo o pedido fica registrado no Room; não há envio real para Defesa Civil ou vizinhos. */
    suspend fun pedirAjuda(alertaId: Long) {
        alertaDao.responder(alertaId, System.currentTimeMillis(), pediuAjuda = true)
    }

    /** Sorteia um sensor e gera uma nova leitura. Se o nível de risco subir, cria um alerta. */
    suspend fun simularLeitura(): LeituraSimulada? {
        val sensores = sensorDao.listar()
        if (sensores.isEmpty()) return null

        val sensor = sensores.random()
        val novoNivel = (sensor.nivelCm + Random.nextInt(-10, 21)).coerceIn(0, 80)
        val agora = System.currentTimeMillis()
        val atualizado = sensor.copy(nivelCm = novoNivel, ultimaLeitura = agora)
        sensorDao.atualizar(atualizado)

        val riscoAntes = NivelAgua.deCentimetros(sensor.nivelCm)
        val riscoAgora = NivelAgua.deCentimetros(novoNivel)
        val gerarAlerta = riscoAgora != NivelAgua.NORMAL && riscoAgora > riscoAntes
        if (gerarAlerta) {
            alertaDao.inserir(criarAlerta(atualizado, novoNivel, agora))
        }
        return LeituraSimulada(atualizado, sensor.nivelCm, gerarAlerta)
    }

    /** Dados iniciais simulados (sensores e histórico de alertas), inseridos só na primeira execução. */
    suspend fun popularDadosIniciais() {
        if (sensorDao.contar() > 0) return

        val agora = System.currentTimeMillis()
        val minuto = 60_000L
        val bueiro = SensorEntity(
            nome = "Bueiro da entrada", local = "no bueiro da entrada",
            viela = Vielas.PADRAO, nivelCm = 45, ultimaLeitura = agora - 2 * minuto,
        )
        val fimDaViela = SensorEntity(
            nome = "Fim da viela", local = "no fim da viela",
            viela = Vielas.PADRAO, nivelCm = 22, ultimaLeitura = agora - 4 * minuto,
        )
        val escadaria = SensorEntity(
            nome = "Escadaria", local = "na escadaria",
            viela = Vielas.PADRAO, nivelCm = 12, ultimaLeitura = agora - 5 * minuto,
        )
        sensorDao.inserirTodos(listOf(bueiro, fimDaViela, escadaria))

        val ontem = agora - 19 * 60 * minuto
        alertaDao.inserirTodos(
            listOf(
                criarAlerta(fimDaViela, 21, ontem).copy(confirmadoEm = ontem + 4 * minuto),
                criarAlerta(fimDaViela, 22, agora - 29 * minuto).copy(confirmadoEm = agora - 26 * minuto),
                criarAlerta(bueiro, 45, agora - 2 * minuto),
            )
        )
    }

    private fun criarAlerta(sensor: SensorEntity, cm: Int, quando: Long): AlertaEntity {
        val (titulo, complemento) = when (NivelAgua.deCentimetros(cm)) {
            NivelAgua.SAIA_AGORA ->
                "Água muito alta ${sensor.local}" to " Saia agora e vá para um lugar alto."
            NivelAgua.ATENCAO ->
                "Água subindo ${sensor.local}" to " Fique alerta e separe seus documentos."
            else ->
                "Nível de água começou a subir" to ""
        }
        return AlertaEntity(
            sensorNome = sensor.nome,
            titulo = titulo,
            descricao = "Sensor ${sensor.nome}: $cm cm.$complemento",
            leituraCm = cm,
            criadoEm = quando,
        )
    }
}
