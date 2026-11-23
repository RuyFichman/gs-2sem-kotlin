package com.ruy.vielasegura

import android.content.Context
import com.ruy.vielasegura.data.local.AppDatabase
import com.ruy.vielasegura.data.repository.CasaRepository
import com.ruy.vielasegura.data.repository.MonitoramentoRepository

/** Injeção de dependências manual: cria o banco e os repositórios uma única vez. */
class AppContainer(context: Context) {
    private val database = AppDatabase.obter(context)

    val casaRepository = CasaRepository(database.casaDao())
    val monitoramentoRepository = MonitoramentoRepository(database.sensorDao(), database.alertaDao())
}
