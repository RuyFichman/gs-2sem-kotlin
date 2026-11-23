package com.ruy.vielasegura.ui.screens.casa

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ruy.vielasegura.data.local.entity.CasaEntity
import com.ruy.vielasegura.ui.AppViewModelProvider
import com.ruy.vielasegura.ui.components.CartaoBase
import com.ruy.vielasegura.ui.components.EstadoVazio
import com.ruy.vielasegura.ui.components.VielaTopBar
import com.ruy.vielasegura.ui.navigation.Mensagens
import com.ruy.vielasegura.ui.theme.AzulEscuro
import com.ruy.vielasegura.ui.theme.AzulSuave

@Composable
fun CasasScreen(
    mensagens: Mensagens,
    onCadastrar: () -> Unit,
    onEditar: (Long) -> Unit,
    viewModel: CasasViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val casas by viewModel.casas.collectAsStateWithLifecycle()
    var casaParaExcluir by remember { mutableStateOf<CasaEntity?>(null) }

    Scaffold(
        topBar = { VielaTopBar(titulo = "Meu endereço", subtitulo = "Casas cadastradas neste celular") },
        // O Scaffold posiciona o Snackbar acima do botão flutuante "Cadastrar casa".
        snackbarHost = { SnackbarHost(mensagens.hostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCadastrar,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(50),
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Cadastrar casa", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            )
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val lista = casas
            when {
                lista == null -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                lista.isEmpty() -> EstadoVazio(
                    icone = Icons.Outlined.Home,
                    titulo = "Nenhuma casa cadastrada",
                    mensagem = "Toque em \"Cadastrar casa\" para ligar os alertas da viela à sua casa.",
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(lista, key = { it.id }) { casa ->
                        CasaCard(
                            casa = casa,
                            onEditar = { onEditar(casa.id) },
                            onExcluir = { casaParaExcluir = casa },
                        )
                    }
                    item {
                        Text(
                            "O código da casa é gerado ao salvar e serve para ligar o seu alerta à sua casa.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }

    casaParaExcluir?.let { casa ->
        AlertDialog(
            onDismissRequest = { casaParaExcluir = null },
            title = { Text("Excluir casa?") },
            text = { Text("\"${casa.apelido}\" (${casa.codigo}) será removida deste celular.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.excluir(casa)
                        casaParaExcluir = null
                        mensagens.mostrar("Casa ${casa.codigo} excluída", acao = "Desfazer") {
                            viewModel.desfazerExclusao(casa)
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) { Text("Excluir", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { casaParaExcluir = null }) { Text("Cancelar") }
            },
        )
    }
}

@Composable
private fun CasaCard(casa: CasaEntity, onEditar: () -> Unit, onExcluir: () -> Unit) {
    CartaoBase {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    casa.apelido,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    casa.codigo,
                    color = AzulEscuro,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AzulSuave)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(casa.viela, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (casa.referencia.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(casa.referencia)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                if (casa.moradores == 1) "1 morador" else "${casa.moradores} moradores",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onEditar,
                    modifier = Modifier.weight(1f).height(48.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                ) { Text("Editar", fontWeight = FontWeight.Bold) }
                OutlinedButton(
                    onClick = onExcluir,
                    modifier = Modifier.weight(1f).height(48.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) { Text("Excluir", fontWeight = FontWeight.Bold) }
            }
        }
    }
}
