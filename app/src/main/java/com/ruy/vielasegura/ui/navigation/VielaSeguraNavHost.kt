package com.ruy.vielasegura.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ruy.vielasegura.ui.screens.alertas.AlertaDetalheScreen
import com.ruy.vielasegura.ui.screens.alertas.AlertasScreen
import com.ruy.vielasegura.ui.screens.casa.CasaFormScreen
import com.ruy.vielasegura.ui.screens.casa.CasasScreen
import com.ruy.vielasegura.ui.screens.inicio.InicioScreen
import com.ruy.vielasegura.ui.screens.sensores.SensoresScreen

/** Troca de aba sem empilhar telas repetidas e preservando o estado de cada aba. */
fun NavHostController.navegarParaAba(rota: String) {
    navigate(rota) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun VielaSeguraNavHost() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val escopo = rememberCoroutineScope()
    val mensagens = remember { Mensagens(snackbarHostState, escopo) }

    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route
    val mostrarBarra = Aba.entries.any { it.rota == rotaAtual }
    // A tela de casas tem botão flutuante: ela mesma exibe o Snackbar para que ele fique acima do botão.
    val telaExibeSnackbar = rotaAtual == Rotas.CASAS

    Scaffold(
        snackbarHost = { if (!telaExibeSnackbar) SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (mostrarBarra) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    Aba.entries.forEach { aba ->
                        val selecionada = rotaAtual == aba.rota
                        NavigationBarItem(
                            selected = selecionada,
                            onClick = { navController.navegarParaAba(aba.rota) },
                            icon = { Icon(aba.icone, contentDescription = null) },
                            label = {
                                Text(
                                    aba.rotulo,
                                    fontWeight = if (selecionada) FontWeight.Bold else FontWeight.Normal,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        // Só o espaço inferior (barra de navegação) é aplicado aqui; cada tela cuida da própria barra superior.
        val inferior = PaddingValues(bottom = padding.calculateBottomPadding())
        NavHost(
            navController = navController,
            startDestination = Rotas.INICIO,
            modifier = Modifier
                .padding(inferior)
                .consumeWindowInsets(inferior),
        ) {
            composable(Rotas.INICIO) {
                InicioScreen(
                    onAbrirAlerta = { id -> navController.navigate(Rotas.alertaDetalhe(id)) },
                    onIrPara = { rota -> navController.navegarParaAba(rota) },
                )
            }
            composable(Rotas.SENSORES) {
                SensoresScreen(mensagens = mensagens)
            }
            composable(Rotas.ALERTAS) {
                AlertasScreen(
                    mensagens = mensagens,
                    onVerDetalhes = { id -> navController.navigate(Rotas.alertaDetalhe(id)) },
                )
            }
            composable(
                route = Rotas.ALERTA_DETALHE,
                arguments = listOf(navArgument(Rotas.ARG_ALERTA_ID) { type = NavType.LongType }),
            ) {
                AlertaDetalheScreen(
                    mensagens = mensagens,
                    onVoltar = { navController.popBackStack() },
                )
            }
            composable(Rotas.CASAS) {
                CasasScreen(
                    mensagens = mensagens,
                    onCadastrar = { navController.navigate(Rotas.casaForm()) },
                    onEditar = { id -> navController.navigate(Rotas.casaForm(id)) },
                )
            }
            composable(
                route = Rotas.CASA_FORM,
                arguments = listOf(
                    navArgument(Rotas.ARG_CASA_ID) {
                        type = NavType.LongType
                        defaultValue = Rotas.SEM_ID
                    }
                ),
            ) {
                CasaFormScreen(
                    mensagens = mensagens,
                    onVoltar = { navController.popBackStack() },
                )
            }
        }
    }
}
