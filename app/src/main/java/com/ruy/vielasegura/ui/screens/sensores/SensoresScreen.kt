package com.ruy.vielasegura.ui.screens.sensores

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.ruy.vielasegura.data.local.entity.SensorEntity
import com.ruy.vielasegura.model.NivelAgua
import com.ruy.vielasegura.model.Vielas
import com.ruy.vielasegura.ui.AppViewModelProvider
import com.ruy.vielasegura.ui.components.BarraNivel
import com.ruy.vielasegura.ui.components.CartaoBase
import com.ruy.vielasegura.ui.components.EstadoVazio
import com.ruy.vielasegura.ui.components.NivelBadge
import com.ruy.vielasegura.ui.components.VielaTopBar
import com.ruy.vielasegura.ui.navigation.Mensagens
import com.ruy.vielasegura.util.DataFormatter

@Composable
fun SensoresScreen(
    mensagens: Mensagens,
    viewModel: SensoresViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val sensores by viewModel.sensores.collectAsStateWithLifecycle()
    val simulando by viewModel.simulando.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { VielaTopBar(titulo = "Sensores", subtitulo = Vielas.PADRAO) },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val lista = sensores
            Box(Modifier.weight(1f)) {
                when {
                    lista == null -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    lista.isEmpty() -> EstadoVazio(
                        icone = Icons.Filled.SensorsOff,
                        titulo = "Nenhum sensor encontrado",
                        mensagem = "Ainda não há sensores instalados nesta viela.",
                    )
                    else -> LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(lista, key = { it.id }) { sensor -> SensorCard(sensor) }
                    }
                }
            }
            Button(
                onClick = { viewModel.simularLeitura { mensagens.mostrar(it) } },
                enabled = !simulando && !lista.isNullOrEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(54.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Simular leitura", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        }
    }
}

@Composable
private fun SensorCard(sensor: SensorEntity) {
    val nivel = NivelAgua.deCentimetros(sensor.nivelCm)
    CartaoBase {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    sensor.nome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier.weight(1f),
                )
                NivelBadge(nivel)
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("${sensor.nivelCm}", fontWeight = FontWeight.Bold, fontSize = 34.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    "cm de água",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
            BarraNivel(sensor.nivelCm)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf(NivelAgua.PREPARAR, NivelAgua.ATENCAO, NivelAgua.SAIA_AGORA).forEach {
                    Text(
                        "${it.rotulo} ${it.limiteCm}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "Última leitura ${DataFormatter.diaEHora(sensor.ultimaLeitura).lowercase().replace(", ", " às ")}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
