package com.ruy.vielasegura.ui.screens.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ruy.vielasegura.model.NivelAgua
import com.ruy.vielasegura.ui.AppViewModelProvider
import com.ruy.vielasegura.ui.components.CartaoBase
import com.ruy.vielasegura.ui.components.VielaTopBar
import com.ruy.vielasegura.ui.navigation.Rotas
import com.ruy.vielasegura.ui.theme.AmareloSuave
import com.ruy.vielasegura.ui.theme.AzulEscuro
import com.ruy.vielasegura.ui.theme.AzulSuave
import com.ruy.vielasegura.ui.theme.PetroleoClaro
import com.ruy.vielasegura.ui.theme.corDestaque
import com.ruy.vielasegura.ui.theme.corTexto

@Composable
fun InicioScreen(
    onAbrirAlerta: (Long) -> Unit,
    onIrPara: (String) -> Unit,
    viewModel: InicioViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { VielaTopBar(titulo = "Viela Segura", icone = Icons.Outlined.LocationOn) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Olá! Esta é a situação da sua viela agora.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            CartaoSituacao(
                estado = estado,
                onAcao = {
                    val alertaId = estado.alertaPendenteId
                    when {
                        alertaId != null -> onAbrirAlerta(alertaId)
                        estado.nivel == NivelAgua.NORMAL -> onIrPara(Rotas.SENSORES)
                        else -> onIrPara(Rotas.ALERTAS)
                    }
                },
            )

            AtalhoCard(
                icone = Icons.Filled.Waves,
                corIcone = PetroleoClaro,
                titulo = "Sensores",
                subtitulo = "${estado.totalSensores} sensores monitorando a viela",
                onClick = { onIrPara(Rotas.SENSORES) },
            )
            AtalhoCard(
                icone = Icons.Outlined.NotificationsNone,
                corIcone = AmareloSuave,
                titulo = "Alertas",
                subtitulo = when {
                    estado.alertasPendentes == 1 -> "1 alerta esperando sua resposta"
                    estado.alertasPendentes > 1 -> "${estado.alertasPendentes} alertas esperando sua resposta"
                    estado.pedidosAjuda > 0 -> "Pedido de ajuda registrado"
                    else -> "Nenhum alerta esperando resposta"
                },
                onClick = { onIrPara(Rotas.ALERTAS) },
            )
            AtalhoCard(
                icone = Icons.Outlined.LocationOn,
                corIcone = AzulSuave,
                titulo = "Meu endereço",
                subtitulo = when (estado.totalCasas) {
                    0 -> "Nenhuma casa cadastrada. Toque para cadastrar"
                    1 -> "Casa ${estado.codigoCasa} cadastrada"
                    else -> "${estado.totalCasas} casas cadastradas"
                },
                onClick = { onIrPara(Rotas.CASAS) },
            )
        }
    }
}

@Composable
private fun CartaoSituacao(estado: InicioUiState, onAcao: () -> Unit) {
    val nivel = estado.nivel
    val local = estado.sensorCritico?.local ?: "na viela"
    val (titulo, mensagem) = when (nivel) {
        NivelAgua.NORMAL -> "Tudo tranquilo" to "Os sensores não mostram risco de alagamento agora."
        NivelAgua.PREPARAR -> "Preparar" to "O nível da água começou a subir $local. Fique de olho."
        NivelAgua.ATENCAO -> "Atenção" to "A água está subindo $local. Fique alerta e separe seus documentos."
        NivelAgua.SAIA_AGORA -> "Saia agora" to "A água está muito alta $local. Vá para um lugar alto e seguro."
    }
    val botao = when {
        estado.alertaPendenteId != null -> "Ver alerta"
        nivel == NivelAgua.NORMAL -> "Ver sensores"
        else -> "Ver alertas"
    }
    val corTexto = nivel.corTexto()

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = nivel.corDestaque(), contentColor = corTexto),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                estado.viela.uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
            )
            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 38.sp)
            Spacer(Modifier.height(4.dp))
            Text(mensagem, fontSize = 16.sp)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onAcao,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (nivel == NivelAgua.SAIA_AGORA) Color.White else corTexto,
                    contentColor = if (nivel == NivelAgua.SAIA_AGORA) nivel.corDestaque() else Color.White,
                ),
            ) {
                Text(botao, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AtalhoCard(
    icone: ImageVector,
    corIcone: Color,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit,
) {
    CartaoBase(onClick = onClick) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(corIcone),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icone, contentDescription = null, tint = AzulEscuro)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(subtitulo, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            }
        }
    }
}
