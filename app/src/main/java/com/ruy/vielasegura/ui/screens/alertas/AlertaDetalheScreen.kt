package com.ruy.vielasegura.ui.screens.alertas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.ruy.vielasegura.data.local.entity.nivel
import com.ruy.vielasegura.data.local.entity.precisaAjuda
import com.ruy.vielasegura.model.NivelAgua
import com.ruy.vielasegura.ui.AppViewModelProvider
import com.ruy.vielasegura.ui.components.CartaoBase
import com.ruy.vielasegura.ui.components.LinhaInfo
import com.ruy.vielasegura.ui.components.VielaTopBar
import com.ruy.vielasegura.ui.navigation.Mensagens
import com.ruy.vielasegura.ui.theme.corDestaque
import com.ruy.vielasegura.ui.theme.corTexto
import com.ruy.vielasegura.util.DataFormatter

@Composable
fun AlertaDetalheScreen(
    mensagens: Mensagens,
    onVoltar: () -> Unit,
    viewModel: AlertaDetalheViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val alerta by viewModel.alerta.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { VielaTopBar(titulo = "Detalhe do alerta", onVoltar = onVoltar) },
        bottomBar = {
            alerta?.let { atual ->
                val estouBem = {
                    viewModel.confirmarEstouBem()
                    mensagens.mostrar("Pronto! Registramos que você está bem.")
                }
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (atual.confirmadoEm == null || atual.precisaAjuda()) {
                        Button(
                            onClick = estouBem,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (atual.precisaAjuda()) "Agora estou bem" else "Estou bem",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                            )
                        }
                    }
                    if (atual.confirmadoEm == null) {
                        BotaoPrecisoDeAjuda(
                            onClick = {
                                viewModel.pedirAjuda()
                                mensagens.mostrar(MENSAGEM_PEDIDO_AJUDA)
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            grande = true,
                        )
                    }
                    if (atual.confirmadoEm != null && !atual.precisaAjuda()) {
                        OutlinedButton(
                            onClick = onVoltar,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                        ) { Text("Voltar", fontWeight = FontWeight.Bold, fontSize = 17.sp) }
                    }
                }
            }
        },
    ) { padding ->
        val atual = alerta
        if (atual == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            DetalheConteudo(atual, Modifier.padding(padding))
        }
    }
}

@Composable
private fun DetalheConteudo(alerta: AlertaEntity, modifier: Modifier) {
    val nivel = alerta.nivel()
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = nivel.corDestaque(), contentColor = nivel.corTexto()),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("NÍVEL DO ALERTA", fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
                Text(nivel.rotulo, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 38.sp)
                Text(alerta.titulo + ".")
            }
        }

        CartaoBase {
            Column(Modifier.padding(16.dp)) {
                LinhaInfo("Sensor", alerta.sensorNome)
                LinhaInfo("Leitura", "${alerta.leituraCm} cm")
                LinhaInfo("Horário", DataFormatter.diaEHora(alerta.criadoEm))
                LinhaInfo(
                    "Status",
                    when {
                        alerta.confirmadoEm == null -> "Aguardando resposta"
                        alerta.precisaAjuda() -> "Pedido de ajuda às ${DataFormatter.hora(alerta.confirmadoEm)}"
                        else -> "Confirmado às ${DataFormatter.hora(alerta.confirmadoEm)}"
                    },
                )
            }
        }

        if (alerta.precisaAjuda()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Seu pedido de ajuda foi registrado", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Se a situação for grave, não espere: ligue 193 (Bombeiros) ou 199 (Defesa Civil).")
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Protótipo: o pedido fica salvo neste celular e não é enviado a nenhum serviço.",
                        fontSize = 13.sp,
                    )
                }
            }
        }

        CartaoBase {
            Column(Modifier.padding(16.dp)) {
                Text("Como agir agora", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(8.dp))
                passosPara(nivel).forEachIndexed { indice, passo ->
                    Row(Modifier.padding(vertical = 3.dp)) {
                        Text(
                            "${indice + 1}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.width(20.dp),
                        )
                        Text(passo)
                    }
                }
            }
        }
    }
}

private fun passosPara(nivel: NivelAgua): List<String> = when (nivel) {
    NivelAgua.NORMAL, NivelAgua.PREPARAR -> listOf(
        "Guarde documentos e remédios em um saco plástico.",
        "Deixe o celular carregado.",
        "Acompanhe as próximas atualizações do app.",
    )
    NivelAgua.ATENCAO -> listOf(
        "Separe documentos, celular carregado e remédios.",
        "Avise os vizinhos e quem mora com você.",
        "Fique atento às próximas atualizações do app.",
    )
    NivelAgua.SAIA_AGORA -> listOf(
        "Saia de casa e vá para um lugar alto e seguro.",
        "Ajude idosos, crianças e pessoas com deficiência.",
        "Em emergência, ligue 199 (Defesa Civil) ou 193 (Bombeiros).",
    )
}
