package com.ruy.vielasegura.ui.screens.alertas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ruy.vielasegura.data.local.entity.AlertaEntity
import com.ruy.vielasegura.data.local.entity.estaConfirmado
import com.ruy.vielasegura.data.local.entity.nivel
import com.ruy.vielasegura.data.local.entity.precisaAjuda
import com.ruy.vielasegura.model.Vielas
import com.ruy.vielasegura.ui.AppViewModelProvider
import com.ruy.vielasegura.ui.components.CartaoBase
import com.ruy.vielasegura.ui.components.EstadoVazio
import com.ruy.vielasegura.ui.components.NivelBadge
import com.ruy.vielasegura.ui.components.VielaTopBar
import com.ruy.vielasegura.ui.navigation.Mensagens
import com.ruy.vielasegura.ui.theme.corForte
import com.ruy.vielasegura.util.DataFormatter

@Composable
fun AlertasScreen(
    mensagens: Mensagens,
    onVerDetalhes: (Long) -> Unit,
    viewModel: AlertasViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val alertas by viewModel.alertas.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { VielaTopBar(titulo = "Alertas", subtitulo = Vielas.PADRAO) },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val lista = alertas
            when {
                lista == null -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                lista.isEmpty() -> EstadoVazio(
                    icone = Icons.Outlined.NotificationsOff,
                    titulo = "Nenhum alerta por aqui",
                    mensagem = "Quando a água subir na viela, os alertas aparecem nesta tela.",
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(lista, key = { it.id }) { alerta ->
                        AlertaCard(
                            alerta = alerta,
                            onVerDetalhes = { onVerDetalhes(alerta.id) },
                            onEstouBem = {
                                viewModel.confirmarEstouBem(alerta.id)
                                mensagens.mostrar("Pronto! Registramos que você está bem.")
                            },
                            onPedirAjuda = {
                                viewModel.pedirAjuda(alerta.id)
                                mensagens.mostrar(MENSAGEM_PEDIDO_AJUDA)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlertaCard(
    alerta: AlertaEntity,
    onVerDetalhes: () -> Unit,
    onEstouBem: () -> Unit,
    onPedirAjuda: () -> Unit,
) {
    val nivel = alerta.nivel()
    val pendente = !alerta.estaConfirmado()
    val pediuAjuda = alerta.precisaAjuda()
    CartaoBase(
        onClick = onVerDetalhes,
        borda = when {
            pendente -> BorderStroke(2.dp, nivel.corForte())
            pediuAjuda -> BorderStroke(2.dp, MaterialTheme.colorScheme.error)
            else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        },
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NivelBadge(nivel)
                Spacer(Modifier.weight(1f))
                Text(
                    DataFormatter.diaEHora(alerta.criadoEm),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(alerta.titulo, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(Modifier.height(6.dp))
            Text(alerta.descricao, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            when {
                pendente -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onEstouBem,
                            modifier = Modifier.weight(1f).height(48.dp),
                        ) { Text("Estou bem", fontWeight = FontWeight.Bold) }
                        BotaoPrecisoDeAjuda(onClick = onPedirAjuda, modifier = Modifier.weight(1f).height(48.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onVerDetalhes,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    ) { Text("Ver detalhes", fontWeight = FontWeight.Bold) }
                }
                pediuAjuda -> {
                    RespostaTexto(alerta)
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onEstouBem,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    ) { Text("Agora estou bem", fontWeight = FontWeight.Bold) }
                }
                else -> RespostaTexto(alerta)
            }
        }
    }
}

/** Mensagem exibida ao pedir ajuda. O protótipo só registra o pedido; não há envio real. */
const val MENSAGEM_PEDIDO_AJUDA =
    "Pedido de ajuda registrado. Em emergência, ligue 193 (Bombeiros) ou 199 (Defesa Civil)."

@Composable
fun BotaoPrecisoDeAjuda(onClick: () -> Unit, modifier: Modifier = Modifier, grande: Boolean = false) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
        ),
    ) {
        if (grande) {
            Icon(Icons.Filled.Warning, contentDescription = null)
            Spacer(Modifier.width(8.dp))
        }
        Text(
            "Preciso de ajuda",
            fontWeight = FontWeight.Bold,
            fontSize = if (grande) 17.sp else 14.sp,
            maxLines = 1,
        )
    }
}

/** Status da resposta: "Confirmado às HH:mm" ou "Pedido de ajuda às HH:mm". */
@Composable
fun RespostaTexto(alerta: AlertaEntity) {
    val horario = alerta.confirmadoEm ?: return
    val ajuda = alerta.precisaAjuda()
    val cor = if (ajuda) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            if (ajuda) Icons.Filled.Warning else Icons.Filled.Check,
            contentDescription = null,
            tint = cor,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            if (ajuda) "Pedido de ajuda às ${DataFormatter.hora(horario)}" else "Confirmado às ${DataFormatter.hora(horario)}",
            color = cor,
            fontWeight = FontWeight.Bold,
        )
    }
}
