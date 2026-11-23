package com.ruy.vielasegura.ui.screens.casa

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ruy.vielasegura.model.Vielas
import com.ruy.vielasegura.ui.AppViewModelProvider
import com.ruy.vielasegura.ui.components.VielaTopBar
import com.ruy.vielasegura.ui.navigation.Mensagens

@Composable
fun CasaFormScreen(
    mensagens: Mensagens,
    onVoltar: () -> Unit,
    viewModel: CasaFormViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            VielaTopBar(
                titulo = if (estado.editando) "Editar casa" else "Cadastrar casa",
                onVoltar = onVoltar,
            )
        },
        bottomBar = {
            Row(
                Modifier
                    .imePadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onVoltar,
                    modifier = Modifier.weight(1f).height(54.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                ) { Text("Cancelar", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                Button(
                    onClick = {
                        viewModel.salvar { codigo, editou ->
                            mensagens.mostrar(
                                if (editou) "Casa $codigo atualizada" else "Casa salva com o código $codigo"
                            )
                            onVoltar()
                        }
                    },
                    enabled = !estado.salvando,
                    modifier = Modifier.weight(1f).height(54.dp),
                ) { Text("Salvar", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Rotulo("Apelido da casa *")
            OutlinedTextField(
                value = estado.apelido,
                onValueChange = viewModel::onApelidoChange,
                placeholder = { Text("Ex.: Casa da Dona Maria") },
                isError = estado.erroApelido != null,
                supportingText = estado.erroApelido?.let { { MensagemErro(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next,
                ),
                shape = RoundedCornerShape(12.dp),
                colors = coresCampo(),
                modifier = Modifier.fillMaxWidth(),
            )

            Rotulo("Viela")
            SeletorViela(selecionada = estado.viela, onSelecionar = viewModel::onVielaChange)

            Rotulo("Referência para chegar")
            OutlinedTextField(
                value = estado.referencia,
                onValueChange = viewModel::onReferenciaChange,
                placeholder = { Text("Ex.: Portão azul, ao lado do bar do Seu Jorge") },
                minLines = 2,
                maxLines = 4,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                shape = RoundedCornerShape(12.dp),
                colors = coresCampo(),
                modifier = Modifier.fillMaxWidth(),
            )

            Rotulo("Número de moradores *")
            OutlinedTextField(
                value = estado.moradores,
                onValueChange = viewModel::onMoradoresChange,
                placeholder = { Text("Ex.: 4") },
                isError = estado.erroMoradores != null,
                supportingText = estado.erroMoradores?.let { { MensagemErro(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                shape = RoundedCornerShape(12.dp),
                colors = coresCampo(),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(4.dp))
            Text(
                if (estado.editando) {
                    "Código atual: ${estado.codigo}. Se mudar a viela, o código é atualizado ao salvar."
                } else {
                    "O código da casa, como VC-012, é gerado quando você salva."
                },
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeletorViela(selecionada: String, onSelecionar: (String) -> Unit) {
    var aberto by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = aberto, onExpandedChange = { aberto = it }) {
        OutlinedTextField(
            value = selecionada,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = aberto) },
            shape = RoundedCornerShape(12.dp),
            colors = coresCampo(),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            Vielas.todas.forEach { viela ->
                DropdownMenuItem(
                    text = { Text(viela) },
                    onClick = {
                        onSelecionar(viela)
                        aberto = false
                    },
                )
            }
        }
    }
}

@Composable
private fun Rotulo(texto: String) {
    Text(texto, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
}

@Composable
private fun MensagemErro(texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.ErrorOutline, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(texto, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun coresCampo() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    errorContainerColor = MaterialTheme.colorScheme.surface,
)
