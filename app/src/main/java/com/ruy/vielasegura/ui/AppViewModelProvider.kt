package com.ruy.vielasegura.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ruy.vielasegura.VielaSeguraApp
import com.ruy.vielasegura.ui.screens.alertas.AlertaDetalheViewModel
import com.ruy.vielasegura.ui.screens.alertas.AlertasViewModel
import com.ruy.vielasegura.ui.screens.casa.CasaFormViewModel
import com.ruy.vielasegura.ui.screens.casa.CasasViewModel
import com.ruy.vielasegura.ui.screens.inicio.InicioViewModel
import com.ruy.vielasegura.ui.screens.sensores.SensoresViewModel

/** Fábrica única que entrega os repositórios do [com.ruy.vielasegura.AppContainer] para cada ViewModel. */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            InicioViewModel(app().container.monitoramentoRepository, app().container.casaRepository)
        }
        initializer { SensoresViewModel(app().container.monitoramentoRepository) }
        initializer { AlertasViewModel(app().container.monitoramentoRepository) }
        initializer {
            AlertaDetalheViewModel(createSavedStateHandle(), app().container.monitoramentoRepository)
        }
        initializer { CasasViewModel(app().container.casaRepository) }
        initializer { CasaFormViewModel(createSavedStateHandle(), app().container.casaRepository) }
    }
}

private fun CreationExtras.app(): VielaSeguraApp = this[APPLICATION_KEY] as VielaSeguraApp
