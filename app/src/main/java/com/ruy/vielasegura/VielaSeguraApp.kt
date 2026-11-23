package com.ruy.vielasegura

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VielaSeguraApp : Application() {

    lateinit var container: AppContainer
        private set

    private val escopo = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        escopo.launch { container.monitoramentoRepository.popularDadosIniciais() }
    }
}
